package com.example.myappchat.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.User;
import com.example.myappchat.utils.RealPathUtil;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    private ImageView imgAvatar;
    private EditText edtUserName, edtUserEmail;
    private TextView tvChangePassword;
    private Button btnSave;
    private ProgressBar progressbarOfUpdateProfile;
    private Uri imagePath;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();


    private static int PICK_IMAGE=123;

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data == null){
                            return;
                        }
                        imagePath = data.getData();
                        imgAvatar.setImageURI(imagePath);

                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        imgAvatar = findViewById(R.id.getuserimg);
        edtUserName = findViewById(R.id.getusername);
        edtUserEmail = findViewById(R.id.getuseremail);
        tvChangePassword = findViewById(R.id.changePasswordTv);
        btnSave = findViewById(R.id.saveProfile);
        progressbarOfUpdateProfile = findViewById(R.id.progressbarofsetProfile);

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                startActivityForResult(intent, PICK_IMAGE);
                RequestPermission();
            }
        });
        imgAvatar.setEnabled(false);

        setUserInfoToView();
        setBtnOnChange();

    }

    private void RequestPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery();
            return;
        }

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        } else {
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void setUserInfoToView(){
        edtUserName.setEnabled(false);
        edtUserEmail.setEnabled(false);

        ApiService.apiService.getMyInfo("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    User myInfo = response.body().getUser();
                    edtUserName.setText(myInfo.getUsername());
                    edtUserEmail.setText(myInfo.getEmail());
                    if(myInfo.getAvatar().isEmpty()){
                        myInfo.setAvatar("abcd");
                    }
                    Picasso.with(getApplicationContext())
                            .load(env.getUrlAvatar() + myInfo.getAvatar())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(imgAvatar);
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }


    private void setBtnSave(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(edtUserName.getText().toString().equals(env.getUser().getUsername()) && edtUserEmail.getText().toString().equals(env.getUser().getEmail()))){
                    progressbarOfUpdateProfile.setVisibility(View.VISIBLE);
                    updateProfileUser();
                }
                edtUserName.setEnabled(false);
                edtUserEmail.setEnabled(false);
                btnSave.setText("Thay đổi hồ sơ");

//                uploadImageToStore();
                uploadImageToFireBase();
                setBtnOnChange();
                imgAvatar.setEnabled(false);

            }
        });
    }

    private void uploadImageToFireBase() {
        if(imagePath != null){
            final StorageReference riversRef = storageRef.child("public/image/avatar/" + env.getUser().get_id());
            UploadTask  uploadTask = riversRef.putFile(imagePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imagePath = null;
                            deleteAvatar();
                            updateAvatar(uri);
                            progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);

                            updateEnv();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressbarOfUpdateProfile.setVisibility(View.VISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Đăng ảnh bị lỗi", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void setBtnOnChange(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtUserName.setEnabled(true);
                edtUserEmail.setEnabled(true);
                btnSave.setText("Lưu hồ sơ");
                imgAvatar.setEnabled(true);
                setBtnSave();
            }
        });
    }

    private void updateProfileUser(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("username", edtUserName.getText().toString());
        map.put("email", edtUserEmail.getText().toString());
        ApiService.apiService.updateProfile("Bearer " + env.getToken(), map).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    updateEnv();
                    Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else {
                    edtUserName.setText(env.getUser().getUsername());
                    edtUserEmail.setText(env.getUser().getEmail());
                    progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Có gì đó sai sai, thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {
                progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Có gì đó sai sai, thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAvatar(Uri uri){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("avatar", uri.toString());
        ApiService.apiService.updateProfile("Bearer " + env.getToken(), map).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    updateEnv();
                    Toast.makeText(getApplicationContext(), "Đăng ảnh thành công", Toast.LENGTH_SHORT).show();
                } else {
                    edtUserName.setText(env.getUser().getUsername());
                    edtUserEmail.setText(env.getUser().getEmail());
                    progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Có gì đó sai sai, thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {
                progressbarOfUpdateProfile.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Có gì đó sai sai, thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadImageToStore() {
        if(imagePath != null){
            String imagePathReal = RealPathUtil.getRealPath(this, imagePath);
            File file = new File(imagePathReal);


            RequestBody requestBodyAvatar = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part mutiPartAvatar = MultipartBody.Part.createFormData("avatar", file.getName(), requestBodyAvatar);


            ApiService.apiService.updateAvatar("Bearer " + env.getToken(), mutiPartAvatar).enqueue(new Callback<ApiModel>() {
                @Override
                public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                    if(response.isSuccessful()){
                        deleteAvatar();
                        Toast.makeText(getApplicationContext(), imagePathReal, Toast.LENGTH_SHORT).show();
                    } else if(response.code() == 500){
                        Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiModel> call, Throwable t) {

                }
            });
        }
    }

    private void deleteAvatar(){
        ApiService.apiService.deleteAvatar("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {

            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    private void updateEnv(){
        ApiService.apiService.getMyInfo("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    env.setUser(response.body().getUser());
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagePath = data.getData();
            imgAvatar.setImageURI(imagePath);
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }
}
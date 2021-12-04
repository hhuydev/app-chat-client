package com.example.myappchat.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myappchat.Adapter.MessageAdapter;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.Conversation;
import com.example.myappchat.model.Message;
import com.example.myappchat.model.User;
import com.example.myappchat.utils.RealPathUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ImageButton sendButton;
    private EditText messageEditText;
    private ImageButton imgBtnAttachImage;
    Socket mSocket;
    Conversation conv;
    private Uri imagePath;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private static int PICK_IMAGE=123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();


        imgBtnAttachImage = findViewById(R.id.imgBtnAttachImage);
        conv = (Conversation) intent.getSerializableExtra("conv");
        toolbar = findViewById(R.id.toolbar_chat);
        toolbar.setTitle(intent.getStringExtra("convName"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        messageAdapter = new MessageAdapter();
        recyclerView = findViewById(R.id.rvc_chat);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setRecyclerView();

        messageEditText = findViewById(R.id.editText_chat);
        sendButton = findViewById(R.id.chat_send_btn);

        imgBtnAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestPermission();
            }
        });

        connectSocket();
        //Gửi tin nhắn
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageEditText.getText().toString();
                if(messageText.trim().length() > 0 ){
                    ApiService.apiService.createMessage("Bearer " + env.getToken(), conv.get_id(), messageText).enqueue(new Callback<ApiModel>() {
                        @Override
                        public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                            if(response.isSuccessful()){
                                try {
                                    mSocket.emit("sendMessage", new String(messageText.getBytes("UTF-8"), "ISO-8859-1"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                messageEditText.setText("");
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiModel> call, Throwable t) {

                        }
                    });
                }
                try{
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                } catch (Exception e){

                }
            }
        });
    }

    //Nhận tin nhắn
    private Emitter.Listener onRetrieveData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        String message = object.getString("text");

                        JSONObject userSenderJson = object.getJSONObject("user");
                        Gson gson = new Gson(); // khởi tạo Gson
                        User senderUser = gson.fromJson(String.valueOf(userSenderJson), User.class);

                        if(messageAdapter.getItemCount() == 0){
                            setRecyclerView();
                        }
                        messageAdapter.addMessage(new Message(conv.get_id(), senderUser.get_id(), new String(message.getBytes("ISO-8859-1"),"UTF-8")));
                    } catch (JSONException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1 );

                }

            });
        }
    };


    private void setRecyclerView(){
        ApiService.apiService.getMessage("Bearer " + env.getToken(), conv.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    if(response.body().getMessageArrayList().size() > 0){
                        messageAdapter = new MessageAdapter(response.body().getMessageArrayList());
                    }
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.scrollToPosition(response.body().getMessageArrayList().size()-1);
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    public void connectSocket(){
        ApiService.apiService.getMyInfo("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    User myInfo = response.body().getUser();
                    try {
                        IO.Options options = new IO.Options();
                        options.timestampRequests = true;
                        options.query = "token=" + env.getToken();
                        mSocket = IO.socket(env.getUrlServer(), options);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    JSONObject join = new JSONObject();
                    try {
                        join.put("username", myInfo.get_id());
                        join.put("room", conv.get_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mSocket.connect();
                    mSocket.emit("join", join);
                    mSocket.on("message", onRetrieveData);
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
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
            requestPermissions(permission, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PICK_IMAGE){
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
//                        createImageMessageLocalServer();
                        CreateImageMessage();
                    }
                }
            }
    );

    private void CreateImageMessage() {
        if(imagePath != null){
            final  StorageReference imageRef = storageRef.child("public/image/conversation/" + conv.get_id() + "/" + System.currentTimeMillis());
            imageRef.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ApiService.apiService.createMessage("Bearer " + env.getToken(), conv.get_id(), uri.toString()).enqueue(new Callback<ApiModel>() {
                                @Override
                                public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                                    if(response.isSuccessful()){
                                        try {
                                            mSocket.emit("sendMessage", new String(uri.toString().getBytes("UTF-8"), "ISO-8859-1"));
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiModel> call, Throwable t) {

                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Gửi ảnh bị lỗi", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void createImageMessageLocalServer() {
        if(imagePath != null) {
            String imagePathReal = RealPathUtil.getRealPath(this, imagePath);
            File file = new File(imagePathReal);

            RequestBody requestBodyAvatar = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part mutiPartAvatar = MultipartBody.Part.createFormData("photos", file.getName(), requestBodyAvatar);
            RequestBody requestBodyConvId = RequestBody.create(MediaType.parse("text/plain"), conv.get_id());

            ApiService.apiService.createImageMessage("Bearer " + env.getToken(), mutiPartAvatar, requestBodyConvId).enqueue(new Callback<ApiModel>() {
                @Override
                public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                    if(response.isSuccessful()){
                        mSocket.emit("sendMessage", "/uploads/images/" + file.getName());

                    }
                }

                @Override
                public void onFailure(Call<ApiModel> call, Throwable t) {

                }
            });
        }
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.btn_leave:
                leavingToRoomChat();
                return true;
            case R.id.btn_member:
                Intent iten = new Intent(this, MemberActivity.class);
                iten.putExtra("conv", conv);
                startActivity(iten);
                return true;
            case R.id.btn_add_member:
                Intent mainIntent = new Intent(this, AddFriendToConvActivity.class);
                mainIntent.putExtra("conv", conv);
                startActivity(mainIntent);
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_chat, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!conv.isGroup()) {
            menu.findItem(R.id.btn_add_member).setVisible(false);
            menu.findItem(R.id.btn_leave).setVisible(false);
        }
        return true;
    }

    private void leavingToRoomChat() {
        // check owner
        ApiService.apiService.getConversatioById(conv.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.code() == 200){
                    Conversation conversation = response.body().getConversation();
                    if(conversation.getOwner().equals(env.getUser().get_id()) && conversation.getUsers().size() > 1){
                        Toast.makeText(getApplicationContext(), "Bạn phải nhường quyền quản trị phòng cho người khác trước khi rời phòng", Toast.LENGTH_SHORT).show();
                    } else {
                        Leaving();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Server có lỗi", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    private void Leaving() {
        ApiService.apiService.LeaveConv("Bearer " + env.getToken(), conv.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {

            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mSocket.disconnect();
        } catch (Exception e){

        }
    }

}
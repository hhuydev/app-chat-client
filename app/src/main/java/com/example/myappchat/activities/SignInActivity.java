package com.example.myappchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.model.LoginModel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    TextInputEditText mEdtName, mEdtEmail, mEdtPassword, mEditConfirmPassword;
    ProgressDialog dialogLoading;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mEdtName = findViewById(R.id.editText_signIn_name);
        mEdtEmail = findViewById(R.id.editText_signIn_email);
        mEdtPassword = findViewById(R.id.editText_signIn_password);
        mEditConfirmPassword = findViewById(R.id.editText_signIn_confirm_password);


        toolbar = (Toolbar) findViewById(R.id.toolbar_signin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnSignIn = findViewById(R.id.btnSignin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateName() || !validateEmail() || !validatePasword() || !validatePaswordConfirm()){
                    return;
                }
                dialogLoading = ProgressDialog.show(SignInActivity.this, "Đăng ký",
                        "Vui lòng đợi...", true);
                registerUser();
            }
        });
    }

    private void registerUser() {
        String userName = mEdtName.getText().toString();
        String email = mEdtEmail.getText().toString();
        String password = mEdtPassword.getText().toString();
        String confirmPassword = mEditConfirmPassword.getText().toString();

        if(userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Nhập thiếu trường dữ liệu", Toast.LENGTH_SHORT).show();
        } else if(!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Mật khẩu nhập lại chưa đúng", Toast.LENGTH_SHORT).show();
        } else {
            ApiService.apiService.signup(userName, email, password).enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    if(response.isSuccessful()){
                        dialogLoading.cancel();
                        LoginModel loginModel = response.body();
                        Intent intent = new Intent(SignInActivity.this, OtpActivity.class);
                        intent.putExtra("email", loginModel.getUser().getEmail());
                        intent.putExtra("secret", loginModel.getSecret());
                        intent.putExtra("token", loginModel.getToken());
                        startActivity(intent);
//                      finish();
                    } else if (response.code() == 422){
                        dialogLoading.cancel();
                        mEdtEmail.setError("Địa chỉ email đã tồn tại");
                    }
                    else {
                        dialogLoading.cancel();
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), jObjError.getString("message").toString(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    dialogLoading.cancel();
                }
            });
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    private boolean validateName(){
        String nameInput = mEdtName.getText().toString().trim();

        if(nameInput.isEmpty()){
            mEdtName.setError("Họ và tên không được để trống");
            return false;
        } else {
            mEdtName.setError(null);
            return true;
        }
    }

    private boolean validateEmail(){
        String emailInput = mEdtEmail.getText().toString().trim();

        if(emailInput.isEmpty()){
            mEdtEmail.setError("Email không được để trống");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            mEdtEmail.setError("Chỉ nhập địa chỉ email");
            return false;
        }
        else {
            mEdtEmail.setError(null);
            return true;
        }
    }

    private boolean validatePasword(){
        String passwordInput = mEdtPassword.getText().toString().trim();

        if(passwordInput.isEmpty()){
            mEdtPassword.setError("Mật khẩu không được để trống");
            return false;
        } else if(passwordInput.length() < 6){
            mEdtPassword.setError("Mật khẩu phải lớn hơn 6 ký tự");
            return false;
        }
        else {
            mEdtPassword.setError(null);
            return true;
        }
    }

    private boolean validatePaswordConfirm(){
        String passwordConfirmInput = mEditConfirmPassword.getText().toString().trim();

        if(passwordConfirmInput.isEmpty()){
            mEditConfirmPassword.setError("Nhập lại mật khẩu không được để trống");
            return false;
        } else if(passwordConfirmInput.length() < 6){
            mEditConfirmPassword.setError("Nhập lại mật khẩu phải lớn hơn 6 ký tự");
            return false;
        } else if (!passwordConfirmInput.equals(mEdtPassword.getText().toString().trim())){
            mEditConfirmPassword.setError("Nhập lại mật khẩu phải giống mật khẩu");
            return false;
        } else {
            mEditConfirmPassword.setError(null);
            return true;
        }
    }
}
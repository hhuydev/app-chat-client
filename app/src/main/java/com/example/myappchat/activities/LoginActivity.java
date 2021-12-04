package com.example.myappchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.LoginModel;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextInputEditText mEdtEmail;
    TextInputEditText mEdtpassword;
    ProgressDialog dialogLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEdtEmail = findViewById(R.id.editText_login_email);
        mEdtpassword = findViewById(R.id.editText_login_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateEmail() || !validatePasword()){
                    return;
                }
                dialogLoading = ProgressDialog.show(LoginActivity.this, "Đăng nhập",
                        "Vui lòng đợi...", true);
                Login();
            }
        });

    }

    private void Login() {

        ApiService.apiService.Login(mEdtEmail.getText().toString(), mEdtpassword.getText().toString()).enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(response.isSuccessful()){
                    dialogLoading.cancel();
                    LoginModel loginModel = response.body();

                    Intent mainIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    mainIntent.putExtra("loginModel", loginModel);
                    env.setToken(loginModel.getToken());
                    env.setUser(loginModel.getUser());
                    startActivity(mainIntent);
                    finish();

                } else if(response.code() == 400){
                    dialogLoading.cancel();
                    createDiaLogUserLocked();
                } else {
                    dialogLoading.cancel();
                    createDiaLogBadPassword();
                }

            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                dialogLoading.cancel();
                Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
            }
        });

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

    public void createDiaLogUserLocked(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Không thể đăng nhập!");
        alertDialogBuilder.setMessage("Tài khoản của bạn đã bị khoá, vui lòng liên hệ quản trị viên");
        alertDialogBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertDialogBuilder.create().show();
    }

    public void createDiaLogBadPassword(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Không thể đăng nhập!");
        alertDialogBuilder.setMessage("Sai mật khẩu hoặc tài khoản");
        alertDialogBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertDialogBuilder.create().show();
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
        String passwordInput = mEdtpassword.getText().toString().trim();

        if(passwordInput.isEmpty()){
            mEdtpassword.setError("Mật khẩu không được để trống");
            return false;
        } else if(passwordInput.length() < 6){
            mEdtpassword.setError("Mật khẩu phải lớn hơn 6 ký tự");
            return false;
        }
        else {
            mEdtpassword.setError(null);
            return true;
        }
    }
}
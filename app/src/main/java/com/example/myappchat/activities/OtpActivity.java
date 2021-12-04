package com.example.myappchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myappchat.MainActivity;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.OtpAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private TextView tvSendOtpAgain;
    private EditText edtOtp;
    private Button btnVerifyOtp;
    private ProgressBar progressbarofotpauth;
    private Intent intent;
    private String email, secret, otp, token;
    private int remaining;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpctivity);

        tvSendOtpAgain = findViewById(R.id.tvSendOtpAgain);
        edtOtp  = findViewById(R.id.edtOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        progressbarofotpauth = findViewById(R.id.progressbarofotpauth);

        intent = getIntent();
        email = intent.getStringExtra("email");
        secret = intent.getStringExtra("secret");
        token = intent.getStringExtra("token");

        sendOtp();
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbarofotpauth.setVisibility(View.VISIBLE);
                ApiService.apiService.verify2Fa("Bearer " + token, edtOtp.getText().toString().trim(), secret).enqueue(new Callback<ApiModel>() {
                    @Override
                    public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                        if(response.isSuccessful()){
                            progressbarofotpauth.setVisibility(View.INVISIBLE);
                            Intent i = new Intent(OtpActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            progressbarofotpauth.setVisibility(View.INVISIBLE);
                            edtOtp.setError("Mã xác thực không đúng");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Server Error!", Toast.LENGTH_SHORT).show();
                        progressbarofotpauth.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        tvSendOtpAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbarofotpauth.setVisibility(View.VISIBLE);
                sendOtp();
                progressbarofotpauth.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void sendOtp(){
        ApiService.apiService.send2Fa("Bearer " + token, secret).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    OtpAuth otpAuth = response.body().getOtpAuth();
                    otp = otpAuth.getToken();
                    remaining = otpAuth.getRemaining();
                    Toast.makeText(getApplicationContext(), otp, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Không thể lấy mã xác thực", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
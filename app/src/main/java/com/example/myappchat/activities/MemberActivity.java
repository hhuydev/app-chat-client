package com.example.myappchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.myappchat.Adapter.MemberAdapter;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.Conversation;
import com.example.myappchat.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private Conversation conv;

    private MemberAdapter adapter;
    private List<User> members = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Intent intent = getIntent();
        conv = (Conversation) intent.getSerializableExtra("conv");

        recyclerView = findViewById(R.id.member_list_rcv);
        toolbar = findViewById(R.id.toolbar_member);

        toolbar.setTitle("Thành viên");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setRecyclerView();
    }

    private void setRecyclerView() {
        ApiService.apiService.getConversatioById(conv.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.code() == 200){
                    Conversation conversation = response.body().getConversation();
                    members = conversation.getUsers();
                    adapter = new MemberAdapter(members, MemberActivity.this, conversation);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return true;
        }
    }
}
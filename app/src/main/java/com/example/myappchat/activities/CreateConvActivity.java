package com.example.myappchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myappchat.Adapter.AddFriendWhenCreateConvAdaper;
import com.example.myappchat.Env.env;
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

public class CreateConvActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public AddFriendWhenCreateConvAdaper adaper;
    public Toolbar toolbar;
    List<User> friendList = new ArrayList<>();
    List<User> selectedFriend = new ArrayList<>();
    EditText edtGroupChatName, edtSearchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conv);

        recyclerView = findViewById(R.id.add_friend_list_rcv);
        toolbar = findViewById(R.id.toolbar_create_conv);
        edtGroupChatName = findViewById(R.id.edtGroupChatName);
        edtSearchName = findViewById(R.id.edtSearchName);
        toolbar.setTitle("Tạo nhóm mới");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setRecyclerView();

        edtGroupChatName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                setActionNext();
            }
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adaper.getFilter().filter(editable);
            }
        });

    }

    private void setRecyclerView() {
        friendList = new ArrayList<>();
        ApiService.apiService.getFriends("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.code() == 200){
                    friendList = response.body().getFriends();

                    adaper = new AddFriendWhenCreateConvAdaper(friendList, CreateConvActivity.this, selectedFriend, toolbar, edtGroupChatName);
                    recyclerView.setAdapter(adaper);
                    recyclerView.addItemDecoration(new DividerItemDecoration(CreateConvActivity.this, DividerItemDecoration.VERTICAL));
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) { }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_creat_group_chat, menu);

        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_next:
                CreateConv();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            default:
                return true;
        }
    }

    private void CreateConv() {
        //create with name
        String groupName = edtGroupChatName.getText().toString().trim();
        ApiService.apiService.CreateConversation("Bearer " + env.getToken(), groupName).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.code() == 201){
                    Conversation newConv = response.body().getNewConversation();

                    //add user to conversation
                    for (User item : selectedFriend) {
                        ApiService.apiService.AddUserToConv("Bearer " + env.getToken(), newConv.get_id(), item.getEmail()).enqueue(new Callback<ApiModel>() {
                            @Override
                            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {

                            }

                            @Override
                            public void onFailure(Call<ApiModel> call, Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_next);
        item.setEnabled(false);

        return true;
    }

    private void setActionNext() {
        if(selectedFriend.size() <= 0 || edtGroupChatName.getText().toString().trim().isEmpty()){
            toolbar.getMenu().findItem(R.id.action_next).setEnabled(false);
        } else {
            toolbar.getMenu().findItem(R.id.action_next).setEnabled(true);
        }
    }



}
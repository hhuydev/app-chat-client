package com.example.myappchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.myappchat.Adapter.AddFriendToConvAdapter;
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

public class AddFriendToConvActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public AddFriendToConvAdapter adapter;
    public Toolbar toolbar;
    List<User> friendList = new ArrayList<>();
    List<User> selectedFriend = new ArrayList<>();
    EditText edtFriendName;

    Conversation conv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_to_conv);

        Intent intent = getIntent();
        conv = (Conversation) intent.getSerializableExtra("conv");

        recyclerView = findViewById(R.id.add_friend_to_conv_list_rcv);
        toolbar = findViewById(R.id.toolbar_add_friend_conv);
        edtFriendName = findViewById(R.id.edtSearchFriendName);

        toolbar.setTitle("Thêm thành viên");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setRecyclerView();

        edtFriendName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(editable);
            }
        });
    }
    private void setRecyclerView() {
        friendList = new ArrayList<>();
        ApiService.apiService.getConversatioById(conv.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.code() == 200){
                    Conversation conversation = response.body().getConversation();
                    ApiService.apiService.getFriends("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
                        @Override
                        public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                            if(response.code() == 200){
                                friendList = response.body().getFriends();
                                for (User userInConv : conversation.getUsers()) {
                                    for(int i = 0; i < friendList.size(); i++){
                                        if(userInConv.get_id().equals(friendList.get(i).get_id())){
                                            friendList.remove(i);
                                        }
                                    }
                                }
                                adapter = new AddFriendToConvAdapter(friendList, AddFriendToConvActivity.this, selectedFriend, toolbar);
                                recyclerView.setAdapter(adapter);

                            }
                        }

                        @Override
                        public void onFailure(Call<ApiModel> call, Throwable t) { }
                    });
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
            case R.id.action_next:
                AddFriendToConv();
                finish();
                return true;
            default:
                return true;
        }
    }

    private void AddFriendToConv() {
        if(selectedFriend.size() > 0){
            for (User friend : selectedFriend) {
                ApiService.apiService.AddUserToConv("Bearer " + env.getToken(), conv.get_id(), friend.getEmail()).enqueue(new Callback<ApiModel>() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_creat_group_chat, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_next);
        item.setEnabled(false);

        return true;
    }
}
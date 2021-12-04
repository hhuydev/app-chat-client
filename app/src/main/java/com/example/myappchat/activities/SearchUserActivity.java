package com.example.myappchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.MessageRepApiModel;
import com.example.myappchat.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;
    private Button btnAddFriend, btnUnfriend;
    private TextView tvNameSearch;
    private SearchView searchView;
    private Intent intent;
    private ImageView imgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        intent = getIntent();

        constraintLayout = findViewById(R.id.container_search_layout);
        toolbar = findViewById(R.id.toolbar_search);
        tvNameSearch = findViewById(R.id.txtNameSearch);
        btnAddFriend = findViewById(R.id.btnFriendReq);
        btnUnfriend = findViewById(R.id.btnUnfriend);
        imgUser = findViewById(R.id.search_single_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_seach, menu);
        MenuItem menuItem = menu.findItem(R.id.action_seacch);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Nhập email");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String email = query.trim();
                ApiService.apiService.searchUser("Bearer " + env.getToken(), email).enqueue(new Callback<ApiModel>() {
                    @Override
                    public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                        if(response.isSuccessful()){
                            User user = response.body().getUser();
                            constraintLayout.setVisibility(View.VISIBLE);
                            tvNameSearch.setText(user.getUsername());
                            if(user.getAvatar().isEmpty()){
                                user.setAvatar("abcd");
                            }
                            Picasso.with(SearchUserActivity.this)
                                    .load(env.getUrlAvatar() + user.getAvatar())
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .into(imgUser);

                            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SendFriendReq(email);
                                }
                            });

                            btnUnfriend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SearchUserActivity.this);
                                    alertDialogBuilder.setTitle("Xác nhận");
                                    alertDialogBuilder.setMessage("Bạn chắc chắn huỷ kết bạn?");
                                    alertDialogBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            UnFriend(user.get_id());
                                        }
                                    });
                                    alertDialogBuilder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    alertDialogBuilder.create().show();
                                }
                            });
                            setDisplayFollowIsFriend(user);
                        } else {
                            Toast.makeText(getApplicationContext(), "Không tìm thấy", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiModel> call, Throwable t) {

                    }
                });

                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        MenuItemCompat.expandActionView(menuItem);
        searchView.setQuery("", true);
        String strEmail = intent.getStringExtra("email");

        try {
            if(!strEmail.isEmpty()){
                searchView.setQuery(strEmail, true);
            }
        } catch (Exception e){ }

        return super.onCreateOptionsMenu(menu);
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

    private void SendFriendReq(String email){
        ApiService.apiService.sendFriendReq("Bearer " + env.getToken(), email).enqueue(new Callback<MessageRepApiModel>() {
            @Override
            public void onResponse(Call<MessageRepApiModel> call, Response<MessageRepApiModel> response) {
                if(response.isSuccessful()){
                    String message = response.body().getMessage();
                    if(message.equals("Sent friend request, wait to accept")){
                        btnAddFriend.setEnabled(false);
                        Toast.makeText(getApplicationContext(), "Đã gửi", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageRepApiModel> call, Throwable t) {

            }
        });
    }

    private void UnFriend(String userId) {
        ApiService.apiService.UnFriend("Bearer " + env.getToken(), userId).enqueue(new Callback<MessageRepApiModel>() {
            @Override
            public void onResponse(Call<MessageRepApiModel> call, Response<MessageRepApiModel> response) {
                if(response.code() == 200){
                    btnUnfriend.setVisibility(View.INVISIBLE);
                    btnAddFriend.setVisibility(View.VISIBLE);
                    btnAddFriend.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Đã huỷ kết bạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageRepApiModel> call, Throwable t) {

            }
        });
    }

    private void setDisplayFollowIsFriend(User user){
        ApiService.apiService.getFriends("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    List<User> friends = response.body().getFriends();
                    for (User friend : friends) {
                        if(friend.get_id().equals(user.get_id())){
                            btnUnfriend.setVisibility(View.VISIBLE);
                            btnAddFriend.setVisibility(View.INVISIBLE);
                            break;
                        } else {
                            btnUnfriend.setVisibility(View.INVISIBLE);
                            btnAddFriend.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
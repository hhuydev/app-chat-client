package com.example.myappchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappchat.Env.env;
import com.example.myappchat.R;
import com.example.myappchat.activities.SearchUserActivity;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendAdaper extends RecyclerView.Adapter<FriendAdaper.FriendViewHoler>{
    private List<String> friendIdList;
    private Context context;


    public FriendAdaper(List<String> friendIdList, Context context) {
        this.friendIdList = friendIdList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_single_layout, parent,false);
        return new FriendAdaper.FriendViewHoler(view);    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHoler holder, int position) {
        String friendId = friendIdList.get(position);
        if(friendId == null){
            return;
        }
        ApiService.apiService.getUserById(friendId).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    User friend = response.body().getUser();
                    if(friend.getAvatar().isEmpty()){
                        friend.setAvatar("abcdef");
                    }
                    holder.textViewName.setText(friend.getUsername());
                    Picasso.with(context)
                            .load(env.getUrlAvatar()+friend.getAvatar())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(holder.imgFriend);
                    holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, SearchUserActivity.class);
                            intent.putExtra("email", friend.getEmail());
                            context.startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return friendIdList.size();
    }

    public class FriendViewHoler extends RecyclerView.ViewHolder{

        private ImageView imgFriend;
        private TextView textViewName, textViewLastMessage;
        private ConstraintLayout constraintLayout;

        public FriendViewHoler(@NonNull View itemView) {
            super(itemView);

            imgFriend = itemView.findViewById(R.id.friend_single_image);
            textViewName = itemView.findViewById(R.id.friend_single_name);
            textViewLastMessage = itemView.findViewById(R.id.friend_single_last_message);
            constraintLayout = itemView.findViewById(R.id.friends_single_layout);

        }
    }

}

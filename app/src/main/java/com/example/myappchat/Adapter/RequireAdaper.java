package com.example.myappchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.MessageRepApiModel;
import com.example.myappchat.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequireAdaper extends RecyclerView.Adapter<RequireAdaper.RequireViewHoler>{
    private List<User> requireList;
    private Context context;

    public RequireAdaper(List<User> requireList, Context context) {
        this.requireList = requireList;
        this.context = context;
    }

    @NonNull
    @Override
    public RequireViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requires_single_layout, parent,false);
        return new RequireViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequireViewHoler holder, int position) {
        User require = requireList.get(position);
        String urlImage = require.getAvatar();
        if(urlImage.isEmpty()){
            urlImage = "abcd";
        }
        if(require == null){
            return;
        }
        Picasso.with(context)
                .load(env.getUrlAvatar()+urlImage)
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imgUser);
        holder.textViewName.setText(require.getUsername());
        holder.accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AcceptFriendReq(require);

            }
        });

        holder.declBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RefusedFriendReq(require);

            }
        });
    }

    @Override
    public int getItemCount() {
        if(requireList != null){
            return requireList.size();
        }
        return 0;
    }

    public class RequireViewHoler extends RecyclerView.ViewHolder{

        private ImageView imgUser;
        private TextView textViewName;
        private Button accBtn, declBtn;

        public RequireViewHoler(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.require_single_image);
            textViewName = itemView.findViewById(R.id.require_single_TextView);
            accBtn = itemView.findViewById(R.id.requires_single_accept_btn);
            declBtn = itemView.findViewById(R.id.requires_single_decline_btn);

        }
    }

    private void AcceptFriendReq(User require){
        ApiService.apiService.AcceptFriendReq("Bearer " + env.getToken(), require.getEmail()).enqueue(new Callback<MessageRepApiModel>() {
            @Override
            public void onResponse(Call<MessageRepApiModel> call, Response<MessageRepApiModel> response) {
                if(response.isSuccessful() && response.body().getMessage().equals("Friend request accepted")){
                    Toast.makeText(context.getApplicationContext(), "Đã trở thành bạn bè", Toast.LENGTH_SHORT).show();
                    requireList.remove(require);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Đã có lỗi, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageRepApiModel> call, Throwable t) {

            }
        });
    }

    private void RefusedFriendReq(User require){
        ApiService.apiService.cancelFriendReq("Bearer " + env.getToken(), require.getEmail()).enqueue(new Callback<MessageRepApiModel>() {
            @Override
            public void onResponse(Call<MessageRepApiModel> call, Response<MessageRepApiModel> response) {
                if(response.isSuccessful() && response.code() == 200){
                    Toast.makeText(context.getApplicationContext(), "Đã từ chối", Toast.LENGTH_SHORT).show();
                    requireList.remove(require);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Đã có lỗi, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageRepApiModel> call, Throwable t) {

            }
        });
    }
}

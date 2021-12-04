package com.example.myappchat.Adapter;

import com.example.myappchat.R;
import com.example.myappchat.activities.ChatActivity;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.Conversation;
import com.example.myappchat.model.User;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvAdapter extends RecyclerView.Adapter<ConvAdapter.ConvViewHolder> {
    private Context context;
    private ArrayList<Conversation> convList;

    public ConvAdapter(ArrayList<Conversation> convList, Context context) {
        this.context = context;
        this.convList = convList;
    }

    @NonNull
    @Override
    public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent,false);
        return new ConvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConvViewHolder holder, int position) {
        Conversation conv = convList.get(position);
        if(conv == null){
            return;
        }
        ApiService.apiService.getLastMessage("Bearer " + env.getToken(), conv.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    if(checkImageLink(env.getUrlAvatar() + response.body().getLastMessage().getText())){
                        holder.imgLastMessage.setVisibility(View.VISIBLE);
                        holder.textViewLastMessage.setText("");
                    } else {
                        holder.imgLastMessage.setVisibility(View.GONE);
                        holder.textViewLastMessage.setText(response.body().getLastMessage().getText());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });


        Picasso.with(context)
                .load("https://www.mittalskinclinic.com/wp-content/uploads/2017/08/client-icon-18.png")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imgConv);
        holder.textViewName.setText(conv.getName());


        if(!conv.isGroup() && conv.getUsers().get(0).get_id().equals(env.getUser().get_id())){
            setConvLayout(holder, conv, conv.getUsers().get(1).get_id());
        } else if(!conv.isGroup() && !conv.getUsers().get(0).get_id().equals(env.getUser().get_id())){
            setConvLayout(holder, conv, conv.getUsers().get(0).get_id());
        }
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("conv", conv);
                intent.putExtra("convName", holder.textViewName.getText());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(convList != null){
            return  convList.size();
        }
        return 0;
    }

    public class ConvViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgConv, imgLastMessage;
        private TextView textViewName, textViewLastMessage;
        private ConstraintLayout constraintLayout;

        public ConvViewHolder(@NonNull View itemView) {
            super(itemView);

            imgConv = itemView.findViewById(R.id.user_single_image);
            imgLastMessage = itemView.findViewById(R.id.imgLastMessage);
            textViewName = itemView.findViewById(R.id.user_single_name);
            textViewLastMessage = itemView.findViewById(R.id.user_single_last_message);
            constraintLayout = itemView.findViewById(R.id.users_single_layout);

        }
    }

    public void setConvLayout(ConvViewHolder holder, Conversation conv, String userId){
        ApiService.apiService.getUserById(userId).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    User user = response.body().getUser();
                    if(user.getAvatar().isEmpty()){
                        user.setAvatar("abcdef");
                    }
                    holder.textViewName.setText(user.getUsername());
                    Picasso.with(context)
                            .load(env.getUrlAvatar()+user.getAvatar())
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(holder.imgConv);
                }
            }
            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    private boolean checkImageLink(String strLink){
        if(Patterns.WEB_URL.matcher(strLink.trim()).matches() && strLink.contains("myappchat-25ec5.appspot.com")){
            return true;
        }
        return false;
    }
}

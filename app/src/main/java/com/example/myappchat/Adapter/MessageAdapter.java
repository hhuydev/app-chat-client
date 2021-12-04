package com.example.myappchat.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappchat.R;
import com.example.myappchat.Env.env;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.Message;
import com.example.myappchat.model.User;
import com.example.myappchat.utils.DownloadImageTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private Context context;



    public void addMessage(Message message){
        messages.add(message);
    }
    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    public MessageAdapter() {
        messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        if(message == null){
            return;
        }
        User myUser = env.getUser();
        if(myUser.getAvatar().isEmpty()){
            myUser.setAvatar("abcd");
        }
        holder.message.setVisibility(View.VISIBLE);
        holder.imgMessage.setVisibility(View.VISIBLE);
        //My user
        if(myUser.get_id().equals(message.getSender())){
            holder.message.setText(message.getText());
            Picasso.with(context)
                .load(env.getUrlAvatar() + myUser.getAvatar())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imgUser);
//            holder.imgUser.setImageResource(R.drawable.default_avatar);
            MoveMessageBoxToRight(holder, context);
        }
        //My Friend
        else {
            ApiService.apiService.getUserById(message.getSender()).enqueue(new Callback<ApiModel>() {
                @Override
                public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                    String imgAvatarFriend = null;
                    if(response.code() == 200){
                        imgAvatarFriend = response.body().getUser().getAvatar();

                        if(!imgAvatarFriend.isEmpty()){
                            holder.message.setText(message.getText());
                            Picasso.with(context)
                                    .load(env.getUrlAvatar() + imgAvatarFriend)
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .into(holder.imgUser);
                        }

                    }

//            holder.imgUser.setImageResource(R.drawable.default_avatar);
                    MoveMessageBoxToLeft(holder, context);
                }

                @Override
                public void onFailure(Call<ApiModel> call, Throwable t) {

                }
            });
        }

        //Message Link Image
        if(checkImageLink(env.getUrlAvatar() + message.getText().trim())){
//            new DownloadImageTask((holder.imgMessage)).execute(env.getUrlAvatar() + message.getText().trim());
            Picasso.with(context)
                    .load(env.getUrlAvatar() + message.getText().trim())
                    .into(holder.imgMessage);
            holder.message.setVisibility(View.GONE);
            holder.imgMessage.setVisibility(View.VISIBLE);
        } else {
            holder.message.setVisibility(View.VISIBLE);
            holder.imgMessage.setVisibility(View.GONE);
        }
    }


    private void MoveMessageBoxToLeft(MessageViewHolder holder, Context context){
        holder.singleMessageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        holder.singleMessageLayout.setPaddingRelative(0,convertDpToPixel(8, context),convertDpToPixel(110,context),0);
        holder.message.setBackgroundResource(R.drawable.message_box);
        holder.message.setTextColor(Color.BLACK);
        int pixels = convertDpToPixel(8, context);
        holder.message.setPadding(pixels, pixels, pixels, pixels);
    }

    private void MoveMessageBoxToRight(MessageViewHolder holder, Context context){
        holder.singleMessageLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        holder.singleMessageLayout.setPaddingRelative(0,convertDpToPixel(8, context),convertDpToPixel(110,context),0);
        holder.message.setBackgroundResource(R.drawable.my_message_box);
        holder.message.setTextColor(Color.WHITE);
        int pixels = convertDpToPixel(8, context);
        holder.message.setPadding(pixels, pixels, pixels, pixels);
    }

    private static int convertDpToPixel(int dp, Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        int Pixels = (int) (dp*scale + 0.5f);
        return Pixels;
    }

    private static int convertPixelToDp(int pixel, Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        int dp = (int) (pixel/scale + 0.5f);
        return dp;
    }

    @Override
    public int getItemCount() {
        if(messages != null){
            return messages.size();
        }
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private ImageView imgUser, imgMessage;
        private LinearLayoutCompat singleMessageLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.message_single_mess);
            imgUser = itemView.findViewById(R.id.message_single_image);
            imgMessage = itemView.findViewById(R.id.imgMessage);
            singleMessageLayout = itemView.findViewById(R.id.message_single_layout);

        }
    }

    private boolean checkImageLink(String strLink){
        if(Patterns.WEB_URL.matcher(strLink.trim()).matches() && strLink.contains("myappchat-25ec5.appspot.com")){
            return true;
        }
        return false;
    }
}

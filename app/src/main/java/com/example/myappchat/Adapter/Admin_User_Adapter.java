package com.example.myappchat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappchat.Env.env;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Admin_User_Adapter extends RecyclerView.Adapter<Admin_User_Adapter.UserViewHoler> implements Filterable {

    private Context mContext;
    private List<User> userOlds;
    private List<User> users;

    public Admin_User_Adapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
        this.userOlds = users;
    }

    @NonNull
    @Override
    public UserViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_user_single_layout, parent,false);
        return new Admin_User_Adapter.UserViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHoler holder, int position) {
        User user = users.get(position);
        if(user == null){
            return;
        }
        if(user.getAvatar().isEmpty()){
            user.setAvatar("abcdef");
        }
        holder.textViewName.setText(user.getUsername());
        Picasso.with(mContext)
                .load(env.getUrlAvatar() + user.getAvatar())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imgUser);

        if(user.isLocked()){
            holder.imgBtnLock.setVisibility(View.VISIBLE);
            holder.imgBtnUnLock.setVisibility(View.GONE);
        } else {
            holder.imgBtnLock.setVisibility(View.GONE);
            holder.imgBtnUnLock.setVisibility(View.VISIBLE);
        }

        if(user.isAdmin()){
            holder.imgBtnLock.setVisibility(View.GONE);
            holder.imgBtnUnLock.setVisibility(View.GONE);
        }

        holder.imgBtnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle("Xác nhận");
                alertDialogBuilder.setMessage("Mở khoá tài khoản này?");
                alertDialogBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        unlockUser(user.get_id(), holder);
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


        holder.imgBtnUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle("Khoá tài khoản");
                alertDialogBuilder.setMessage("Người dùng sẽ không thể đăng nhập vào tài khoản này, bạn chắc chắn?");
                alertDialogBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        lockUser(user.get_id(), holder);
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
    }

    @Override
    public int getItemCount() {
        return users.size();
    }



    public class UserViewHoler extends RecyclerView.ViewHolder{

        private ImageView imgUser;
        private TextView textViewName;
        private ImageButton imgBtn, imgBtnLock, imgBtnUnLock;

        public UserViewHoler(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.admin_user_single_image);
            textViewName = itemView.findViewById(R.id.admin_user_single_name);
            imgBtn = itemView.findViewById(R.id.admin_user_single_imageButton);
            imgBtnLock = itemView.findViewById(R.id.admin_imgbtn_lock);
            imgBtnUnLock = itemView.findViewById(R.id.admin_imgbtn_unlock);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if(strSearch.isEmpty()){
                    users = userOlds;
                } else {
                    List<User> list = new ArrayList<>();
                    for(User user: userOlds){
                        if(user.getUsername().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(user);
                        }
                    }
                    users = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = users;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                users = (List<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    
    void unlockUser(String userId, UserViewHoler holder){
        ApiService.apiService.unlockUser("Bearer " + env.getToken(), userId).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    holder.imgBtnLock.setVisibility(View.GONE);
                    holder.imgBtnUnLock.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext.getApplicationContext(), "Đã mở khoá tài khoản", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "Bạn không có quyền làm điều này", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {
                Toast.makeText(mContext.getApplicationContext(), "Kết nối đến bị server lỗi", Toast.LENGTH_SHORT).show();

            }
        });
    }


    void lockUser(String userId, UserViewHoler holder){
        ApiService.apiService.lockUser("Bearer " + env.getToken(), userId).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    holder.imgBtnUnLock.setVisibility(View.GONE);
                    holder.imgBtnLock.setVisibility(View.VISIBLE);
                    Toast.makeText(mContext.getApplicationContext(), "Đã khoá tài khoản", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "Bạn không có quyền làm điều này", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {
                Toast.makeText(mContext.getApplicationContext(), "Kết nối đến bị server lỗi", Toast.LENGTH_SHORT).show();

            }
        });
    }

}

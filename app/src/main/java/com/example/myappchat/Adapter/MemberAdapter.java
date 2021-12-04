package com.example.myappchat.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappchat.Env.env;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.Conversation;
import com.example.myappchat.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHoler>{
    private List<User> memberList;
    private Context context;
    private Conversation conv;
    private boolean flagOwner = true;

    public MemberAdapter(List<User> memberList, Context context, Conversation conv) {
        this.memberList = memberList;
        this.context = context;
        this.conv = conv;
    }

    @NonNull
    @Override
    public MemberViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_single_layout, parent,false);
        return new MemberAdapter.MemberViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHoler holder, int position) {
        User member = memberList.get(position);
        if(member == null){
            return;
        }

        holder.textViewName.setText(member.getUsername());
        if(member.getAvatar().isEmpty()){
            member.setAvatar("abcdef");
        }
        Picasso.with(context)
                .load(env.getUrlAvatar() + member.getAvatar())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imgMember);

        if(env.getUser().get_id().equals(conv.getOwner())){
            holder.imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowMenu(view, member);
                }
            });
        } else {
            holder.imgBtn.setVisibility(View.GONE);
        }
        if(member.get_id().equals(env.getUser().get_id())){
            holder.imgBtn.setVisibility(View.GONE);
        }
        if(conv.isGroup() && conv.getOwner().equals(member.get_id())){
            holder.textViewQuanTri.setText("Quản trị phòng");
            holder.textViewQuanTri.setVisibility(View.VISIBLE);
        } else {
            holder.textViewQuanTri.setText("");
            holder.textViewQuanTri.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    private void ShowMenu(View view, User member) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.menu_member);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.setOwner:
                        if(flagOwner == false){
                            Toast.makeText(context.getApplicationContext(), "Bạn không có quyền làm điều này", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        alertDialogBuilder.setTitle("Xác nhận");
                        alertDialogBuilder.setMessage("Bạn sẽ mất quyền quản trị nhóm, bạn chắc chắn?");
                        alertDialogBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                setOwner(member);
                                flagOwner = false;
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialogBuilder.create().show();
                        return true;
                    case R.id.remove_member:
                        if(flagOwner == false){
                            Toast.makeText(context.getApplicationContext(), "Bạn không có quyền làm điều này", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        alertDialogBuilder.setTitle("Xác nhận");
                        alertDialogBuilder.setMessage("Xoá thành viên này ra khỏi nhóm?");
                        alertDialogBuilder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                RemoveUserToConv(member);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialogBuilder.create().show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void setOwner(User member) {
        ApiService.apiService.ChangeConvOwner("Bearer " + env.getToken(), conv.get_id(), member.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context.getApplicationContext(), "Đã thay đổi người quản trị phòng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Bạn không có quyền làm điều này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });

    }

    private void RemoveUserToConv(User member) {
        ApiService.apiService.RemoveUserToConv("Bearer " + env.getToken(), conv.get_id(), member.get_id()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context.getApplicationContext(), "Đã xoá thành viên", Toast.LENGTH_SHORT).show();
                    memberList.remove(member);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Bạn không có quyền làm điều này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }


    public class MemberViewHoler extends RecyclerView.ViewHolder{
        private ImageView imgMember;
        private TextView textViewName, textViewQuanTri;
        private ImageButton imgBtn;

        public MemberViewHoler(@NonNull View itemView) {
            super(itemView);

            imgMember = itemView.findViewById(R.id.member_single_image);
            textViewName = itemView.findViewById(R.id.member_single_name);
            textViewQuanTri = itemView.findViewById(R.id.txtQuanTri);
            imgBtn = itemView.findViewById(R.id.member_single_imageButton);
        }
    }
}

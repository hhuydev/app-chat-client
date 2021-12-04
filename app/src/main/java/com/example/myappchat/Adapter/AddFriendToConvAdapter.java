package com.example.myappchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappchat.Env.env;
import com.example.myappchat.R;
import com.example.myappchat.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddFriendToConvAdapter extends RecyclerView.Adapter<AddFriendToConvAdapter.AddFriendViewHoler> implements Filterable {

    private List<User> friendList;
    private List<User> friendListOld;
    private Context context;
    private List<User> selectedUser;
    public Toolbar toolbar;

    public AddFriendToConvAdapter(List<User> friendList, Context context, List<User> selectedUser, Toolbar toolbar) {
        this.friendList = friendList;
        this.context = context;
        this.selectedUser = selectedUser;
        this.toolbar = toolbar;
        this.friendListOld = friendList;

    }

    @NonNull
    @Override
    public AddFriendViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_user_single_layout, parent,false);
        return new AddFriendToConvAdapter.AddFriendViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendViewHoler holder, int position) {
        User friend = friendList.get(position);
        if(friend == null){
            return;
        }
        if(friend.getAvatar().isEmpty()){
            friend.setAvatar("abcdef");
        }
        holder.textViewName.setText(friend.getUsername());
        Picasso.with(context)
                .load(env.getUrlAvatar()+ friend.getAvatar())
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.imgFriend);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkBoxAdd.isChecked()){
                    holder.checkBoxAdd.setChecked(false);
                    selectedUser.remove(friend);
                    setActionNext();
                } else {
                    holder.checkBoxAdd.setChecked(true);
                    selectedUser.add(friend);
                    setActionNext();
                }
            }
        });

        if(selectedUser.contains(friend)){
            holder.checkBoxAdd.setChecked(true);
        } else {
            holder.checkBoxAdd.setChecked(false);
        }
    }

    private void setActionNext() {
        if(selectedUser.size() <= 0){
            toolbar.getMenu().findItem(R.id.action_next).setEnabled(false);
        } else {
            toolbar.getMenu().findItem(R.id.action_next).setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public class AddFriendViewHoler extends RecyclerView.ViewHolder{

        private ImageView imgFriend;
        private TextView textViewName;
        private CheckBox checkBoxAdd;
        private ConstraintLayout constraintLayout;

        public AddFriendViewHoler(@NonNull View itemView) {
            super(itemView);

            imgFriend = itemView.findViewById(R.id.add_user_single_image);
            textViewName = itemView.findViewById(R.id.add_user_single_name);
            checkBoxAdd = itemView.findViewById(R.id.checkboxAdd);
            constraintLayout = itemView.findViewById(R.id.add_user_single_layout);

        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if(strSearch.isEmpty()){
                    friendList = friendListOld;
                } else {
                    List<User> list = new ArrayList<>();
                    for(User user: friendListOld){
                        if(user.getUsername().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(user);
                        }
                    }
                    friendList = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = friendList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                friendList = (List<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

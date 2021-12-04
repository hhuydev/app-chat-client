package com.example.myappchat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myappchat.Adapter.FriendAdaper;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.FriendRequestStatuses;
import com.example.myappchat.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendAdaper adaper;
    List<String> friendIdList = new ArrayList<>();

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.friend_list_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setRecyclerView();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    private void setRecyclerView() {
        friendIdList = new ArrayList<>();
        ApiService.apiService.getFriends("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if(response.code() == 200){
                    List<User> friendModel = response.body().getFriends();
                    if(friendModel.size() > 0){
                        for (User item : friendModel) {
                            friendIdList.add(item.get_id());
                        }
                    }
                    adaper = new FriendAdaper(friendIdList, getContext());
                    recyclerView.setAdapter(adaper);
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
    }
}
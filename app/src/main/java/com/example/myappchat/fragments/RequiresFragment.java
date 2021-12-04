package com.example.myappchat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myappchat.Adapter.RequireAdaper;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.ListFriendRequest;
import com.example.myappchat.model.Require;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequiresFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequireAdaper adaper;
    List<Require> list;


    public RequiresFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = view.findViewById(R.id.req_list_rcv);

        setRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        setRecyclerView();
    }

    private void setRecyclerView(){
        list = new ArrayList<>();
        ApiService.apiService.getListFriendReq("Bearer " + env.getToken()).enqueue(new Callback<ListFriendRequest>() {
            @Override
            public void onResponse(Call<ListFriendRequest> call, Response<ListFriendRequest> response) {
                if(response.isSuccessful()){
                    ListFriendRequest listFriendRequest = response.body();
                    if(listFriendRequest.getMessage().equals("success get list friend request")){
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        adaper = new RequireAdaper(listFriendRequest.getUserList(), getContext());
                        recyclerView.setAdapter(adaper);
                        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

                    }
                }
            }

            @Override
            public void onFailure(Call<ListFriendRequest> call, Throwable t) {

            }
        });
    }
}
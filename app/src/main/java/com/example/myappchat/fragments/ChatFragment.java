package com.example.myappchat.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myappchat.Adapter.ConvAdapter;
import com.example.myappchat.R;
import com.example.myappchat.api.ApiService;
import com.example.myappchat.Env.env;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.Conversation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConvAdapter convAdapter;
    private ArrayList<Conversation> conversations;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = mainView.findViewById(R.id.conv_list_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        setRecyclerView();



        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
    }

    private void setRecyclerView(){
        conversations = new ArrayList<>();
        ApiService.apiService.getConv("Bearer " + env.getToken()).enqueue(new Callback<ApiModel>() {
            @Override
            public void onResponse(Call<ApiModel> call, Response<ApiModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage() != null) {
                        return;
                    }
                    conversations = response.body().getConversations();
                    convAdapter = new ConvAdapter(conversations, getContext());
                    recyclerView.setAdapter(convAdapter);
                }
            }

            @Override
            public void onFailure(Call<ApiModel> call, Throwable t) {

            }
        });
    }
}
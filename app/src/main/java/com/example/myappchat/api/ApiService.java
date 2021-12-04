package com.example.myappchat.api;

import com.example.myappchat.Env.env;
import com.example.myappchat.model.ApiModel;
import com.example.myappchat.model.ListFriendRequest;
import com.example.myappchat.model.LoginModel;
import com.example.myappchat.model.MessageRepApiModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd-HH:mm:ss")
            .create();


    ApiService apiService = new Retrofit.Builder()
            .baseUrl(env.getUrlServer())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);


    //Login
    @FormUrlEncoded
    @POST("api/users/login")
    Call<LoginModel> Login(@Field("email") String email, @Field("password") String passwword);

    //Logup
    @FormUrlEncoded
    @POST("api/users/signup")
    Call<LoginModel> signup(@Field("username") String username, @Field("email") String email, @Field("password") String passwword);

    //Get Me
    @GET("api/users/me")
    Call<ApiModel> getMyInfo(@Header("authorization") String token);

    //Get Conversations
    @GET("api/conversations/get-conversations")
    Call<ApiModel> getConv(@Header("authorization") String token);


    //Create Message
    @FormUrlEncoded
    @POST("api/messages/create-message")
    Call<ApiModel> createMessage(@Header("authorization") String token, @Field("conversationId") String conversationId, @Field("text") String message);

    //Get Messages By Conversation
    @GET("api/messages/get-messages-by-conversation/{id}")
    Call<ApiModel> getMessage(@Header("authorization") String token, @Path("id") String conversationId);

    //Get Last Messages By Conversation
    @GET("api/messages/get-latest-message-by-conversation/{id}")
    Call<ApiModel> getLastMessage(@Header("authorization") String token, @Path("id") String conversationId);

    //Get User By Email
    @GET("api/users/search-user/{email}")
    Call<ApiModel> searchUser(@Header("authorization") String token, @Path("email") String email);

    //Get User by UserId
    @GET("api/users/{userId}")
    Call<ApiModel> getUserById(@Path("userId") String userId);


    //Send Friend Request
    @FormUrlEncoded
    @POST("api/friends/friend-request")
    Call<MessageRepApiModel> sendFriendReq(@Header("authorization") String token, @Field("email") String email);

    @FormUrlEncoded
    @POST("api/friends/delete-friend")
    Call<MessageRepApiModel> UnFriend(@Header("authorization") String token, @Field("userId") String userId);

    //Get List Friend Request
    @GET("api/friends/get-list-friend-request")
    Call<ListFriendRequest> getListFriendReq(@Header("authorization") String token);

    //Accept Friend Request
    @FormUrlEncoded
    @POST("api/friends/accept-friend-request")
    Call<MessageRepApiModel> AcceptFriendReq(@Header("authorization") String token, @Field("email") String email);

    //Cancel Friend Request
    @FormUrlEncoded
    @POST("api/friends/cancel-friend-request")
    Call<MessageRepApiModel> cancelFriendReq(@Header("authorization") String token, @Field("email") String email);

    //Get Friend List
    @GET("api/friends/get-my-list-friend")
    Call<ApiModel> getFriends(@Header("authorization") String token);

    //Update Profile
    @FormUrlEncoded
    @PATCH("api/users/me/update")
    Call<ApiModel> updateProfile(@Header("authorization") String token, @FieldMap HashMap<String, String> params);

    //Delete Avatar
    @DELETE("api/users/me/delete-avatar")
    Call<ApiModel> deleteAvatar(@Header("authorization") String token);

    //Update Avatar
    @Multipart
    @POST("api/users/me/upload-avatar")
    Call<ApiModel> updateAvatar(@Header("authorization") String token, @Part MultipartBody.Part filePart);

    //Create Conversation
    @FormUrlEncoded
    @POST("api/conversations/create-conversation")
    Call<ApiModel> CreateConversation(@Header("authorization") String token, @Field("name") String groupName);

    //Get Conversation
    @GET("api/conversations/get-conversation/{ConvId}")
    Call<ApiModel> getConversatioById(@Path("ConvId") String convId);

    //Add User to Conversation
    @FormUrlEncoded
    @POST("api/conversations/invite-user-to-conversation")
    Call<ApiModel> AddUserToConv(@Header("authorization") String token, @Field("conversationId") String ConvId, @Field("email") String email);


    //Remove User To Conversation
    @FormUrlEncoded
    @POST("api/conversations/remove-user-from-conversation")
    Call<ApiModel> RemoveUserToConv(@Header("authorization") String token, @Field("conversationId") String ConvId, @Field("userId") String userId);



    //Leave Conversation
    @FormUrlEncoded
    @POST("api/conversations/leave-conversation")
    Call<ApiModel> LeaveConv(@Header("authorization") String token, @Field("conversationId") String ConvId);


    //Update Conversation Name
    @FormUrlEncoded
    @PATCH("api/conversations/update-conversation-name")
    Call<ApiModel> UpdateConvName(@Header("authorization") String token, @Field("conversationId") String ConvId, @Field("name") String name);

    //Change Conversation Owner
    @FormUrlEncoded
    @PATCH("api/conversations/change-conversation-owner")
    Call<ApiModel> ChangeConvOwner(@Header("authorization") String token, @Field("conversationId") String ConvId, @Field("userId") String userId);


    //Delete Conversation
    @DELETE("api/conversations/delete-conversation")
    Call<ApiModel> deleteItem(@Header("authorization") String token, @Field("conversationId") String ConvId);




    //Admin Get All User
    @GET("api/admin/getAllUser")
    Call<ApiModel> getAllUser(@Header("authorization") String token);

    //Admin Lock User
    @FormUrlEncoded
    @POST("api/admin/lockAccount")
    Call<ApiModel> lockUser(@Header("authorization") String token, @Field("userId") String userId);


    //Admin Unlock User
    @FormUrlEncoded
    @POST("api/admin/unlockAccount")
    Call<ApiModel> unlockUser(@Header("authorization") String token, @Field("userId") String userId);

    //Create Image To Message
    @Multipart
    @POST("api/messages/create-img-message")
    Call<ApiModel> createImageMessage(@Header("authorization") String token, @Part MultipartBody.Part filePart, @Part("conversationId") RequestBody convId);

    @FormUrlEncoded
    @POST("api/users/enable-2fa")
    Call<ApiModel> send2Fa(@Header("authorization") String token, @Field("secret") String secret);

    @FormUrlEncoded
    @POST("api/users/verify-2fa")
    Call<ApiModel> verify2Fa(@Header("authorization") String token, @Field("otpToken") String otpToken, @Field("secret") String secret);
}

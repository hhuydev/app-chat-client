package com.example.myappchat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginModel implements Serializable  {
   @SerializedName("user")
   @Expose
   private User user;
   @SerializedName("token")
   @Expose
   private String token;
   @SerializedName("isCorrectIdentifier")
   @Expose
   private boolean isCorrectIdentifier;
   @SerializedName("secret")
   @Expose
   private String secret;

   public String getSecret() {
      return secret;
   }

   public void setSecret(String secret) {
      this.secret = secret;
   }

   public LoginModel() {
   }

   public LoginModel(User user, String token, boolean isCorrectIdentifier) {
      this.user = user;
      this.token = token;
      this.isCorrectIdentifier = isCorrectIdentifier;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public String getToken() {
      return token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public boolean isCorrectIdentifier() {
      return isCorrectIdentifier;
   }

   public void setCorrectIdentifier(boolean correctIdentifier) {
      isCorrectIdentifier = correctIdentifier;
   }
}

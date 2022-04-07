package com.example.aves.Interface;

import com.example.aves.Domain.Results;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://aves-server.herokuapp.com/";

    @POST("auth/create/")
    @FormUrlEncoded
    Call<JsonObject> postCreateUser(@Field("username") String username,
    @Field("email") String email, @Field("password") String password);

    @POST("auth/login/")
    @FormUrlEncoded
    Call<JsonObject> postLoginUser(@Field("email") String email, @Field("password") String password);

    @GET("contents/popular/")
    Call<JsonObject> getContentsPopular(@Query("page") int page);

    @GET("contents/new/")
    Call<JsonObject> getContentsNew(@Query("page") int page);

    @GET("contents/get/{CONTENT_ID}")
    Call<JsonObject> getContent(@Path("CONTENT_ID") String id);

    @GET("contents/search/")
    Call<JsonObject> getSearchContent(@Query("q") String query);

    @Multipart
    @POST("contents/upload/")
    Call<JsonObject> postUploadFile(@Part MultipartBody.Part image, @Part MultipartBody.Part upload,
                                    @Part("name") RequestBody name, @Part("description") RequestBody description,
                                    @Part("price") RequestBody price, @Part("category") RequestBody category);

    @PUT("contents/edit/{CONTENT_ID}")
    @FormUrlEncoded
    Call<JsonObject> putUploadContent(@Path("CONTENT_ID") String id,
                                      @Field("name") String name, @Field("description") String description,
                                      @Field("price") String price, @Field("category") String category);

    @POST("contents/like/")
    @FormUrlEncoded
    Call<JsonObject> postLike (@Field("owner") int userId, @Field("content") String contentId);


    @POST("payment/check/")
    @FormUrlEncoded
    Call<JsonObject> postCheckPayment(@Field("owner") int userId, @Field("content") String contentId);

    @GET("payment/info/{CONTENT_ID}")
    Call<JsonObject> getPurchaseInfo(@Path("CONTENT_ID") String contentId);

}

package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aves.Domain.UserDomain;
import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Helper.Util;
import com.example.aves.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private TextView textViewName, textViewDescription, textViewSize, textViewLength, textViewPrice;
    private Button buttonLike, buttonPreview, buttonBuy;
    private ImageView imageViewThumbnail;
    private LinearLayout layoutDetailHolder;

    private String preview = "";

    private String contentId = "";
    private String name;
    private String price;
    private String size;
    private String description;
    private String likes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle bundle = getIntent().getExtras();
        contentId = bundle.getString("contentId");

        initViews();
        initListeners();
        getContent(contentId);

        initDB();
    }



    private void initDB() {
        db = openOrCreateDatabase("aves", Context.MODE_PRIVATE, null);
    }

    private void initViews() {
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewName = findViewById(R.id.textViewName);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewSize = findViewById(R.id.textViewSize);
        textViewLength = findViewById(R.id.textViewLength);

        buttonLike = findViewById(R.id.buttonLike);
        buttonPreview = findViewById(R.id.buttonPreview);
        buttonBuy = findViewById(R.id.buttonBuy);

        imageViewThumbnail = findViewById(R.id.detailImage);

        layoutDetailHolder = findViewById(R.id.layoutDetailHolder);
    }

    private void initListeners() {
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                likeContent();
            }
        });

        buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preview.length() > 0 ) {
                    Intent intent = new Intent(DetailActivity.this, PreviewActivity.class);
                    intent.putExtra("url", preview);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "No preview available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!contentId.equals("")) {
                   checkAlreadyBought();
                }
            }
        });
    }

    private void likeContent() {
        UserDomain user = getLocalUserData();
        String token = "Bearer " + user.getAccessToken();
        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().postLike(user.getId(), contentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200) {
                    JsonObject res = response.body();
                    if(res.get("result").getAsBoolean()){
                        Toast.makeText(getApplicationContext(), "Liked", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unliked", Toast.LENGTH_LONG).show();
                    }
                } else if(response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "UnAuthorized user.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("json", "new error", t);
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkAlreadyBought() {
        UserDomain user = getLocalUserData();
        String token = "Bearer " + user.getAccessToken();
        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().postCheckPayment(user.getId(), contentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200) {
                    JsonObject res = response.body();
                     if(res.get("result").getAsBoolean()){
                         Toast.makeText(getApplicationContext(), "You already bought this content.", Toast.LENGTH_LONG).show();
                     } else {
                         Intent intent = new Intent(DetailActivity.this, PurchaseInfoActivity.class);
                         intent.putExtra("contentId", contentId);
                         startActivity(intent);
                     }
                } else if(response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "UnAuthorized user.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("json", "new error", t);
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getContent(String id) {
        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getContent(id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200) {
                    layoutDetailHolder.setVisibility(View.VISIBLE);
                    JsonObject res = response.body();
                    contentId = res.get("id").getAsString();
                    name = res.get("name").getAsString();
                    price = res.get("price").getAsString();
                    int size = res.get("size").getAsInt();
//                    int length = res.get("length").getAsInt();
                    description = res.get("description").getAsString();
                    preview  = res.get("preview").getAsString();
                    likes = Integer.toString(res.get("likes").getAsInt());

                    String image = "";
                    if(!res.get("image").isJsonNull()){
                        image = res.get("image").getAsString();
                    }

                    Log.v("json", "fetched");
                    setView(name, price, description, preview, likes, image, Integer.toString(size));
                } else if(response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "UnAuthorized user.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("json", "new error", t);
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setView(String name, String price, String description, String preview, String likes, String image, String size) {
        textViewDescription.setText(description);
        textViewName.setText(name);
        textViewPrice.setText(price);
        textViewSize.setText(size);
//        textViewLength.setText(length);
        if(image.length() > 0) {
            Picasso.with(getApplicationContext()).load(image).fit().centerInside().into(imageViewThumbnail);
        }
    }

    private UserDomain getLocalUserData(){
        int userId = getUserId();

        Cursor c = db.rawQuery("SELECT user_id, user_name, user_email, accessToken, refreshtoken FROM Users WHERE user_id=" + userId, null);
        c.moveToFirst();
        int userID = c.getInt(0);
        String userName = c.getString(1);
        String userEmail = c.getString(2);
        String accessToken = c.getString(3);
        String refreshToken = c.getString(4);

        UserDomain user = new UserDomain(userID, userName, userEmail, accessToken, refreshToken);
        return user;
    }

    private int getUserId() {
        Cursor c = db.rawQuery("SELECT user_id FROM App WHERE index_id=1", null);
        c.moveToFirst();
        return c.getInt(0);
    }

}
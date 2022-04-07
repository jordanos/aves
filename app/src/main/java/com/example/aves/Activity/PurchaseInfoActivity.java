package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aves.Domain.UserDomain;
import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Interface.Api;
import com.example.aves.R;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PurchaseInfoActivity extends AppCompatActivity {
    private int userId;
    private String contentId;

    private SQLiteDatabase db;
    private Button buttonPurchase;
    private TextView textViewUrl;

    private Api api;
    String BASE_URL = api.BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_info);
        Bundle bundle = getIntent().getExtras();
        contentId = bundle.getString("contentId");


        initDB();
        initViews();
        initListeners();

        userId = getLocalUserData().getId();

    }

    private void initViews() {
        buttonPurchase = findViewById(R.id.buttonPurcahse);
        textViewUrl = findViewById(R.id.textViewUrl);
    }

    private void initListeners() {
        buttonPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseContent();
            }
        });
    }

    private void purchaseContent() {
//        get purchase and add it to Contents table
        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getPurchaseInfo(contentId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code() == 200) {
                    JsonObject res = response.body();
                    String id = res.get("id").getAsString();
                    String key = res.get("key").getAsString();
                    String nonce = res.get("nonce").getAsString();
                    String contentUrl = res.get("content").getAsString();
                    addToContents(userId, id, key, nonce);
                    Toast.makeText(getApplicationContext(), "Content purchased", Toast.LENGTH_SHORT).show();
                    String urlText = "You can download the content from here...\n" + BASE_URL + "media" + contentUrl;
                    textViewUrl.setText(urlText);

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

    private void addToContents(int userId, String contentId, String key, String nonce) {
        db.execSQL("INSERT INTO Contents(user_id, content_id, content_key, content_nonce) VALUES(" + userId +",'"+ contentId + "','"
                + key +"', '" + nonce +"');");
    }

    private void initDB() {
        db = openOrCreateDatabase("aves", Context.MODE_PRIVATE, null);
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
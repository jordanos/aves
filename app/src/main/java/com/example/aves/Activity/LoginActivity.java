package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.aves.Helper.Info;
import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Helper.Util;
import com.example.aves.R;
import com.google.gson.JsonObject;

import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Util util;
    private static final String BASE_URL = Info.getDomainUrl();
    private SQLiteDatabase db;
    private Button loginBtn;
    private EditText emailText, passwordText;
    private TextView textViewRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intiViews();
        initDB();
        initListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        util = new Util(this);
    }


    private void initListeners() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                loginCheck(email, password);
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent =  new Intent(LoginActivity.this, SignupActivity.class);
                signupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signupIntent);
            }
        });
    }


    private void intiViews() {
        loginBtn = (Button) findViewById(R.id.buttonLogin);
        emailText = (EditText) findViewById(R.id.editTextEmail);
        passwordText = (EditText) findViewById(R.id.editTextPassword);
        textViewRegister = (TextView)findViewById(R.id.textViewRegister);
    }


    private void loginCheck(String email, String password) {
        if(email.length() < 5 || password.length() < 6 ) {
            Toast.makeText(getApplicationContext(), "Password or email is too short.", Toast.LENGTH_SHORT).show();
            return;
        }
        login(email, password);
    }


    private void login(String email, String password) {
        util.showProgress();
        if(util.isConnected()){
            Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().postLoginUser(email, password);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    util.hideProgress();
                    if(response.code() == 200) {
                        JsonObject res = response.body();
                        int id = res.get("id").getAsInt();
                        String username = res.get("username").getAsString();
                        String email = res.get("email").getAsString();
                        String refreshToken = res.get("refreshToken").getAsString();
                        String accessToken = res.get("accessToken").getAsString();
                        initUser(id, username, email, refreshToken, accessToken);
                        startHomepage(id, username);
                    } else if(response.code() == 401) {
                        util.showToast("error");
                        Log.e("json", "executing");
//                        JsonObject res = response.body();
//                        String message = res.get("detail").getAsString();
//                        util.showError(message);
//                        Log.e("json", response.body().toString());
                    }
                    else {
                        util.showError("Something went wrong.");
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    util.showError("Something went wrong.");
                }
            });
        } else {
            util.hideProgress();
            util.showWarning("Please turn on your internet connection");
        }

    }

    private void startHomepage(int id, String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("user_id", id);
        intent.putExtra("user_name", username);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initUser(int id, String username, String email, String refreshToken, String accessToken) {
        db.execSQL("INSERT INTO Users(user_id, user_name, user_email, refreshToken, accessToken) VALUES(" + id +",'"+ username + "','"
                + email +"', '" + refreshToken +"', '" + accessToken +"');");
        db.execSQL("UPDATE App SET user_id=" + id + ", is_logged=1 WHERE index_id=1;");
    }

    private void initDB() {
        db = openOrCreateDatabase("aves", Context.MODE_PRIVATE, null);
    }

}
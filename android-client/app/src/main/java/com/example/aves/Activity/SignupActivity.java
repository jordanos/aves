package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;


import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Helper.Util;
import com.example.aves.R;
import com.google.gson.JsonObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private Util util;
    private SQLiteDatabase db;
    EditText editTextName, editTextEmail, editTextPass, editTextConfirmPass;
    Button buttonSignup;
    TextView textViewAlready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        initViews();
        initDB();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        util = new Util(this);
    }

    private void initListeners() {
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPass.getText().toString();
                checkSignup(name, email, password);
            }
        });

        textViewAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
            }
        });
    }

    private void initViews() {
        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPass = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPass = (EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPass = (EditText)findViewById(R.id.editTextPassword);
        buttonSignup = (Button)findViewById(R.id.buttonSignup);
        textViewAlready = (TextView)findViewById(R.id.textViewLogin);
    }

    private void checkSignup(String name, String email, String password) {
        if(name.length() < 3) {
            Toast.makeText(getApplicationContext(), "Username length must be 3 or more.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.length() < 5) {
            Toast.makeText(getApplicationContext(), "Email length must be 5 or more.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password length must be 6 or more.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!editTextConfirmPass.getText().toString().equals(editTextPass.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Passwords should match.", Toast.LENGTH_SHORT).show();
            return;
        }
        createUser(name, email, password);
    }


    private void initDB() {
        db = openOrCreateDatabase("tafachDB", Context.MODE_PRIVATE, null);
    }


    private void createUser(String username, String email, String password) {
        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().postCreateUser(username, email, password);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            if(response.code() == 201) {
                JsonObject res = response.body();
                String id = res.get("id").getAsString();
                String username = res.get("username").getAsString();
                String email = res.get("email").getAsString();
                util.showToast("Account created");
                startLoginPage();
            } else if(response.code() == 401) {
                JsonObject res = response.body();
                String message = res.get("detail").getAsString();
                util.showError(message);
            } else {
                util.showError("Something went wrong.");
            }
        }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startLoginPage() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
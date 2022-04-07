package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import com.example.aves.R;


public class IntroActivity extends AppCompatActivity {
    private final int splashTimeOut = 1500;
    private SQLiteDatabase db;
    private Intent homeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initDB();

        if(isUserLogged()) {
            homeIntent = new Intent(IntroActivity.this, MainActivity.class);
            int id = getUserId();
            homeIntent.putExtra("user_id", id);
            homeIntent.putExtra("user_name", getUserName(id));
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            homeIntent = new Intent(IntroActivity.this, LoginActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(homeIntent);
                finish();
            }
        }, splashTimeOut);

    }

    private int getUserId() {
        Cursor c = db.rawQuery("SELECT user_id FROM App WHERE index_id=1", null);
        c.moveToFirst();
        return c.getInt(0);
    }

    private String getUserName(int id) {
        Cursor c = db.rawQuery("SELECT user_name FROM Users WHERE user_id=" + id, null);
        c.moveToFirst();
        return c.getString(0);
    }

    private boolean isUserLogged() {
        Cursor c = db.rawQuery("SELECT is_logged FROM App WHERE index_id=1", null);
        if(c.getCount() == 0)
        {
            return false;
        }
        if(c.moveToFirst()) {
            if(c.getInt(0) == 1) return true;
        }
        return false;
    }

    private void initDB() {
        db = openOrCreateDatabase("aves", Context.MODE_PRIVATE, null);
        //create tables
        db.execSQL("CREATE TABLE IF NOT EXISTS Users(user_id INTEGER PRIMARY KEY, user_name VARCHAR, user_email VARCHAR UNIQUE, refreshToken VARCHAR, accessToken VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Contents(user_id INTEGER NOT NULL, content_id VARCHAR NOT NULL, content_key VARCHAR NOT NULL, content_nonce VARCHAR NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS App(index_id INTEGER PRIMARY KEY, user_id INTEGER, is_logged INTEGER DEFAULT 0);");
        try{
            db.execSQL("INSERT INTO App(index_id, user_id, is_logged) VALUES(1, -1, 0);");
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

}
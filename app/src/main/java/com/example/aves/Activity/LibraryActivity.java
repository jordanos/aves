 package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.aves.Domain.UserDomain;
import com.example.aves.Helper.FileChooser;
import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Helper.Util;
import com.example.aves.R;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

 public class LibraryActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private static final int PICKFILE_RESULT_CODE = 1;
    private static final int REQUEST_CODE = 2;
    private Button buttonPickFile;

    private String srcFile;
    private Uri srcUri;

    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        initDB();

        initViews();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        util = new Util(getApplicationContext());
    }

    private void initViews() {
        buttonPickFile = findViewById(R.id.buttonPickFile);
    }

    private void initListeners() {
        buttonPickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile(1);
            }
        });
    }

    private void chooseFile(int code) {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK){
            srcUri = data.getData();
            srcFile = FileChooser.getPath(this, srcUri);
            if(isStoragePermissionGranted()){
                Log.v("upload","Permission is granted");
                try {
                    playContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ActivityCompat.requestPermissions(LibraryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("upload","Permission is granted");
                return true;
            } else {

                Log.v("upload","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("upload","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v("upload","Permission: "+permissions[0]+ "was "+grantResults[0]);
            try {
                playContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playContent() throws IOException {
        byte[] bytes = new byte[16];
        File file = new File(srcFile);
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();
        String contentId = getGuidFromByteArray(bytes);
        getContent(contentId);
    }

    public String getGuidFromByteArray(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid.toString();
    }

    private void getKeysAndPlay(String contentId) {
        int userId = getUserId();
        boolean status = isLocalKeyAvailable(userId, contentId);
        if(status){
            Cursor c = db.rawQuery("SELECT user_id, content_id, content_key, content_nonce FROM Contents WHERE user_id=" + userId +" AND content_id='"+ contentId +"'", null);
            if (c == null) {
                Log.e("key", "cursor is null");
                return;
            }
//            c.moveToFirst();
            Log.e("key", "cursor not null");
            Log.e("key", c.getString(1));
            Log.e("key", c.getString(2));
            Log.e("key", c.getString(3));
            String key = c.getString(2);
            String nonce = c.getString(3);
            startPlaying(key, nonce);
        }else {
            Log.v("key", "not exists");
        }

    }

    private void startPlaying(String key, String nonce) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("nonce", nonce);
        intent.putExtra("uri", srcUri.toString());
        startActivity(intent);
    }



    private void initDB() {
        db = openOrCreateDatabase("aves", Context.MODE_PRIVATE, null);
    }

//    private void getPurchaseInfo(){
//
//        Cursor c = db.rawQuery("SELECT user_id, user_name, user_email, accessToken, refreshtoken FROM Users WHERE user_id=" + userId, null);
//        c.moveToFirst();
//        int userID = c.getInt(0);
//        String userName = c.getString(1);
//        String userEmail = c.getString(2);
//        String accessToken = c.getString(3);
//        String refreshToken = c.getString(4);
//
//        UserDomain user = new UserDomain(userID, userName, userEmail, accessToken, refreshToken);
//        return user;
//    }

    private int getUserId() {
        Cursor c = db.rawQuery("SELECT user_id FROM App WHERE index_id=1", null);
        c.moveToFirst();
        return c.getInt(0);
    }

//    private Boolean isLocalKeyAvailable(int userId, String contentId) {
//        Cursor c = db.rawQuery("EXISTS(SELECT * FROM Contents WHERE content_id='"+ contentId + "' AND user_id=" + userId +");", null);
//        c.moveToFirst();
//        Log.v("content", Integer.toString(c.getInt(0)));
//        c.getInt(0);
//        return false;
//    }
    private boolean isLocalKeyAvailable(int userId, String contentId) {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM Contents WHERE content_id='"+ contentId + "' AND user_id=" + userId, null);
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return c != null;
    }

     private void getContent(String contentId) {
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
                     startPlaying(key, nonce);
                     Toast.makeText(getApplicationContext(), "Content playing", Toast.LENGTH_SHORT).show();
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
}
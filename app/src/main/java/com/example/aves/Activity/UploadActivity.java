package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aves.Helper.FileChooser;
import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Helper.Util;
import com.example.aves.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadActivity extends AppCompatActivity {
    private static final int PICKFILE_RESULT_CODE = 1;
    private static final int REQUEST_CODE = 2;
    private static final int PICKFILE_RESULT_CODE2 = 3;
    private Util util;
    private Button buttonUpload, buttonGetFile, buttonGetImage;
    private EditText editTextName, editTextDescription, editTextPrice, editTextCategory;

    private String srcFile, srcImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initViews();
        initListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        util = new Util(this);
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextCategory = findViewById(R.id.editTextCategory);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonGetFile = findViewById(R.id.buttonGetFile);
        buttonGetImage = findViewById(R.id.buttonGetImage);
    }

    private void initListeners() {
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStoragePermissionGranted()){
                    Log.v("upload","Permission is granted");
                    uploadFile();
                } else {
                    ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                }
            }
        });

        buttonGetFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile(PICKFILE_RESULT_CODE);
            }
        });

        buttonGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile(PICKFILE_RESULT_CODE2);
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
            Uri uri = data.getData();
            srcFile = FileChooser.getPath(this, uri);
        } else if (requestCode == PICKFILE_RESULT_CODE2 && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            srcImage = FileChooser.getPath(this, uri);
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
            uploadFile();
        }
    }


    private void uploadFile() {
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();
        String price = editTextPrice.getText().toString();
        String category = editTextCategory.getText().toString();

        File upload = new File(srcFile);
        File image = new File(srcImage);

//        upload file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), upload);
        MultipartBody.Part bodyUpload =
                MultipartBody.Part.createFormData("upload", upload.getName(), requestFile);
//        image file
        RequestBody requestImage =
                RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part bodyImage =
                MultipartBody.Part.createFormData("image", image.getName(), requestImage);

//      user  data to be sent
        RequestBody bName = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody bDescription = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody bPrice = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody bCategory = RequestBody.create(MediaType.parse("text/plain"), category);

        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().postUploadFile(bodyImage, bodyUpload, bName, bDescription, bPrice, bCategory);

        util.showProgress();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                util.hideProgress();
                if (response.code() == 201) {
                    JsonObject res = response.body();
                    util.showToast("success");
                } else {
                    util.showError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                util.hideProgress();
                if (t instanceof IOException) {
                    util.showError("Network Error.");
                } else {
                    util.showError("Conversion Error.");
                }
            }
        });
    }
}
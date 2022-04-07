package com.example.aves.Helper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aves.Interface.Api;
import com.example.aves.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class Util {
    private Context context;
    private ProgressDialog progressDialog;
    private boolean isProgressDialog;
    private ConnectivityManager cm;


    public Util(Context context) {
        this.context = context;
        this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        isProgressDialog = false;
    }

    public void showProgress() {
        if(!this.isProgressDialog){
            progressDialog = new ProgressDialog(this.context);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.isProgressDialog = true;
        }
    }

    public void hideProgress() {
        if(isProgressDialog) {
            progressDialog.dismiss();
            this.isProgressDialog = false;
        }
    }

    public Boolean isConnected() {
        NetworkInfo activeNetwork = this.cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showWarning(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getMediaUrl(String url){
        String newUrl = Api.BASE_URL + "media" + url;

        return  newUrl;
    }

}

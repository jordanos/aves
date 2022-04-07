package com.example.aves.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aves.Adapter.CartListAdapter;
import com.example.aves.Domain.FoodDomain;
import com.example.aves.Helper.Locations;
import com.example.aves.Helper.ManagementCart;
import com.example.aves.Interface.ChangeNumberItemsListener;
import com.example.aves.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartListActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private static final String BASE_URL = "https://tafach-delivery-server.herokuapp.com/";
    private RequestQueue mRequestQueue;
    private ArrayList<FoodDomain> foodlist;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    private SQLiteDatabase db;
    private int user_id;
    private String user_name;
    private ManagementCart managementCart;
    private TextView totalFeeTxt, taxTxt, deliveryTxt, totalTxt, emptyTxt;
    private double tax;
    private ScrollView scrollView;
    private Button buttonCheckout;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    private String order_location;
    private String from_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getInt("user_id");
        user_name = bundle.getString("user_name");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        managementCart = new ManagementCart(this);

        initDB();
        initView();
        initList();
        refreshCart();

        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(managementCart.getListCard().size() > 0) {
                    from_location = Locations.getLocation();
                    getLastLocation();
                } else {
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private boolean getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            order_location = location.getLatitude() + "," + location.getLongitude();
                            double percentTax = 0.02;
                            double delivery = 10;

                            tax = Math.round((managementCart.getTotalFee() * percentTax) * 100.0) / 100.0;
                            double total_price = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
                            int items_count = managementCart.getItemsCount();
                            getData(parseAddOrderUrl(user_id, items_count, total_price, order_location, from_location));
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location and try again...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            order_location = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
            double percentTax = 0.02;
            double delivery = 10;

            tax = Math.round((managementCart.getTotalFee() * percentTax) * 100.0) / 100.0;
            double total_price = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
            int items_count = managementCart.getItemsCount();
            getData(parseAddOrderUrl(user_id, items_count, total_price, order_location, from_location));
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void getData(String url) {
        progressDialog = new ProgressDialog(CartListActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ConnectivityManager cm = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                if(response.getInt("status") == 200) {
                                    Intent intent = new Intent(CartListActivity.this, YenepayActivity.class);
//                                intent.putExtra("user_id", user_id);
//                                intent.putExtra("user_name", user_name);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                                    Uri uriNotify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                                            .setSmallIcon(R.drawable.burger)
                                            .setContentTitle("Checkout Pending")
                                            .setContentText("Please complete your payment.")
                                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                            .setSound(uriNotify)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            // Set the intent that will fire when the user taps the notification
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true);

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(CartListActivity.this);

                                    // notificationId is a unique int for each notification that you must define
                                    notificationManager.notify(1, builder.build());

                                    foodlist.clear();
                                    managementCart.clearCart();
                                    adapter.notifyDataSetChanged();
                                    refreshCart();
                                } else {
                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    if( error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), "Network Error, please try again.", Toast.LENGTH_SHORT).show();
                    } else if( error instanceof ServerError) {
                        Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                    } else if( error instanceof AuthFailureError) {
                        Toast.makeText(getApplicationContext(), "Auth Error", Toast.LENGTH_SHORT).show();
                    } else if( error instanceof ParseError) {
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                    } else if( error instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(), "No Connection Error", Toast.LENGTH_SHORT).show();
                    } else if( error instanceof TimeoutError) {
                        Toast.makeText(getApplicationContext(), "Timeout Error, make sure internet is available.", Toast.LENGTH_SHORT).show();
                    }                }
            });
            mRequestQueue.add(request);
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Enable internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String parseAddOrderUrl(int user_id, int items_count, Double total_price, String order_location, String from_location) {
        return BASE_URL + "add_order?user_id=" + user_id + "&items_count=" + items_count + "&total_price=" + total_price + "&order_location=" + order_location + "&from_location=" + from_location;
    }

    private void initDB() {
        mRequestQueue = Volley.newRequestQueue(this);
        db = openOrCreateDatabase("tafachDB", Context.MODE_PRIVATE, null);
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        foodlist = managementCart.getListCard();
        adapter = new CartListAdapter(foodlist, this, new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                refreshCart();
            }
        });

        recyclerViewList.setAdapter(adapter);
        refreshCart();
    }

    private void refreshCart() {
        double percentTax = 0.02;
        double delivery = 10;

        tax = Math.round((managementCart.getTotalFee() * percentTax) * 100.0) / 100.0;
        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managementCart.getTotalFee() * 100.0) / 100.0;

        totalFeeTxt.setText("ETB " + itemTotal);
        taxTxt.setText("ETB " + tax);
        deliveryTxt.setText("ETB " + delivery);
        totalTxt.setText("ETB " + total);

        if (foodlist.isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        recyclerViewList = findViewById(R.id.recyclerview);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.taxTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        emptyTxt = findViewById(R.id.emptyTxt);
        scrollView = findViewById(R.id.scrollView4);
        buttonCheckout = (Button)findViewById(R.id.btn_checkout);
    }
}
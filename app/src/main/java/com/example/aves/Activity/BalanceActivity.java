package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import com.example.aves.Adapter.BalanceAdapter;
import com.example.aves.Domain.OrderDomain;
import com.example.aves.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BalanceActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private static final String BASE_URL = "https://tafach-delivery-server.herokuapp.com/";
    private RequestQueue mRequestQueue;
    private ArrayList<OrderDomain> orderlist = new ArrayList<>();
    private BalanceAdapter adapter;
    private RecyclerView recyclerViewList;
    private SQLiteDatabase db;
    private int user_id;
    private TextView textViewNoOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getInt("user_id");

        initDB();
        initView();
        initList();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pull_to_refresh_orders);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(parseGetOrderUrl(user_id));
                pullToRefresh.setRefreshing(false);
            }
        });

        getData(parseGetOrderUrl(user_id));
    }

    private void initDB() {
        mRequestQueue = Volley.newRequestQueue(this);
        db = openOrCreateDatabase("tafachDB", Context.MODE_PRIVATE, null);
    }

    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        adapter = new BalanceAdapter(orderlist);
        adapter.setOnItemClickListener(new BalanceAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                String orderLocation = orderlist.get(position).getOrder_location();
                String fromLocation = orderlist.get(position).getFrom_location();
                if(orderLocation.split(",").length == 2 && fromLocation.split(",").length == 2){
                    String orderLat = orderlist.get(position).getOrder_location().split(",")[0];
                    String orderLong = orderlist.get(position).getOrder_location().split(",")[1];
                    String fromLat = orderlist.get(position).getFrom_location().split(",")[0];
                    String fromLong = orderlist.get(position).getFrom_location().split(",")[1];
                    String getQuery = "google.navigation:q=" + fromLat + "," + fromLong;
                    Uri gmmIntentUri = Uri.parse(getQuery);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Can not find maps application to open location tracking.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error while opening location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerViewList.setAdapter(adapter);
    }

    private String parseGetOrderUrl(int user_id) {
        return BASE_URL + "get_order?user_id=" + user_id;
    }

    private void getData(String url) {
        mRequestQueue.stop();
        orderlist.clear();
        adapter.notifyDataSetChanged();

        progressDialog = new ProgressDialog(BalanceActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ConnectivityManager cm = (ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                if(response.getInt("status") == 200) {
                                    JSONArray jsonArray = response.getJSONArray("message");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject order = jsonArray.getJSONObject(i);
                                        int order_id = order.getInt("order_id");
                                        int user_id = order.getInt("user_id");
                                        int items_count = order.getInt("items_count");
                                        Double total_price = order.getDouble("total_price");
                                        String order_status = order.getString("order_status");
                                        String order_location = order.getString("order_location");
                                        String from_location = order.getString("from_location");

                                        orderlist.add(new OrderDomain(order_id, user_id, items_count, total_price, order_status, order_location, from_location));
                                        adapter.notifyDataSetChanged();
                                    }

                                    if (orderlist.size() > 0) {
                                        Toast.makeText(getApplicationContext(), "Tap on one of the orders and track your food.", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    textViewNoOrders.setVisibility(View.VISIBLE);
                                    recyclerViewList.setVisibility(View.GONE);                                }
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
            mRequestQueue.start();
            mRequestQueue.add(request);
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Enable internet connection and try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        textViewNoOrders = (TextView)findViewById(R.id.text_no_orders);
        recyclerViewList = findViewById(R.id.recycler_orders);
    }
}
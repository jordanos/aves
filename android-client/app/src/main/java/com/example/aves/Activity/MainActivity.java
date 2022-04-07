package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aves.Adapter.ContentsAdapter;
import com.example.aves.Domain.ContentDomain;
import com.example.aves.Domain.FoodDomain;
import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Helper.Util;
import com.example.aves.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    private static int FILTER_TYPE = 0;
    private int user_id;

    private ContentsAdapter adapter;
    private ArrayList<ContentDomain> contentList = new ArrayList<>();
    private RecyclerView recyclerViewContentsList;
    private GridLayoutManager manager;

    private TextView textViewCat, textViewName;

    private SQLiteDatabase db;
    private Util util;


    private boolean hasNext = true;
    private int page = 1;
    private boolean isScrolling = false;

    private ProgressDialog progressDialog;
    private boolean isProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        user_id = bundle.getInt("user_id");
        initDB();
        initViews();
        bottomNavigation();
        util = new Util(this);

        recyclerViewContentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentItems = manager.getChildCount();
                int totalItems = manager.getItemCount();
                int scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    if(hasNext){
                        getContents();
                    }
                }
            }
        });

        getContents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        util = new Util(this);
    }

    private void initDB() {
        db = openOrCreateDatabase("tafachDB", Context.MODE_PRIVATE, null);
    }

    private void initViews() {
        recyclerViewContents();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pull_to_refresh_main);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contentList.clear();
                getContents();
                pullToRefresh.setRefreshing(false);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    private void bottomNavigation() {
        FloatingActionButton floatingActionButton = findViewById(R.id.card_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout layoutSearch = findViewById(R.id.layoutSearch);
        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(contactIntent);
            }
        });

        LinearLayout layoutLibrary = findViewById(R.id.layoutLibrary);
        layoutLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(MainActivity.this, LibraryActivity.class);
                contactIntent.putExtra("user_id", user_id);
                startActivity(contactIntent);
            }
        });

        LinearLayout layoutProfile = findViewById(R.id.layoutProfile);
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileIntent.putExtra("user_id", user_id);
                startActivity(profileIntent);
            }
        });
//        layoutLibrary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popup = new PopupMenu(MainActivity.this, view);
//                //Inflating the Popup using xml file
//                popup.getMenuInflater().inflate(R.menu.settings, popup.getMenu());
//
//                //registering popup with OnMenuItemClickListener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.item_logout:
//                                logout();
//                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
//                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(loginIntent);
//                                return true;
//                            case R.id.item_orders:
//                                Intent ordersIntent = new Intent(MainActivity.this, BalanceActivity.class);
//                                ordersIntent.putExtra("user_id", user_id);
//                                startActivity(ordersIntent);
//                                return true;
//                            default:
//                                return false;
//                        }
//                    }
//                });
//                popup.show();
//            }
//        });
    }

    private void logout() {
        db.execSQL("DELETE FROM Users WHERE user_id=" + user_id);
        db.execSQL("UPDATE App SET user_id=0, is_logged=0 WHERE index_id=1");
    }

    private void showProgress() {
        if(this.isProgressDialog){
           return;
        }
        progressDialog.show();
        this.isProgressDialog = true;
    }

    private void hideProgress() {
        if(!this.isProgressDialog){
            return;
        }
        progressDialog.dismiss();
        this.isProgressDialog = false;
    }

    private void recyclerViewContents() {
        manager = new GridLayoutManager(this, 2);
        recyclerViewContentsList = findViewById(R.id.recyclerView);
        recyclerViewContentsList.setLayoutManager(manager);
        contentList = new ArrayList<>();
        adapter = new ContentsAdapter(contentList);

        recyclerViewContentsList.setAdapter(adapter);
    }

    private void refreshActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void setFilterType(int filterType) {
        page = 1;
        hasNext = true;
        getContents();
    }

    private void getContents() {
        Call<JsonObject> call;

        if(FILTER_TYPE == 0){
            call = RetrofitClient.getInstance().getMyApi().getContentsPopular(page);
        } else {
            call = RetrofitClient.getInstance().getMyApi().getContentsNew(page);
        }

        hasNext = false;
        showProgress();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                hideProgress();
                if(response.code() == 200) {
                    JsonObject res = response.body();

                    JsonArray contents =  res.getAsJsonArray("results");
//                    Log.v("content", contents.get(1).getAsJsonObject().get("id").getAsString());
                    for (int i = 0; i < contents.size(); i++) {
                        JsonObject content = contents.get(i).getAsJsonObject();
                        String id = content.get("id").getAsString();
                        String name = content.get("name").getAsString();
                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                        JsonElement image = content.get("image");
                        String img = "";
                        if(!image.isJsonNull()) {
                            img = image.getAsString();
                        }
                        int likes = content.get("likes").getAsInt();
                        contentList.add(new ContentDomain(id, name, img, likes));
//                        adapter.notifyItemInserted(i);
                    }
                    adapter.notifyDataSetChanged();
                    if(res.get("next").isJsonNull()) {
                        hasNext = false;
                    } else {
                        hasNext = true;
                        page ++;
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                hideProgress();
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }
        });
    }
}
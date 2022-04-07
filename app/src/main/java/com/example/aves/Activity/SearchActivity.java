package com.example.aves.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aves.Adapter.ContentsAdapter;
import com.example.aves.Adapter.SearchAdapter;
import com.example.aves.Domain.ContentDomain;
import com.example.aves.Helper.RetrofitClient;
import com.example.aves.Helper.Util;
import com.example.aves.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class SearchActivity extends AppCompatActivity {
    Util util;

    private EditText editTextSearch;

    private SearchAdapter adapter;
    private ArrayList<ContentDomain> contentList = new ArrayList<>();
    private RecyclerView recyclerViewContentsList;
    private LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initListeners();
        initListViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        util = new Util(this);
    }

    private void initListViews() {
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewContentsList = findViewById(R.id.recyclerViewSearch);
        recyclerViewContentsList.setLayoutManager(manager);
        contentList = new ArrayList<>();
        adapter = new SearchAdapter(contentList);

        recyclerViewContentsList.setAdapter(adapter);
    }


    private void initViews() {
        editTextSearch = findViewById(R.id.editTextSearch);
    }

    private void initListeners() {
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = editTextSearch.getText().toString();
                    if(query.length() > 2){
                        performSearch(query);
                    } else {
                        Toast.makeText(getApplicationContext(), "Search string must be at least 3", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch(String query) {
        contentList.clear();
        adapter.notifyDataSetChanged();

        util.showProgress();

        Call<JsonObject> call = RetrofitClient.getInstance().getMyApi().getSearchContent(query);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                util.hideProgress();
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
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                util.hideProgress();
                Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_LONG).show();
            }
        });
    }

}
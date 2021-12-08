package com.togtokh.monuz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.togtokh.monuz.adepter.AllGenreListAdepter;
import com.togtokh.monuz.list.GenreList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllGenre extends AppCompatActivity {
    Context context = this;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView allGenreRecylerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_genre);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        allGenreRecylerView = findViewById(R.id.allGenreRecylerView);

        loadGenre();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadGenre();
        });
    }

    private void loadGenre() {
        List<GenreList> genreList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_all_genre.php", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String icon = rootObject.get("icon").getAsString();
                    String description = rootObject.get("description").getAsString();
                    int featured = rootObject.get("featured").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        genreList.add(new GenreList(id, name, icon, description, featured, status));
                    }
                }

                AllGenreListAdepter myadepter = new AllGenreListAdepter(context, genreList);
                allGenreRecylerView.setLayoutManager(new GridLayoutManager(context, 5, RecyclerView.VERTICAL, false));
                allGenreRecylerView.setAdapter(myadepter);
            }
            swipeRefreshLayout.setRefreshing(false);
        }, error -> {
            swipeRefreshLayout.setRefreshing(false);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
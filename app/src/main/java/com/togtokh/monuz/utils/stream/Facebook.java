package com.togtokh.monuz.utils.stream;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.togtokh.monuz.AppConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Facebook {
    public interface FacebookCallback {
        void onSuccess(JsonObject result);
        void onError(VolleyError error);
    }

    public static void getStreamLink(Context context, String url, final Facebook.FacebookCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, AppConfig.url+"/api/fetch/facebook/fbfetch.php?url="+url, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            if(jsonObject.get("links").getAsJsonObject() != null) {
                JsonObject links = jsonObject.get("links").getAsJsonObject();
                callback.onSuccess(links);
            } else {
                JsonObject links = null;
                callback.onSuccess(links);
            }
        }, error -> callback.onError(error));
        queue.add(sr);
    }
}

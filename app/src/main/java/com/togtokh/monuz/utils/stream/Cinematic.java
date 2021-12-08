package com.togtokh.monuz.utils.stream;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Cinematic {
    public interface CinematicCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }
    public static void getStreamLink(Context context, String url, final Cinematic.CinematicCallback callback) {
        String[] parts = url.split("/");
        String id = parts[4];
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, "https://api.mygp.cinematic.mobi/api/v1/content/detail-wap/"+id, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            String link = jsonObject.get("file").getAsString();
            callback.onSuccess(link);
        }, error -> callback.onError(error));
        queue.add(sr);
    }
}

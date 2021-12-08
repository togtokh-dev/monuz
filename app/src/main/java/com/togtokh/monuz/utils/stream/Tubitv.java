package com.togtokh.monuz.utils.stream;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Tubitv {
    public interface TubitvCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }
    public static void getStreamLink(Context context, String url, final Tubitv.TubitvCallback callback) {
        String[] parts = url.split("/");
        String id = parts[4];
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, "https://tubitv.com/oz/videos/"+id+"/content", response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            JsonArray video_resources = jsonObject.get("video_resources").getAsJsonArray();
            JsonObject rawManifest = video_resources.get(0).getAsJsonObject();
            JsonObject manifest = rawManifest.get("manifest").getAsJsonObject();
            String link = manifest.get("url").getAsString();
            callback.onSuccess(link);
        }, error -> callback.onError(error));
        queue.add(sr);
    }
}

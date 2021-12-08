package com.togtokh.monuz.utils.stream;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Vimeo {
    public interface vimeoCallback {
        void onSuccess(JsonArray result);
        void onError(VolleyError error);
    }

    public static void getStreamLink(Context context, String url, final Vimeo.vimeoCallback callback) {
        String[] parts = url.split("/");
        String id = parts[3];

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, "https://player.vimeo.com/video/"+id+"/config", response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            JsonObject requestObj = jsonObject.get("request").getAsJsonObject();
            JsonObject filesObj = requestObj.get("files").getAsJsonObject();
            JsonArray progressiveObj = filesObj.get("progressive").getAsJsonArray();
            callback.onSuccess(progressiveObj);
        }, error -> callback.onError(error));
        queue.add(sr);
    }
}

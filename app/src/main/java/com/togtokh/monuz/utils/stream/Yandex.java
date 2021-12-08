package com.togtokh.monuz.utils.stream;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Yandex {
    public interface yandexCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }

    public static void getStreamLink(Context context, String url, final Yandex.yandexCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, "https://cloud-api.yandex.net/v1/disk/public/resources/download?public_key="+ Uri.parse(url), response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            String href = jsonObject.get("href").getAsString();
            callback.onSuccess(href);
        }, error -> callback.onError(error));
        queue.add(sr);
    }
}

package com.togtokh.monuz.utils.stream;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GoFile {
    public interface goFileCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }

    public static void getStreamLink(Context context, String url, final goFileCallback callback) {
        String[] parts = url.split("/");
        String id = parts[4];
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, "https://api.gofile.io/getFolder?folderId="+id, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            JsonObject data = jsonObject.get("data").getAsJsonObject();
            JsonObject contents = data.get("contents").getAsJsonObject();
            for (String keyStr : contents.keySet()) {
                JsonObject md5 = contents.get(keyStr).getAsJsonObject();
                String link = md5.get("link").getAsString();
                callback.onSuccess(link);
            }
        }, error -> callback.onError(error));
        queue.add(sr);
    }
}

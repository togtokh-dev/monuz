package com.togtokh.monuz.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.togtokh.monuz.list.YTStreamList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Yts {

    public interface VolleyCallback {
        void onSuccess(List<YTStreamList> result);
        void onError(VolleyError error);
    }
    public interface VolleyCallback2 {
        void onSuccess(String result);
        void onError(VolleyError error);
    }


    public static void getlinks(Context context, String url, final VolleyCallback callback) {
        List<YTStreamList> qualityItems = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, "https://yt1s.com/api/ajaxSearch/index", response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            if(jsonObject.has("vid")) {
                String vid = jsonObject.get("vid").getAsString();

                JsonObject links = jsonObject.get("links").getAsJsonObject();
                JsonObject mp4 = links.get("mp4").getAsJsonObject();

                for (String keyStr : mp4.keySet()) {

                    JsonObject video = mp4.get(keyStr).getAsJsonObject();
                    String q = video.get("q").getAsString();
                    String k = video.get("k").getAsString();
                    qualityItems.add(new YTStreamList(q, vid, k));

                }
                callback.onSuccess(qualityItems);
            } else {
                callback.onError(new VolleyError("Invalid URL"));
            }
        }, error -> {
            callback.onError(error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("cache-control", "no-cache");
                params.put("content-type", "application/x-www-form-urlencoded");
                return params;

            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("q", url);
                return params;
            }
        };
        queue.add(sr);
    }

    public static void getStreamLinks(Context context, String k, String vid, final VolleyCallback2 callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.POST, "https://yt1s.com/api/ajaxConvert/convert", response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            String dlink = jsonObject.get("dlink").getAsString();
            callback.onSuccess(dlink);
           // Player.qualityItems.add(new YTStreamList(q, dlink));
            //Player.ytMultipleQualityDialog(context);
        }, error -> {
            callback.onError(error);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("cache-control", "no-cache");
                params.put("content-type", "application/x-www-form-urlencoded");
                return params;

            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("k", k);
                params.put("vid", vid);
                return params;
            }
        };
        queue.add(sr);
    }
}

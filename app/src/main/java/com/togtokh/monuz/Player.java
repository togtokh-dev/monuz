package com.togtokh.monuz;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.togtokh.monuz.db.resume_content.ResumeContent;
import com.togtokh.monuz.db.resume_content.ResumeContentDatabase;
import com.togtokh.monuz.dialog.TrackSelectionDialog;
import com.togtokh.monuz.list.MultiqualityList;
import com.togtokh.monuz.list.YTStreamList;
import com.togtokh.monuz.utils.HelperUtils;
import com.togtokh.monuz.utils.Utils;
import com.togtokh.monuz.utils.Yts;
import com.togtokh.monuz.utils.stream.Cinematic;
import com.togtokh.monuz.utils.stream.Facebook;
import com.togtokh.monuz.utils.stream.GoFile;
import com.togtokh.monuz.utils.stream.Tubitv;
import com.togtokh.monuz.utils.stream.Vimeo;
import com.togtokh.monuz.utils.stream.Yandex;
import com.github.vkay94.dtpv.DoubleTapPlayerView;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import es.dmoral.toasty.Toasty;

public class Player extends AppCompatActivity {
    Context context = this;
    private DoubleTapPlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;

    YouTubeOverlay youtube_overlay;

    DataSource.Factory factory;

    private DefaultTrackSelector trackSelector;

    int skip_available;
    String intro_start;
    String intro_end;
    Button Skip_Intro_btn;

    String ContentType = null;
    int Current_List_Position = 0;

    Button Play_Next_btn;

    String Next_Ep_Avilable;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    Boolean contentLoaded = false;

    LowCostVideo xGetter;
    static ProgressDialog progressDialog;

    ResumeContentDatabase db;
    int resumeContentID;

    long resumePosition = 0;

    String userData = null;
    int userId;

    String mContentType = "";

    int wsType;

    int sourceID;

    int ct;

    MergingMediaSource nMediaSource = null;

    PowerManager.WakeLock wakeLock;

    int maxBrightness;

    String shouldInterceptRequestURL = "";

    String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        if(AppConfig.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Player:No Sleep");
        wakeLock.acquire(300*60*1000L /*300 minutes*/);

        maxBrightness = getMaxBrightness(this, 1000);

        loadData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);

        xGetter = new LowCostVideo(this);
        xGetter.onFinish(new LowCostVideo.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                progressDialog.dismiss();
                if (multiple_quality){
                    if (vidURL!=null) {
                        //This video you can choose qualities
                        for (XModel model : vidURL) {
                            //If google drive video you need to set cookie for play or download
                        }
                        multipleQualityDialog(vidURL);
                    }else done(null);
                }else {
                    done(vidURL.get(0));
                }
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
                done(null);
            }
        });

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        View decorView = getWindow().getDecorView();
        int uiOptions =  View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_player);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Player.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Player.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }


        playerView = findViewById(R.id.player_view);

        youtube_overlay = findViewById(R.id.ytOverlay);
        youtube_overlay.performListener(new YouTubeOverlay.PerformListener() {
            @Override
            public void onAnimationStart() {
                youtube_overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
                youtube_overlay.setVisibility(View.GONE);
            }

            @SuppressLint("WrongConstant")
            @Nullable
            @Override
            public Boolean shouldForward(@NotNull com.google.android.exoplayer2.Player player, @NotNull DoubleTapPlayerView doubleTapPlayerView, float v) {
                if (player.getPlaybackState() == PlaybackState.STATE_ERROR ||
                        player.getPlaybackState() == PlaybackState.STATE_NONE ||
                        player.getPlaybackState() == PlaybackState.STATE_STOPPED) {

                    playerView.cancelInDoubleTapMode();
                    return null;
                }

                if (player.getCurrentPosition() > 500 && v < playerView.getWidth() * 0.35)
                    return false;

                if (player.getCurrentPosition() < player.getDuration() && v > playerView.getWidth() * 0.65)
                    return true;

                return null;
            }
        });

        Intent intent = getIntent();
        int contentID = intent.getExtras().getInt("contentID");
        sourceID = intent.getExtras().getInt("SourceID");
        String name = intent.getExtras().getString("name");
        source = intent.getExtras().getString("source");
        String url = intent.getExtras().getString("url");


        if(intent.getExtras().getString("Content_Type") != null) {
            mContentType = intent.getExtras().getString("Content_Type");

            if(mContentType.equals("Movie")) {
                ct = 1;
            } else if(mContentType.equals("WebSeries")) {
                ct = 2;
            }
        }

        //ResumePlayback
        db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
        resumePosition = intent.getExtras().getLong("position");

        TextView contentFirstName = findViewById(R.id.contentFirstName);
        TextView contentSecondName = findViewById(R.id.contentSecondName);
        contentSecondName.setText(name);

        if(mContentType.equals("Movie")) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_movie_details.php?ID="+contentID, response -> {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                contentFirstName.setText(jsonObject.get("name").getAsString());

                String releaseDate = "";
                if (!jsonObject.get("release_date").getAsString().equals("")) {
                    releaseDate = jsonObject.get("release_date").getAsString();
                }

                int type = jsonObject.get("type").getAsInt();

                db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
                if(db.resumeContentDao().getResumeContentid(contentID) == 0) {
                    db.resumeContentDao().insert(new ResumeContent(0, contentID, source, url, jsonObject.get("poster").getAsString(), jsonObject.get("name").getAsString(), releaseDate, 0, 0, intent.getExtras().getString("Content_Type"), type));
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                } else {
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                    db.resumeContentDao().updateSource(source, url, type, resumeContentID);
                }

                if(userData != null) {
                    HelperUtils.setWatchLog(context, String.valueOf(userId), jsonObject.get("id").getAsInt(), 1, AppConfig.apiKey);
                } else {
                    HelperUtils.setWatchLog(context, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), jsonObject.get("id").getAsInt(),1, AppConfig.apiKey);
                }

            }, error -> {
                // Do nothing
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("x-api-key", AppConfig.apiKey);
                    return params;
                }
            };
            queue.add(sr);
        } else if(mContentType.equals("WebSeries")) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_webseries_details.php?ID="+contentID, response -> {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                contentFirstName.setText(jsonObject.get("name").getAsString());

                String releaseDate = "";
                if(!jsonObject.get("release_date").getAsString().equals("")) {
                    releaseDate = jsonObject.get("release_date").getAsString();
                }

                int type = jsonObject.get("type").getAsInt();
                wsType = type;

                db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
                if(db.resumeContentDao().getResumeContentid(contentID) == 0) {
                    db.resumeContentDao().insert(new ResumeContent(0, contentID, source, url, jsonObject.get("poster").getAsString(), jsonObject.get("name").getAsString(), releaseDate, 0, 0, intent.getExtras().getString("Content_Type"), type));
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                } else {
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                }

                if(userData != null) {
                    HelperUtils.setWatchLog(context, String.valueOf(userId), jsonObject.get("id").getAsInt(), 2, AppConfig.apiKey);
                } else {
                    HelperUtils.setWatchLog(context, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), jsonObject.get("id").getAsInt(),2, AppConfig.apiKey);
                }

            }, error -> {
                // Do nothing
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


        int content_type = intent.getExtras().getInt("content_type");
        ImageView Live_logo =findViewById(R.id.Live_logo);
        if(content_type == 3) {
            Live_logo.setVisibility(View.VISIBLE);
        } else {
            Live_logo.setVisibility(View.GONE);
        }

        skip_available = intent.getExtras().getInt("skip_available");
        intro_start = intent.getExtras().getString("intro_start");
        intro_end = intent.getExtras().getString("intro_end");

        if(intent.getExtras().getString("Content_Type") != null || intent.getExtras().getString("Current_List_Position") != null || intent.getExtras().getString("Next_Ep_Avilable") != null) {
            ContentType = intent.getExtras().getString("Content_Type");
            Current_List_Position = intent.getExtras().getInt("Current_List_Position");
            Next_Ep_Avilable = intent.getExtras().getString("Next_Ep_Avilable");

        }

        if(resumePosition == 0) {
            if (db.resumeContentDao().getResumeContentid(contentID) != 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Resume!");
                alertDialog.setMessage("Do You Want to Resume From Where You Left?");

                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Start Over", (dialog, which) -> {
                    if(mContentType.equals("Movie")) {
                        dialog.dismiss();
                        Prepare_Source(source, url);
                    } else if(mContentType.equals("WebSeries")) {
                        dialog.dismiss();
                        Prepare_Source(source, url);
                        db.resumeContentDao().updateSource(source, url, wsType, resumeContentID);
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Resume", (dialog, which) -> {
                    if(mContentType.equals("Movie")) {
                        dialog.dismiss();
                        resumePosition = db.resumeContentDao().getResumeContentPosition(contentID);
                        Prepare_Source(source, url);
                    } else if(mContentType.equals("WebSeries")) {
                        resumePosition = db.resumeContentDao().getResumeContentPosition(contentID);
                        Prepare_Source(db.resumeContentDao().getResumeContentSourceType(contentID), db.resumeContentDao().getResumeContentSourceUrl(contentID));
                        contentSecondName.setText(db.resumeContentDao().getResumeContentName(contentID));
                    }

                });
                alertDialog.show();
            } else {
                Prepare_Source(source, url);
            }
        } else {
            Prepare_Source(source, url);
        }

        ImageView img_full_scr = findViewById(R.id.img_full_scr);
        img_full_scr.setOnClickListener(view -> {
            if(playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                img_full_scr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));
            } else if(playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                img_full_scr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
            } else {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                img_full_scr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));
            }
        });

        ImageView img_settings = findViewById(R.id.img_settings);
        img_settings.setOnClickListener(view -> {
            if(contentLoaded) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
                DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector,
                                /* onDismissListener= */ dismissedDialog -> {
                                });
                trackSelectionDialog.show(getSupportFragmentManager(), null);
            } else {
                Toasty.warning(this, "Please Wait! Content Not Loaded.", Toast.LENGTH_SHORT, true).show();
            }
        });

        ////
        factory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "KAIOS"));


        ImageView Back_btn_img = findViewById(R.id.Back_btn_img);
        Back_btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBackPressed = true;
                releasePlayer();
                handler.removeCallbacks(runnable);
                finish();
            }
        });

        Skip_Intro_btn = findViewById(R.id.Skip_Intro_btn);
        Skip_Intro_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.seekTo(Get_mil_From_Time(intro_end));
            }
        });

        handler.post(runnable);

        FullScreencall();

        Play_Next_btn = findViewById(R.id.Play_Next_btn);
        Play_Next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContentType.equals("WebSeries")) {
                    if(Next_Ep_Avilable.equals("Yes")) {
                        Play_Next_btn.setText("Playing Now");
                        Intent intent = new Intent();
                        intent.putExtra("Current_List_Position", Current_List_Position);
                        setResult(RESULT_OK, intent);

                        isBackPressed = true;
                        releasePlayer();
                        handler.removeCallbacks(runnable);
                        finish();
                    }
                }
            }
        });

        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );
        DialogPropertiesSignal properties = builder.getDialogProperties();
        properties.setConnectionCallback(new ConnectionCallback() { // Optional
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                // ...
            }
        });
        properties.setCancelable(true); // Optional
        properties.setNoInternetConnectionTitle("No Internet"); // Optional
        properties.setNoInternetConnectionMessage("Check your Internet connection and try again"); // Optional
        properties.setShowInternetOnButtons(true); // Optional
        properties.setPleaseTurnOnText("Please turn on"); // Optional
        properties.setWifiOnButtonText("Wifi"); // Optional
        properties.setMobileDataOnButtonText("Mobile data"); // Optional

        properties.setOnAirplaneModeTitle("No Internet"); // Optional
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode."); // Optional
        properties.setPleaseTurnOffText("Please turn off"); // Optional
        properties.setAirplaneModeOffButtonText("Airplane mode"); // Optional
        properties.setShowAirplaneModeOffButtons(true); // Optional
        builder.build();


        ImageView imgBrightness = findViewById(R.id.img_Brightness);
        BoxedVertical brightness = findViewById(R.id.brightness);
        ConstraintLayout brightnessLayout = findViewById(R.id.brightnessLayout);

        imgBrightness.setOnClickListener(view->{
            if (brightnessLayout.getVisibility() == View.VISIBLE) {
                brightnessLayout.setVisibility(View.GONE);
            } else if (brightnessLayout.getVisibility() == View.GONE) {
                brightnessLayout.setVisibility(View.VISIBLE);
            }
        });

        brightness.setMax(maxBrightness);
        brightness.setValue(getBrightness(this));
        setBrightness(Player.this, getBrightness(this));
        brightness.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                setBrightness(Player.this, boxedPoints.getValue());
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {

            }
        });

        ImageView imgVolume = findViewById(R.id.img_Volume);
        BoxedVertical volume = findViewById(R.id.volume);
        ConstraintLayout volumeLayout = findViewById(R.id.volumeLayout);

        imgVolume.setOnClickListener(view->{
            if (volumeLayout.getVisibility() == View.VISIBLE) {
                volumeLayout.setVisibility(View.GONE);
            } else if (volumeLayout.getVisibility() == View.GONE) {
                volumeLayout.setVisibility(View.VISIBLE);
            }
        });

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume= am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volumeLevel= am.getStreamVolume(AudioManager.STREAM_MUSIC);

        volume.setMax(maxVolume);
        volume.setValue(volumeLevel);
        volume.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, boxedPoints.getValue(), 0);
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {

            }
        });
    }

    int getMaxBrightness(Context context, int defaultValue){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            Field[] fields = powerManager.getClass().getDeclaredFields();
            for (Field field: fields) {
                if(field.getName().equals("BRIGHTNESS_ON")) {
                    field.setAccessible(true);
                    try {
                        return (int) field.get(powerManager);
                    } catch (IllegalAccessException e) {
                        return defaultValue;
                    }
                }
            }
        }
        return defaultValue;
    }

    public void setBrightness(Context context, int brightness){
        //ContentResolver cResolver = context.getContentResolver();
        //Settings.System.putInt(cResolver,  Settings.System.SCREEN_BRIGHTNESS,brightness);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = (float) brightness/maxBrightness;
        getWindow().setAttributes(layout);

    }

    public static int getBrightness(Context context) {
        ContentResolver cResolver = context.getContentResolver();
        try {
            return Settings.System.getInt(cResolver,  Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }


    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(simpleExoPlayer != null) {
                if (simpleExoPlayer.isPlaying()) {
                    long apprxDuration = simpleExoPlayer.getDuration() - 5000;
                    if (simpleExoPlayer.getCurrentPosition() > apprxDuration) {
                        db.resumeContentDao().delete(resumeContentID);
                    } else {
                        db.resumeContentDao().update(simpleExoPlayer.getCurrentPosition(), resumeContentID);
                    }
                }
            }

            //Skip Feature
            if(skip_available == 1) {
                if(simpleExoPlayer != null) {
                    if(intro_start.equals("") || intro_start.equals("0") || intro_start.equals(null) || intro_end.equals("") || intro_end.equals("0") || intro_end.equals(null)) {
                      Skip_Intro_btn.setVisibility(View.GONE);
                    } else {
                      if(Get_mil_From_Time(intro_start) < simpleExoPlayer.getContentPosition() && Get_mil_From_Time(intro_end) > simpleExoPlayer.getContentPosition()) {
                        Skip_Intro_btn.setVisibility(View.VISIBLE);
                      } else {
                        Skip_Intro_btn.setVisibility(View.GONE);
                      }
                    }
                }

            } else {
                Skip_Intro_btn.setVisibility(View.GONE);
            }


            ////
            if(!shouldInterceptRequestURL.equals("")) {
                shouldInterceptRequestPlayerController(shouldInterceptRequestURL);
                shouldInterceptRequestURL = "";
            }

            // Repeat every 1 seconds
            handler.postDelayed(runnable, 1000);
        }
    };

    long Get_mil_From_Time(String Time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:SS");
        Date parsed_date = null;
        try {
            parsed_date = format.parse(Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat sec = new SimpleDateFormat("SS");

        String Hour = hour.format(parsed_date);
        String Min = min.format(parsed_date);
        String Sec = sec.format(parsed_date);


        long m_hour = 0;
        long m_min = 0;
        long m_sec = 0;
        if (!Hour.equals("00")) {
            m_hour = Integer.parseInt(Hour)*3600000;
        }

        if (!Min.equals("00")) {
            m_min = Integer.parseInt(Min)*60000;
        }

        if (!Sec.equals("00")) {
            m_sec = Integer.parseInt(Sec)*1000;
        }

        Long F_mil = m_hour + m_min + m_sec;

        return F_mil;
    }


    void Prepare_Source(String source, String url) {
        String userAgent = Util.getUserAgent(this, "KAIOS");
        // Default parameters, except allowCrossProtocolRedirects is true
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                null /* listener */,
                httpDataSourceFactory
        );

        if (source.equals("M3u8")) {
            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
            initializePlayer(mediaSource);
        } else if (source.equals("Dash")) {
            DashMediaSource mediaSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
            initializePlayer(mediaSource);
        } else if (source.equals("Mp4")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
            initializePlayer(mediaSource);
        } else if (source.equals("Mkv")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
            initializePlayer(mediaSource);
        } else if (source.equals("mp4")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
            initializePlayer(mediaSource);
        } else if (source.equals("mkv")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
            initializePlayer(mediaSource);
        } else if (source.equals("Youtube")) {
            progressDialog.show();
            Yts.getlinks(this, url, new Yts.VolleyCallback() {
                @Override
                public void onSuccess(List<YTStreamList> result) {
                    ytMultipleQualityDialog(Player.this, result);
                }

                @Override
                public void onError(VolleyError error) {
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                }
            });
        } else if (source.equals("YoutubeLive")) {
            /*progressDialog.show();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                try {
                    YoutubeDL.getInstance().init(getApplication());
                } catch (YoutubeDLException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                try {
                    VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(url);
                    progressDialog.dismiss();
                    HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(streamInfo.getManifestUrl())));
                    initializePlayer(mediaSource);
                } catch (YoutubeDLException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }, 10);*/
            HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(AppConfig.url+"/api/fetch/youtube/get_ytlive.php?url="+url)));
            initializePlayer(mediaSource);

        } else if (source.equals("Dailymotion")) {
            String[] parts = url.split("/");
            String dMotionId = parts[4];
            dailymotion(dMotionId);
        } else if (source.equals("Facebook")) {
            progressDialog.show();
            Facebook.getStreamLink(this, url, new Facebook.FacebookCallback() {

                @Override
                public void onSuccess(JsonObject result) {
                    if (result != null) {
                        List<MultiqualityList> multiqualityList = new ArrayList<>();
                        try {
                            multiqualityList.add(new MultiqualityList("SD", result.get("Low_Quality").getAsString()));
                            multiqualityList.add(new MultiqualityList("HD", result.get("High_Quality").getAsString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        CharSequence[] name = new CharSequence[multiqualityList.size()];
                        for (int i = 0; i < multiqualityList.size(); i++) {
                            name[i] = multiqualityList.get(i).getQuality();
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("Quality!")
                                .setItems(name, (dialog, which) -> {
                                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(multiqualityList.get(which).getUrl())));
                                    initializePlayer(mediaSource);
                                })
                                .setPositiveButton("Close", (dialog, which) -> {
                                    isBackPressed = true;
                                    releasePlayer();
                                    handler.removeCallbacks(runnable);
                                    finish();
                                });
                        progressDialog.hide();
                        builder.show();
                    } else {
                        isBackPressed = true;
                        releasePlayer();
                        handler.removeCallbacks(runnable);
                        finish();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.hide();
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                }
            });
            /*Uri uri = Uri.parse(url);
            String v = uri.getQueryParameter("v");
            progressDialog.show();
            xGetter.find(v);*/
        } else if (source.equals("GoFile")) {
            GoFile.getStreamLink(this, url, new GoFile.goFileCallback() {
                @Override
                public void onSuccess(String result) {
                    String userAgent = Util.getUserAgent(context, "KAIOS");
                    // Default parameters, except allowCrossProtocolRedirects is true
                    DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                            userAgent,
                            null /* listener */,
                            DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                            true /* allowCrossProtocolRedirects */
                    );

                    DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                            context,
                            null /* listener */,
                            httpDataSourceFactory
                    );

                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(result)));
                    initializePlayer(mediaSource);
                }

                @Override
                public void onError(VolleyError error) {
                    Log.d("TAG", String.valueOf(error));
                }
            });
        } else if (source.equals("StreamTape")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(AppConfig.url + "/api/fetch/steamtape/stfetch.php?url=" + url)));
            initializePlayer(mediaSource);
            /*String[] parts = url.split("/");
            String id = parts[4];

            Intent intent = new Intent(context, EmbedPlayer.class);
            intent.putExtra("url", "https://streamtape.com/e/"+id+"/");
            startActivity(intent);
            finish();*/

        } else if (source.equals("GoogleDrive")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(AppConfig.url + "/api/fetch/gdfetch.php?url=" + url)));
            initializePlayer(mediaSource);
            /*String[] parts = url.split("/");
            String id = parts[5];
            String finalurl = "https://drive.google.com/uc?export=download&id="+id;
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(finalurl)));
            initializePlayer(mediaSource);*/
        } else if (source.equals("Onedrive")) {
            String encodedUrl = Utils.toBase64(url);
            String finalEncodedUrl = "https://api.onedrive.com/v1.0/shares/u!" + encodedUrl + "/root/content";
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(finalEncodedUrl)));
            initializePlayer(mediaSource);
        } else if (source.equals("OnedriveBusiness")) {
            String OnedriveBusinessURL = url.split("e=")[0]+"download=1";
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(OnedriveBusinessURL)));
            initializePlayer(mediaSource);
        } else if (source.equals("Yandex")) {
            progressDialog.show();
            Yandex.getStreamLink(context, url, new Yandex.yandexCallback() {

                @Override
                public void onSuccess(String result) {
                    progressDialog.hide();
                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(result)));
                    initializePlayer(mediaSource);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.hide();
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                }
            });

        } else if (source.equals("Vimeo")) {
            progressDialog.show();
            Vimeo.getStreamLink(context, url, new Vimeo.vimeoCallback() {

                @Override
                public void onSuccess(JsonArray result) {
                    List<MultiqualityList> multiqualityList = new ArrayList<>();
                    for (JsonElement rootElement : result) {
                        JsonObject rootObject = rootElement.getAsJsonObject();

                        String quality = rootObject.get("quality").getAsString();
                        String url = rootObject.get("url").getAsString();

                        multiqualityList.add(new MultiqualityList(quality, url));
                    }

                    CharSequence[] name = new CharSequence[multiqualityList.size()];

                    for (int i = 0; i < multiqualityList.size(); i++) {
                        name[i] = multiqualityList.get(i).getQuality();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Quality!")
                            .setItems(name, (dialog, which) -> {
                                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(multiqualityList.get(which).getUrl())));
                                initializePlayer(mediaSource);
                            })
                            .setPositiveButton("Close", (dialog, which) -> {
                                isBackPressed = true;
                                releasePlayer();
                                handler.removeCallbacks(runnable);
                                finish();
                            });
                    progressDialog.hide();
                    builder.show();
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.hide();
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                }
            });

        } else if (source.equals("Dropbox")) {
            /*progressDialog.show();
            try {
                YoutubeDL.getInstance().init(getApplication());
                try {
                    VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(url);
                    progressDialog.dismiss();
                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(streamInfo.getUrl())));
                    initializePlayer(mediaSource);
                } catch (YoutubeDLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (YoutubeDLException e) {
                e.printStackTrace();
            }*/

            progressDialog.show();

            android.webkit.WebView webView = new WebView(context);
            webView.setWebViewClient(new Player.MyWebViewClient());
            String ua = System.getProperty("http.agent");
            webView.getSettings().setUserAgentString(ua);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(url);

        } else if (source.equals("Cinematic")) {
            progressDialog.show();
            Cinematic.getStreamLink(this, url, new Cinematic.CinematicCallback() {

                @Override
                public void onSuccess(String result) {
                    progressDialog.dismiss();
                    HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(result)));
                    initializePlayer(mediaSource);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                }
            });
        } else if (source.equals("TubiTV")) {
            progressDialog.show();
            Tubitv.getStreamLink(this, url, new Tubitv.TubitvCallback() {

                @Override
                public void onSuccess(String result) {
                    progressDialog.dismiss();
                    HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(result)));
                    initializePlayer(mediaSource);
                }

                @Override
                public void onError(VolleyError error) {
                    progressDialog.dismiss();
                    finish();
                }
            });

        } else if (source.equals("TwitchLive")) {
            /*progressDialog.show();
            try {
                YoutubeDL.getInstance().init(getApplication());
                try {
                    VideoInfo streamInfo = YoutubeDL.getInstance().getInfo(url);
                    progressDialog.dismiss();
                    HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(streamInfo.getManifestUrl())));
                    initializePlayer(mediaSource);
                } catch (YoutubeDLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (YoutubeDLException e) {
                e.printStackTrace();
            }*/

            progressDialog.show();

            android.webkit.WebView webView = new WebView(context);
            webView.setWebViewClient(new Player.MyWebViewClient());
            String ua = System.getProperty("http.agent");
            webView.getSettings().setUserAgentString(ua);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(url);

        } else if (source.equals("Racaty")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(AppConfig.url + "/api/fetch/racatyfetch.php?url=" + url)));
            initializePlayer(mediaSource);
        } else if (source.equals("ZippyShare")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(AppConfig.url + "/api/fetch/zippyshare/zsfetch.php?url=" + url)));
            initializePlayer(mediaSource);
        } else if (source.equals("DoodStream")) {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(AppConfig.url + "/api/fetch/doodstream/stream.php?url=" + url)));
            initializePlayer(mediaSource);
        } else if(source.equals("StreamSB")) {
            progressDialog.show();

            android.webkit.WebView webView = new WebView(context);
            webView.setWebViewClient(new Player.MyWebViewClient());
            String ua = System.getProperty("http.agent");
            webView.getSettings().setUserAgentString(ua);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(url);

        } else if(source.equals("Voesx")) {
            progressDialog.show();

            android.webkit.WebView webView = new WebView(context);
            webView.setWebViewClient(new Player.MyWebViewClient());
            String ua = System.getProperty("http.agent");
            webView.getSettings().setUserAgentString(ua);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(url);

        } else if(source.equals("Videobin")) {
            progressDialog.show();

            android.webkit.WebView webView = new WebView(context);
            webView.setWebViewClient(new Player.MyWebViewClient());
            String ua = System.getProperty("http.agent");
            webView.getSettings().setUserAgentString(ua);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(url);

        } else if(source.equals("Streamzz")) {
            progressDialog.show();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpURLConnection connection = null;
            InputStream is = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String locationUrl = connection.getHeaderField("Location");

                    java.net.URL aURL;
                    try {
                        aURL = new java.net.URL(locationUrl);
                        String mURL = "https://get.streamz.tw/getlink-"+aURL.getPath().substring(2)+".dll";

                        progressDialog.dismiss();

                        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(mURL)));
                        initializePlayer(mediaSource);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                }
                is = connection.getInputStream();
            } catch (Exception e) {
                try {
                    connection.getErrorStream().close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();

            progressDialog.dismiss();

        } else if(source.equals("MP4Upload") || source.endsWith("GooglePhotos") || source.equals("MediaFire") || source.equals("OKru") ||
                source.endsWith("VK") || source.endsWith("Twitter") || source.equals("Solidfiles") || source.equals("Vidoza") ||
                source.equals("UpToStream") || source.equals("Fansubs") || source.equals("Sendvid") || source.equals("Fembed") || source.equals("Filerio") ||
                source.equals("Megaup") || source.equals("GoUnlimited") || source.endsWith("Cocoscope") || source.equals("Vidbm") || source.equals("Pstream") ||
                source.equals("vlare") || source.equals("StreamWiki") || source.equals("Vivosx") || source.endsWith("BitTube") || source.equals("VideoBin") ||
                source.equals("4shared") || source.equals("vudeo")) {

            progressDialog.show();
            xGetter.find(url);
        } else {
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)));
            initializePlayer(mediaSource);
        }
    }


    Map<String, String>  streamSBParams = new HashMap<String, String>();

    public class MyWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(android.webkit.WebView view, WebResourceRequest request) {
            if(source.equals("StreamSB")) {
                if(request.getUrl().toString().toLowerCase().contains("sources")) {
                    streamSBParams = request.getRequestHeaders();
                    shouldInterceptRequestURL = request.getUrl().toString();

                }
            } else if (source.equals("Voesx")) {
                if(request.getUrl().toString().toLowerCase().contains(".m3u8")) {
                    shouldInterceptRequestURL = request.getUrl().toString();
                }
            } else if(source.equals("Videobin")) {
                if(request.getUrl().toString().toLowerCase().contains(".mp4")) {
                    shouldInterceptRequestURL = request.getUrl().toString();
                }
            } else if(source.equals("Dropbox")) {
                if(request.getUrl().toString().toLowerCase().contains(".m3u8")) {
                    shouldInterceptRequestURL = request.getUrl().toString();
                }
            } else if(source.equals("TwitchLive")) {
                if(request.getUrl().toString().toLowerCase().contains(".m3u8")) {
                    shouldInterceptRequestURL = request.getUrl().toString();
                }
            }

            return super.shouldInterceptRequest(view, request);
        }
    }

    void shouldInterceptRequestPlayerController(String data) {
        String userAgent = Util.getUserAgent(context, "KAIOS");
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context,
                null /* listener */,
                httpDataSourceFactory
        );

        if(source.equals("StreamSB")) {
            shouldInterceptRequestStreamSBPlayer(data, dataSourceFactory);
        } else if (source.equals("Voesx")) {
            shouldInterceptRequestVoesxPlayer(data, dataSourceFactory);
        } else if(source.equals("Videobin")) {
            shouldInterceptRequestVideobinPlayer(data, dataSourceFactory);
        } else if(source.equals("Dropbox")) {
            shouldInterceptRequestDropboxPlayer(data, dataSourceFactory);
        } else if(source.equals("TwitchLive")) {
            shouldInterceptRequestTwitchLivePlayer(data, dataSourceFactory);
        }

    }

    void shouldInterceptRequestStreamSBPlayer(String data, DefaultDataSourceFactory dataSourceFactory) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, data, response -> {
            try {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                JsonObject stream_data = jsonObject.get("stream_data").getAsJsonObject();
                String file = stream_data.get("file").getAsString();

                progressDialog.dismiss();

                HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(file)));
                initializePlayer(mediaSource);
            } catch (Exception e) {
                progressDialog.hide();
                isBackPressed = true;
                releasePlayer();
                handler.removeCallbacks(runnable);
                finish();
            }
        }, error -> {
            progressDialog.hide();
            isBackPressed = true;
            releasePlayer();
            handler.removeCallbacks(runnable);
            finish();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return streamSBParams;
            }
        };
        queue.add(sr);
    }

    void shouldInterceptRequestVoesxPlayer(String data, DefaultDataSourceFactory dataSourceFactory) {
        progressDialog.dismiss();

        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(data)));
        initializePlayer(mediaSource);
    }

    void shouldInterceptRequestVideobinPlayer(String data, DefaultDataSourceFactory dataSourceFactory) {
        progressDialog.dismiss();

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(data)));
        initializePlayer(mediaSource);
    }

    void shouldInterceptRequestDropboxPlayer(String data, DefaultDataSourceFactory dataSourceFactory) {
        progressDialog.dismiss();

        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(data)));
        initializePlayer(mediaSource);
    }

    void shouldInterceptRequestTwitchLivePlayer(String data, DefaultDataSourceFactory dataSourceFactory) {
        progressDialog.dismiss();

        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(data)));
        initializePlayer(mediaSource);
    }

    void ytMultipleQualityDialog(Context context, List<YTStreamList> list) {
        progressDialog.dismiss();
        Collections.reverse(list);
        CharSequence[] name = new CharSequence[list.size()];
        CharSequence[] vid = new CharSequence[list.size()];
        CharSequence[] token = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            name[i] = list.get(i).getName();
            vid[i] = list.get(i).getVid();
            token[i] = list.get(i).getToken();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Quality!")
                .setItems(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Yts.getStreamLinks(context, (String) token[which], (String) vid[which], new Yts.VolleyCallback2(){

                            @Override
                            public void onSuccess(String result) {
                                String userAgent = Util.getUserAgent(context, "KAIOS");
                                // Default parameters, except allowCrossProtocolRedirects is true
                                DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                                        userAgent,
                                        null /* listener */,
                                        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                                        true /* allowCrossProtocolRedirects */
                                );

                                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                                        context,
                                        null /* listener */,
                                        httpDataSourceFactory
                                );

                                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(result)));
                                initializePlayer(mediaSource);
                            }

                            @Override
                            public void onError(VolleyError error) {
                            }
                        });
                    }
                })
                .setPositiveButton("Close", (dialog, which) -> {
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                });
        builder.show();
    }


    void dailymotion(String dMotionId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, "https://www.dailymotion.com/player/metadata/video/"+dMotionId, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            JsonObject qualities = jsonObject.get("qualities").getAsJsonObject();
            JsonArray auto = qualities.get("auto").getAsJsonArray();
            for (JsonElement r : auto) {
                JsonObject rootObject = r.getAsJsonObject();
                String dMotionUrl = rootObject.get("url").getAsString();

                String userAgent = Util.getUserAgent(this, "KAIOS");
                // Default parameters, except allowCrossProtocolRedirects is true
                DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                        userAgent,
                        null /* listener */,
                        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                        true /* allowCrossProtocolRedirects */
                );
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                        this,
                        null /* listener */,
                        httpDataSourceFactory
                );
                HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(dMotionUrl)));
                initializePlayer(mediaSource);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        });
        queue.add(sr);
    }

    private void done(XModel xModel){
        String url = null;
        if (xModel!=null) {
            url = xModel.getUrl();
        }
        if (url!=null) {

            String userAgent = Util.getUserAgent(this, "KAIOS");
            // Default parameters, except allowCrossProtocolRedirects is true
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                    userAgent,
                    null /* listener */,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true /* allowCrossProtocolRedirects */
            );
            if (xModel.getCookie()!=null) {
                httpDataSourceFactory.getDefaultRequestProperties().set("Cookie", xModel.getCookie());
            }
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    this,
                    null /* listener */,
                    httpDataSourceFactory
            );
            Log.d("test", xModel.getUrl());
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(xModel.getUrl())));
            initializePlayer(mediaSource);
        }else {
            Log.d("test", "inValid URL");
        }
    }
    private void multipleQualityDialog(ArrayList<XModel> model) {
        CharSequence[] name = new CharSequence[model.size()];

        for (int i = 0; i < model.size(); i++) {
            name[i] = model.get(i).getQuality();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Quality!")
                .setItems(name, (dialog, which) -> done(model.get(which)))
                .setPositiveButton("Close", (dialog, which) -> {
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                });
        builder.show();
    }

    void initializePlayer(MediaSource mediaSource) {
        ExoTrackSelection.Factory videoTrackSelectionFactory = new
                AdaptiveTrackSelection.Factory();


        trackSelector = new
                DefaultTrackSelector(Player.this, videoTrackSelectionFactory);
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this, renderersFactory)
                .setTrackSelector(trackSelector)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();

            youtube_overlay.player(simpleExoPlayer);

            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);


        // Custom Subtitle
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(
                System.getProperty("http.agent"),
                null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true
        );


        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_subtitle.php?content_id="+sourceID+"&ct="+ct, response -> {

            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                int count = 0;
                for (JsonElement rootElement : jsonArray) {
                    count++;

                    JsonObject rootObject = rootElement.getAsJsonObject();

                    MediaItem.Subtitle subtitle = null;
                    if(rootObject.get("mime_type").getAsString().equals("WebVTT")) {
                        subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_VTT, rootObject.get("language").getAsString());
                    } else if(rootObject.get("mime_type").getAsString().equals("TTML") || rootObject.get("mime_type").getAsString().equals("SMPTE-TT")) {
                        subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_TTML, rootObject.get("language").getAsString());
                    } else if(rootObject.get("mime_type").getAsString().equals("SubRip")) {
                        subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                    } else if(rootObject.get("mime_type").getAsString().equals("SubStationAlpha-SSA)") || rootObject.get("mime_type").getAsString().equals("SubStationAlpha-ASS)")) {
                        subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_SSA, rootObject.get("language").getAsString());
                    } else {
                        subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                    }

                    MediaSource textMediaSource = new SingleSampleMediaSource.Factory(factory).createMediaSource(subtitle, C.TIME_UNSET);

                    if(nMediaSource == null) {
                        nMediaSource = new MergingMediaSource(mediaSource, textMediaSource);
                    } else {
                        nMediaSource = new MergingMediaSource(nMediaSource, textMediaSource);
                    }

                    if(count == jsonArray.size()) {
                        if(nMediaSource != null) {
                            simpleExoPlayer.setMediaSource(nMediaSource);
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(true);

                            if(resumePosition != 0 && simpleExoPlayer != null) {
                                simpleExoPlayer.seekTo(resumePosition);
                            }
                        } else {
                            simpleExoPlayer.setMediaSource(mediaSource);
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(true);

                            if(resumePosition != 0 && simpleExoPlayer != null) {
                                simpleExoPlayer.seekTo(resumePosition);
                            }
                        }
                    }
                }
            } else {
                simpleExoPlayer.setMediaSource(mediaSource);
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(true);

                if(resumePosition != 0 && simpleExoPlayer != null) {
                    simpleExoPlayer.seekTo(resumePosition);
                }
            }
        }, error -> {
            simpleExoPlayer.setMediaSource(mediaSource);
            simpleExoPlayer.prepare();
            simpleExoPlayer.setPlayWhenReady(true);

            if(resumePosition != 0 && simpleExoPlayer != null) {
                simpleExoPlayer.seekTo(resumePosition);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);




         simpleExoPlayer.addListener(new com.google.android.exoplayer2.Player.EventListener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == com.google.android.exoplayer2.Player.STATE_READY) {
                    // Active playback.
                    contentLoaded = true;

                    db.resumeContentDao().updateDuration(simpleExoPlayer.getDuration(), resumeContentID);

                } else if (playbackState == com.google.android.exoplayer2.Player.STATE_ENDED) {
                    if (ContentType != null) {
                        if (ContentType.equals("WebSeries")) {
                            if (Next_Ep_Avilable.equals("Yes")) {
                                Play_Next_btn.setVisibility(View.VISIBLE);
                                CountDownTimer mTimer = new CountDownTimer(5000, 100) {
                                    public void onTick(long millisUntilFinished) {
                                        Play_Next_btn.setText("Playing Next In " + Long.toString(millisUntilFinished / 1000) + "Sec");
                                    }

                                    @Override
                                    public void onFinish() {
                                        Play_Next_btn.setText("Playing Now");
                                        Intent intent = new Intent();
                                        intent.putExtra("Current_List_Position", Current_List_Position);
                                        setResult(RESULT_OK, intent);

                                        isBackPressed = true;
                                        releasePlayer();
                                        handler.removeCallbacks(runnable);
                                        finish();
                                    }
                                };
                                mTimer.start();
                            }

                        }
                    }

                } else if (playWhenReady) {
                    // Not playing because playback ended, the Player is buffering, stopped or
                    // failed. Check playbackState and Player.getPlaybackError for details.
                } else {
                    // Paused by app.
                }

            }
        });

    }

    private void pausePlayer(){
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }
    private void startPlayer(){
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.getPlaybackState();
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer.clearVideoSurface();
            simpleExoPlayer = null;
        }
    }

    Boolean isBackPressed = false;
    @Override
    public void onBackPressed() {
        isBackPressed = true;
        releasePlayer();
        handler.removeCallbacks(runnable);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isBackPressed) {
            pausePlayer();
        }

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            userId = jsonObject.get("ID").getAsInt();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Player.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Player.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        wakeLock.release();
    }
}
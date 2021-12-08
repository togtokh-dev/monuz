package com.togtokh.monuz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.togtokh.monuz.utils.HelperUtils;
import com.togtokh.monuz.utils.TinyDB;
import com.togtokh.monuz.utils.Utils;
import com.facebook.FacebookSdk;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.onesignal.OneSignal;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class Splash extends AppCompatActivity {
    Context context = this;

    public static String notificationData = "";
    String userData;
    String apiKey;
    Integer loginMandatory;
    Integer maintenance;

    String latestAPKVersionName;
    String latestAPKVersionCode;
    String apkFileUrl;
    String whatsNewOnLatestApk;
    int updateSkipable;
    int updateType;

    private boolean vpnStatus = false;
    private HelperUtils helperUtils;

    @Override
    protected void onStart() {
        super.onStart();
        vpnStatus = new HelperUtils(Splash.this).isVpnConnectionAvailable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TinyDB tinyDB = new TinyDB(context);
        if(!tinyDB.getString("appLanguage").equals("") || tinyDB.getString("appLanguage") != null) {
            String languageToLoad  = tinyDB.getString("appLanguage");
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        if(AppConfig.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Splash_TitleBar_BG));

        setContentView(R.layout.activity_splash);

        helperUtils = new HelperUtils(Splash.this);
        vpnStatus = new HelperUtils(Splash.this).isVpnConnectionAvailable();

        StartAppAd.disableSplash();

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(AppConfig.ONESIGNAL_APP_ID);
        OneSignal.setNotificationOpenedHandler(
                result -> Splash.notificationData = result.getNotification().getAdditionalData().toString());


        ApplicationInfo restrictedApp = helperUtils.getRestrictApp();
        if (restrictedApp != null){
            Log.e("test", restrictedApp.loadLabel(this.getPackageManager()).toString());
            HelperUtils.showWarningDialog(this, "Restricted App!", "Please Uninstall "+restrictedApp.loadLabel(this.getPackageManager()).toString()+" to use this App On this Device!", R.raw.sequre);
        } else {
            if (HelperUtils.cr(this, AppConfig.allowRoot)) {
                HelperUtils.showWarningDialog(this, "ROOT!", "You are Not Allowed To Use this App on Rooted Device!", R.raw.sequre);

            } else {
                if (AppConfig.allowVPN) {
                    loadData();
                } else {
                    if (vpnStatus) {
                        helperUtils.showWarningDialog(Splash.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
                    } else {
                        loadData();
                    }
                }
            }
        }

        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );
        DialogPropertiesSignal properties = builder.getDialogProperties();
        // Optional
        properties.setConnectionCallback(hasActiveConnection -> {
            // ...
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


        checkStoragePermission();
    }

    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Log.d("test", "Permission is granted");
                return true;

            } else {
                Log.d("test", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.d("test", "Permission is granted");
            return true;
        }*/
        return true;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userData = sharedPreferences.getString("UserData", null);
        loadConfig();
    }

    private void loadConfig() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_config.php", response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            apiKey = jsonObject.get("api_key").getAsString();
            loginMandatory = jsonObject.get("login_mandatory").getAsInt();
            maintenance = jsonObject.get("maintenance").getAsInt();
            saveConfig(response);
            saveNotification();

            latestAPKVersionName = jsonObject.get("Latest_APK_Version_Name").getAsString();
            latestAPKVersionCode = jsonObject.get("Latest_APK_Version_Code").getAsString();
            apkFileUrl = jsonObject.get("APK_File_URL").getAsString();
            whatsNewOnLatestApk = jsonObject.get("Whats_new_on_latest_APK").getAsString();
            updateSkipable = jsonObject.get("Update_Skipable").getAsInt();
            updateType = jsonObject.get("Update_Type").getAsInt();

            String whatsNew = whatsNewOnLatestApk.replace(",", "\n").trim();

            int version = BuildConfig.VERSION_CODE;
            int latestVersionCode;
            try{
                latestVersionCode = Integer.parseInt(latestAPKVersionCode);
            } catch (NumberFormatException e) {
                latestVersionCode = 1;
            }

            if(latestVersionCode > version) { //latestVersionCode > version
                if(updateSkipable == 0) { //NO
                    MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                            .setTitle("Update "+latestAPKVersionName)
                            .setMessage(whatsNew)
                            .setCancelable(false)
                            .setAnimation(R.raw.rocket_telescope)
                            .setNegativeButton("Exit", R.drawable.ic_baseline_exit, (dialogInterface, which) -> finish())
                            .setPositiveButton("Update!", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                if(updateType == 0) {
                                    Intent intent = new Intent(Splash.this, InAppUpdate.class);
                                    intent.putExtra("Update_Title", "Update " + latestAPKVersionName);
                                    intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
                                    intent.putExtra("APK_File_URL", apkFileUrl);
                                    startActivity(intent);
                                } else if(updateType == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                    startActivity(intent);
                                }
                            })
                            .build();

                    // Show dialog
                    mDialog.show();
                } else if(updateSkipable == 1) { //YES
                    MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                            .setTitle("Update "+latestAPKVersionName)
                            .setMessage(whatsNew)
                            .setCancelable(false)
                            .setAnimation(R.raw.rocket_telescope)
                            .setNegativeButton("Cancel", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                                openApp();
                            })
                            .setPositiveButton("Update!", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                if(updateType == 0) {
                                    Intent intent = new Intent(Splash.this, InAppUpdate.class);
                                    intent.putExtra("Update_Title", "Update "+latestAPKVersionName);
                                    intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
                                    intent.putExtra("APK_File_URL", apkFileUrl);
                                    startActivity(intent);
                                } else if(updateType == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                    startActivity(intent);
                                }
                            })
                            .build();

                    // Show dialog
                    mDialog.show();
                }
            } else {
                openApp();
            }
        }, error -> {
            // Do nothing because
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    void openApp() {
        if(checkStoragePermission()) {
            if (maintenance == 0) {
                if (apiKey.equals(AppConfig.apiKey)) {
                    if (userData == null) {

                        if (loginMandatory == 0) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                saveNotification();
                                Intent intent = new Intent(Splash.this, Home.class);
                                intent.putExtra("Notification_Data", notificationData);
                                startActivity(intent);
                                notificationData = "";
                                finish();
                            }, 500);
                        } else if (loginMandatory == 1) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                saveNotification();
                                Intent intent = new Intent(Splash.this, LoginSignup.class);
                                startActivity(intent);
                                finish();
                            }, 500);
                        }


                    } else {
                        Handler handler = new Handler();
                        handler.postDelayed(this::verifyUser, 500);
                    }
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            } else {
                Intent intent = new Intent(Splash.this, Maintenance.class);
                startActivity(intent);
                finish();
            }
        } else {
            openApp();
        }
    }

    void verifyUser() {
        JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
        String email = jsonObject.get("Email").getAsString();
        String password = jsonObject.get("Password").getAsString();

        String originalInput = "login:"+email+":" + password;
        String encoded = Utils.toBase64(originalInput);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/authentication.php", response -> {
            if(!response.equals("")) {
                JsonObject jsonObject1 = new Gson().fromJson(response, JsonObject.class);
                String status = jsonObject1.get("Status").toString();
                status = status.substring(1, status.length() - 1);

                if (status.equals("Successful")) {
                    saveData(response);

                    JsonObject subObj = new Gson().fromJson(response, JsonObject.class);
                    int subscriptionType = subObj.get("subscription_type").getAsInt();
                    saveUserSubscriptionDetails(subscriptionType);

                    setOneSignalExternalID(String.valueOf(subObj.get("ID").getAsInt()));

                    saveNotification();
                    Intent intent = new Intent(Splash.this, Home.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else if (status.equals("Invalid Credential")) {
                    deleteData();
                    if (loginMandatory == 0) {
                        saveNotification();
                        Intent intent = new Intent(Splash.this, Home.class);
                        intent.putExtra("Notification_Data", notificationData);
                        startActivity(intent);
                        notificationData = "";
                        finish();
                    } else {
                        Intent intent = new Intent(Splash.this, LoginSignup.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                deleteData();
                if (loginMandatory == 0) {
                    saveNotification();
                    Intent intent = new Intent(Splash.this, Home.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else {
                    Intent intent = new Intent(Splash.this, LoginSignup.class);
                    startActivity(intent);
                    finish();
                }
            }

        }, error -> {
            // Do nothing because
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                params.put("X-Requested-With", encoded);
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void setOneSignalExternalID(String externalID) {
        OneSignal.setExternalUserId(externalID, new OneSignal.OSExternalUserIdUpdateCompletionHandler() {
            @Override
            public void onSuccess(JSONObject results) {
                //Log.d("test", results.toString());
            }
            @Override
            public void onFailure(OneSignal.ExternalIdError error) {
                //Log.d("test", error.toString());
            }
        });
    }

    private void saveUserSubscriptionDetails(int subscriptionType) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/dmVyaWZ5.php", response -> {
            if(!response.equals(Utils.fromBase64("ZmFsc2U=")) && !response.equals(Utils.fromBase64("SW5hY3RpdmUgcHVyY2hhc2UgY29kZQ==")) && !response.equals(Utils.fromBase64("SW52YWxpZCBwdXJjaGFzZSBjb2Rl"))) {
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("subscription_type", String.valueOf(subscriptionType));
                editor.apply();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("subscription_type", "0");
                editor.apply();
            }
        }, error -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("subscription_type", "0");
            editor.apply();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void saveData(String userData) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", userData);
        editor.apply();
    }

    private void deleteData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("UserData");
        editor.apply();
    }

    private void saveNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("Notificatin_Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Config", notificationData);
        editor.apply();
    }

    private void saveConfig(String config) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Config", config);
        editor.apply();


        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        AppConfig.adMobNative = jsonObject.get("adMob_Native").getAsString();
        AppConfig.adMobBanner = jsonObject.get("adMob_Banner").getAsString();
        AppConfig.adMobInterstitial = jsonObject.get("adMob_Interstitial").getAsString();
        String StartApp_App_ID = jsonObject.get("StartApp_App_ID").getAsString();
        String Admob_APP_ID = jsonObject.get("Admob_APP_ID").getAsString();
        String facebook_app_id = jsonObject.get("facebook_app_id").getAsString();

        AppConfig.all_live_tv_type= jsonObject.get("all_live_tv_type").getAsInt();
        AppConfig.all_movies_type= jsonObject.get("all_movies_type").getAsInt();
        AppConfig.all_series_type= jsonObject.get("all_series_type").getAsInt();

        AppConfig.facebook_banner_ads_placement_id = jsonObject.get("facebook_banner_ads_placement_id").getAsString();
        AppConfig.facebook_interstitial_ads_placement_id = jsonObject.get("facebook_interstitial_ads_placement_id").getAsString();

        AppConfig.AdColony_APP_ID= jsonObject.get("AdColony_app_id").getAsString();
        AppConfig.AdColony_BANNER_ZONE_ID= jsonObject.get("AdColony_banner_zone_id").getAsString();
        AppConfig.AdColony_INTERSTITIAL_ZONE_ID= jsonObject.get("AdColony_interstitial_zone_id").getAsString();

        AppConfig.Unity_Game_ID= jsonObject.get("unity_game_id").getAsString();
        AppConfig.Unity_Banner_ID= jsonObject.get("unity_banner_id").getAsString();

        AppConfig.Custom_Banner_url= jsonObject.get("custom_banner_url").getAsString();
        AppConfig.Custom_Banner_click_url_type = jsonObject.get("custom_banner_click_url_type").getAsInt();
        AppConfig.Custom_Banner_click_url= jsonObject.get("custom_banner_click_url").getAsString();
        AppConfig.Custom_Interstitial_url= jsonObject.get("custom_interstitial_url").getAsString();
        AppConfig.Custom_Interstitial_click_url_type= jsonObject.get("custom_interstitial_click_url_type").getAsInt();
        AppConfig.Custom_Interstitial_click_url= jsonObject.get("custom_interstitial_click_url").getAsString();

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", Admob_APP_ID);
            ai.metaData.putString("com.startapp.sdk.APPLICATION_ID", StartApp_App_ID);
            ai.metaData.putString("com.facebook.sdk.ApplicationId", facebook_app_id);

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("TAG", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        FacebookSdk.setApplicationId(facebook_app_id);
        StartAppSDK.init(this, StartApp_App_ID, false);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Splash.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Splash.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}
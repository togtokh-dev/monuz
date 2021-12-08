package com.togtokh.monuz;

public class AppConfig {
    static {
        System.loadLibrary("app_config");
    }
    public static native String getApiServerUrl();
    public static native String getApiKey();
    public static native String getOnesignalAppID();
    public static native boolean allowVPNStatus();
    public static native boolean flagSecureStatus();
    public static native boolean allowRootStatus();
    public static native String getYoutubeApiKey();
    public static native boolean unityAdTestModeStatus();

    //<!--DO NOT EDIT THESE DETAILS THESE WILL BE ASSIGNED FROM app_config.cpp-->//
    public static String url = "https://monuz.togtokh.dev";
    public static String apiKey = "RaA6xhIFBP1sy2K7";
    static final String ONESIGNAL_APP_ID = "fd38320b-7e41-4cf6-a6aa-51350522b0ea";
    static boolean allowVPN = false;
    public static final boolean FLAG_SECURE = false;
    static boolean allowRoot = true;
    public static final String YOUTUBE_API_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    public static Boolean unity_ad_testMode =  false;
    //<!--END-->//

    //<!--DO NOT EDIT THESE DETAILS THESE WILL BE ASSIGNED FROM DASHBOARD-->//
    static String adMobNative = "";
    static String adMobBanner = "";
    static String adMobInterstitial = "";
    static String facebook_banner_ads_placement_id = "";
    static String facebook_interstitial_ads_placement_id = "";
    public static String AdColony_APP_ID = "";
    public static String AdColony_BANNER_ZONE_ID = "";
    public static String AdColony_INTERSTITIAL_ZONE_ID = "";
    public static String Unity_Game_ID = "";
    public static String Unity_Banner_ID = "";

    public static String Custom_Banner_url = "";
    public static int Custom_Banner_click_url_type;
    public static String Custom_Banner_click_url= "";
    public static String Custom_Interstitial_url = "";
    public static int Custom_Interstitial_click_url_type;
    public static String Custom_Interstitial_click_url= "";


    public static int all_live_tv_type;
    public static int all_movies_type;
    public static int all_series_type;
    //<!------------------------------------------------------------------->//

}

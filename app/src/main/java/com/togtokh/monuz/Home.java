package com.togtokh.monuz;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.togtokh.monuz.adepter.AllMovieListAdepter;
import com.togtokh.monuz.adepter.AllWebSeriesListAdepter;
import com.togtokh.monuz.adepter.ContinuePlayingListAdepter;
import com.togtokh.monuz.adepter.GenreListAdepter;
import com.togtokh.monuz.adepter.ImageSliderAdepter;
import com.togtokh.monuz.adepter.LiveTvChannelListAdepter;
import com.togtokh.monuz.adepter.MovieListAdepter;
import com.togtokh.monuz.adepter.SearchListAdepter;
import com.togtokh.monuz.adepter.WebSeriesListAdepter;
import com.togtokh.monuz.adepter.moviesOnlyForYouListAdepter;
import com.togtokh.monuz.adepter.webSeriesOnlyForYouListAdepter;
import com.togtokh.monuz.db.resume_content.ResumeContent;
import com.togtokh.monuz.db.resume_content.ResumeContentDatabase;
import com.togtokh.monuz.list.ContinuePlayingList;
import com.togtokh.monuz.list.GenreList;
import com.togtokh.monuz.list.ImageSliderItem;
import com.togtokh.monuz.list.LiveTvChannelList;
import com.togtokh.monuz.list.MovieList;
import com.togtokh.monuz.list.SearchList;
import com.togtokh.monuz.list.WebSeriesList;
import com.togtokh.monuz.utils.HelperUtils;
import com.togtokh.monuz.utils.TinyDB;
import com.togtokh.monuz.utils.Utils;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.Mrec;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import es.dmoral.toasty.Toasty;

public class Home extends AppCompatActivity {
    String userData;
    String config;
    Context context = this;
    private ViewPager2 viewPager2;
    private final Handler sliderHandler = new Handler();

    String imageSliderType;
    List<ImageSliderItem> imageSliderItems;

    int movieImageSliderMaxVisible;
    int webseriesImageSliderMaxVisible;

    int adType;

    LinearLayout admobNativeadTemplateLayout;

    RelativeLayout bannerViewLayout;
    RelativeLayout adViewLayout;

    View forgotPasswordLayout;

    int shuffleContents;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    int showMessage;
    String messageTitle;
    String message;

    boolean removeAds = false;
    boolean playPremium = false;
    boolean downloadPremium = false;

    LinearLayout liveTvLayout;
    int liveTvVisiableInHome;

    int onlyPremium = 1;

    ResumeContentDatabase db;
    public static LinearLayout resume_Layout;
    public static List<ResumeContent> resumeContents;

    View rootView;

    LinearLayout genreLayout;
    RecyclerView genre_list_Recycler_View;

    int genre_visible_in_home;

    String tempLanguage = "";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppConfig.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Home_TitleBar_BG));

        setContentView(R.layout.activity_home);

        rootView = findViewById(R.id.Home);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Home.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Home.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        //Set Views
        genreLayout = findViewById(R.id.genreLayout);
        genre_list_Recycler_View = findViewById(R.id.genre_list_Recycler_View);

        admobNativeadTemplateLayout = findViewById(R.id.admob_nativead_template_layout);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Intent intent = getIntent();
        String notificationData = intent.getExtras().getString("Notification_Data");
        JsonObject notificationDataJsonObj = new Gson().fromJson(notificationData, JsonObject.class);
        if(notificationDataJsonObj != null) {
            String type = notificationDataJsonObj.get("Type").getAsString();
            switch (type) {
                case "External_Browser": {
                    String url = notificationDataJsonObj.get("URL").getAsString();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    break;
                }
                case "Web View": {
                    String url = notificationDataJsonObj.get("URL").getAsString();
                    Intent intent1 = new Intent(Home.this, WebView.class);
                    intent1.putExtra("URL", url);
                    startActivity(intent1);
                    break;
                }
                case "Movie": {
                    int movieId = notificationDataJsonObj.get("Movie_id").getAsInt();
                    Intent movieIntent = new Intent(Home.this, MovieDetails.class);
                    movieIntent.putExtra("ID", movieId);
                    startActivity(movieIntent);
                    break;
                }
                case "Web Series": {
                    int webSeriesId = notificationDataJsonObj.get("Web_Series_id").getAsInt();
                    Intent webseriesIntent = new Intent(Home.this, WebSeriesDetails.class);
                    webseriesIntent.putExtra("ID", webSeriesId);
                    startActivity(webseriesIntent);
                    break;
                }
                default:
            }
        }

        //-----Search LAYOUT--------//
        EditText searchContentEditText = findViewById(R.id.Search_content_editText);
        View bigSearchLottieAnimation = findViewById(R.id.big_search_Lottie_animation);
        RecyclerView searchLayoutRecyclerView = findViewById(R.id.Search_Layout_RecyclerView);

        //------HOME LAYOUTS--------//
        View homeLayout = findViewById(R.id.Home_Layout);
        View searchLayout = findViewById(R.id.Search_Layout);
        View moviesLayout = findViewById(R.id.Movies_Layout);
        View seriesLayout = findViewById(R.id.Series_Layout);
        View accountLayout = findViewById(R.id.Account_Layout);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            final int previousItem = bottomNavigationView.getSelectedItemId();
            final int nextItem = item.getItemId();
            if (previousItem != nextItem) {
                switch (nextItem) {
                    case R.id.Home:
                        homeLayout.setVisibility(View.VISIBLE);
                        searchLayout.setVisibility(View.GONE);
                        moviesLayout.setVisibility(View.GONE);
                        seriesLayout.setVisibility(View.GONE);
                        accountLayout.setVisibility(View.GONE);
                        loadhomecontentlist();
                        break;
                    case R.id.Search:
                        homeLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.VISIBLE);
                        moviesLayout.setVisibility(View.GONE);
                        seriesLayout.setVisibility(View.GONE);
                        accountLayout.setVisibility(View.GONE);
                        searchContentEditText.setText("");
                        break;
                    case R.id.Movies:
                        homeLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.GONE);
                        moviesLayout.setVisibility(View.VISIBLE);
                        seriesLayout.setVisibility(View.GONE);
                        accountLayout.setVisibility(View.GONE);
                        movieList();
                        break;
                    case R.id.Series:
                        homeLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.GONE);
                        moviesLayout.setVisibility(View.GONE);
                        seriesLayout.setVisibility(View.VISIBLE);
                        accountLayout.setVisibility(View.GONE);
                        webSeriesList();
                        break;
                    case R.id.Account:
                        homeLayout.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.GONE);
                        moviesLayout.setVisibility(View.GONE);
                        seriesLayout.setVisibility(View.GONE);
                        accountLayout.setVisibility(View.VISIBLE);
                        break;
                    default:


                }
            }
            return true;

        });

        String openType = intent.getExtras().getString("OpenType");
        if(openType != null) {
            if(openType.equals("Movies")) {
                bottomNavigationView.setSelectedItemId(R.id.Movies);
                homeLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.GONE);
                moviesLayout.setVisibility(View.VISIBLE);
                seriesLayout.setVisibility(View.GONE);
                accountLayout.setVisibility(View.GONE);
                movieList();
            } else if(openType.equals("WebSeries")) {
                bottomNavigationView.setSelectedItemId(R.id.Series);
                homeLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.GONE);
                moviesLayout.setVisibility(View.GONE);
                seriesLayout.setVisibility(View.VISIBLE);
                accountLayout.setVisibility(View.GONE);
                webSeriesList();
            }
        }


        loadData();
        loadConfig();
        loadUserSubscriptionDetails();
        loadhomecontentlist();

        viewPager2 = findViewById(R.id.ViewPagerImageSlider);

        imageSliderItems = new ArrayList<>();
        switch (imageSliderType) {
            case "0":
                topMoviesImageSlider();
                break;
            case "1":
                topWebSeriesImageSlider();
                break;
            case "2":
                customImageSlider();
                break;
            case "3":
                viewPager2.setVisibility(View.GONE);
                break;
            default:
                Log.d("Dooo", "Visiable");
        }



        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1-Math.abs(position);
            page.setScaleY(0.85f + r * 0.20f);
            page.setScaleX(0.90f + r * 0.20f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        resume_Layout= findViewById(R.id.resume_Layout);
        db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
        resumeContents = db.resumeContentDao().getResumeContents();
        loadResumeContents(resumeContents);

        if(resumeContents.isEmpty()) {
            resume_Layout.setVisibility(View.GONE);
        } else {
            resume_Layout.setVisibility(View.VISIBLE);
        }

        loadGenre();

        ///Home Swipe Refresh
        SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
        homeSwipeRefreshLayout.setOnRefreshListener(() -> {
            loadGenre();
            loadhomecontentlist();

            resumeContents = db.resumeContentDao().getResumeContents();
            loadResumeContents(resumeContents);

            if(resumeContents.isEmpty()) {
                resume_Layout.setVisibility(View.GONE);
            } else {
                resume_Layout.setVisibility(View.VISIBLE);
            }
        });

        ///Movie swipe Refresh
        SwipeRefreshLayout movieSwipeRefreshLayout = findViewById(R.id.Movie_Swipe_Refresh_Layout);
        movieSwipeRefreshLayout.setOnRefreshListener(this::movieList);

        ///Web Series swipe Refresh
        SwipeRefreshLayout webSeriesSwipeRefreshLayout = findViewById(R.id.Web_Series_Swipe_Refresh_Layout);
        webSeriesSwipeRefreshLayout.setOnRefreshListener(this::webSeriesList);

        ImageView moreMovies = findViewById(R.id.More_Movies);
        moreMovies.setOnClickListener(view -> {
            bottomNavigationView.setSelectedItemId(R.id.Movies);
            homeLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            moviesLayout.setVisibility(View.VISIBLE);
            seriesLayout.setVisibility(View.GONE);
            accountLayout.setVisibility(View.GONE);
            movieList();
        });

        ImageView moreWebSeries = findViewById(R.id.More_WebSeries);
        moreWebSeries.setOnClickListener(view -> {
            bottomNavigationView.setSelectedItemId(R.id.Series);
            homeLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            moviesLayout.setVisibility(View.GONE);
            seriesLayout.setVisibility(View.VISIBLE);
            accountLayout.setVisibility(View.GONE);
            webSeriesList();
        });

        ImageView moreLiveTV = findViewById(R.id.More_Live_TV);
        moreLiveTV.setOnClickListener(view -> {
            Intent intent12 = new Intent(Home.this, LiveTv.class);
            startActivity(intent12);
        });

        ImageView moreRecentMovies = findViewById(R.id.More_Recent_Movies);
        moreRecentMovies.setOnClickListener(view -> {
            bottomNavigationView.setSelectedItemId(R.id.Movies);
            homeLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            moviesLayout.setVisibility(View.VISIBLE);
            seriesLayout.setVisibility(View.GONE);
            accountLayout.setVisibility(View.GONE);
            movieList();
        });

        ImageView moreRecentSeries = findViewById(R.id.More_Recent_Series);
        moreRecentSeries.setOnClickListener(view -> {
            bottomNavigationView.setSelectedItemId(R.id.Series);
            homeLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            moviesLayout.setVisibility(View.GONE);
            seriesLayout.setVisibility(View.VISIBLE);
            accountLayout.setVisibility(View.GONE);
            webSeriesList();
        });

        ConstraintLayout favouriteListBtn = findViewById(R.id.favourite_list_btn);
        favouriteListBtn.setOnClickListener(view -> {
            Intent favoriteContentsActivity = new Intent(Home.this, Favorites.class);
            startActivity(favoriteContentsActivity);

            /*if(userData != null) {

            } else {
                Snackbar snackbar = Snackbar.make(rootView, "Login to See Favourite Contents!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Login", v -> {
                    Intent lsIntent = new Intent(Home.this, LoginSignup.class);
                    startActivity(lsIntent);
                });
                snackbar.show();
            }*/
        });

        ConstraintLayout genre_list_btn = findViewById(R.id.genre_list_btn);
        CardView cardView80 = findViewById(R.id.cardView80);
        cardView80.setOnClickListener(view->{
            Intent favoriteContentsActivity = new Intent(Home.this, AllGenre.class);
            startActivity(favoriteContentsActivity);
        });

        CardView account = findViewById(R.id.account);
        account.setOnClickListener(v->{
            if(userData != null) {
                final Dialog dialog = new Dialog(Home.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.account_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);

                ImageView close = (ImageView) dialog.findViewById(R.id.Close);
                close.setOnClickListener(view -> dialog.dismiss());

                JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                int userID = jsonObject.get("ID").getAsInt();
                String name = jsonObject.get("Name").getAsString();
                String email = jsonObject.get("Email").getAsString();

                TextView profileName = dialog.findViewById(R.id.profileName);
                profileName.setText(name);

                EditText nameEditText = dialog.findViewById(R.id.nameEditText);
                nameEditText.setText(name);

                EditText emailEditText = dialog.findViewById(R.id.emailEditText);
                emailEditText.setText(email);

                EditText passwordEditText = dialog.findViewById(R.id.passwordEditText);

                Button saveBtn = dialog.findViewById(R.id.saveBtn);
                ProgressBar saveLoadingProgress = dialog.findViewById(R.id.saveLoadingProgress);
                saveBtn.setOnClickListener(view -> {
                    if (view.getVisibility() == View.VISIBLE) {
                        view.setVisibility(View.GONE);
                        saveLoadingProgress.setVisibility(View.VISIBLE);
                    }

                    RequestQueue queue = Volley.newRequestQueue(this);
                    StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/update_account.php", response -> {
                        if(response.equals("Account Updated Successfully")) {
                            Toasty.success(context, "Account Updated Successfully!", Toast.LENGTH_SHORT, true).show();
                            Intent restartIntent = new Intent(Home.this, Splash.class);
                            startActivity(restartIntent);
                            finish();
                        } else {
                            Toasty.warning(context, "Invalid Password!.", Toast.LENGTH_SHORT, true).show();
                        }

                        if (view.getVisibility() == View.GONE) {
                            saveLoadingProgress.setVisibility(View.GONE);
                            view.setVisibility(View.VISIBLE);
                        }
                    }, error -> {
                        if (view.getVisibility() == View.GONE) {
                            saveLoadingProgress.setVisibility(View.GONE);
                            view.setVisibility(View.VISIBLE);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("UserID", String.valueOf(userID));
                            params.put("UserName", nameEditText.getText().toString());
                            params.put("Email", emailEditText.getText().toString());
                            params.put("Password", Utils.getMd5(passwordEditText.getText().toString()));
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("x-api-key", AppConfig.apiKey);
                            return params;
                        }
                    };
                    queue.add(sr);

                });


                dialog.show();
            } else {
                Snackbar snackbar = Snackbar.make(rootView, "You are not Logged in!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Login", view -> {
                    Intent lsIntent = new Intent(Home.this, LoginSignup.class);
                    startActivity(lsIntent);
                });
                snackbar.show();
            }
        });

        ConstraintLayout subscriptionBtn = findViewById(R.id.Subscription_Btn);
        subscriptionBtn.setOnClickListener(view -> {
            if(userData != null) {
                Intent favoriteContentsActivity = new Intent(Home.this, Subscription.class);
                startActivity(favoriteContentsActivity);
            } else {
                Snackbar snackbar = Snackbar.make(rootView, "Login to See Subscription Details!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Login", v -> {
                    Intent lsIntent = new Intent(Home.this, LoginSignup.class);
                    startActivity(lsIntent);
                });
                snackbar.show();
            }
        });

        ConstraintLayout downloadBtn = findViewById(R.id.Download_Btn);
        downloadBtn.setOnClickListener(view -> {
            Intent downloads = new Intent(Home.this, Downloads.class);
            startActivity(downloads);
        });

        ConstraintLayout termsCondition = findViewById(R.id.termsCondition);
        termsCondition.setOnClickListener(view -> {
            Intent termsAndConditionsIntent = new Intent(Home.this, TermsAndConditions.class);
            startActivity(termsAndConditionsIntent);
        });

        ConstraintLayout privecyPolicy = findViewById(R.id.privecyPolicy);
        privecyPolicy.setOnClickListener(view -> {
            Intent privecyPolicyIntent = new Intent(Home.this, PrivecyPolicy.class);
            startActivity(privecyPolicyIntent);
        });

        CardView language = findViewById(R.id.language);
        language.setOnClickListener(view->{
            final Dialog dialog = new Dialog(Home.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.language_change_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

            ImageView Close = (ImageView) dialog.findViewById(R.id.Close);
            Close.setOnClickListener(v -> dialog.dismiss());

            TextView languageDialogHeader = dialog.findViewById(R.id.languageDialogHeader);
            TextView languageDialogSubHeader = dialog.findViewById(R.id.languageDialogSubHeader);

            CardView cardView_english = dialog.findViewById(R.id.cardView_english);
            CardView cardView_hindi = dialog.findViewById(R.id.cardView_hindi);
            CardView cardView_bengali = dialog.findViewById(R.id.cardView_bengali);
            CardView cardView_spanish = dialog.findViewById(R.id.cardView_spanish);
            CardView cardView_russian = dialog.findViewById(R.id.cardView_russian);
            CardView cardView_turkish = dialog.findViewById(R.id.cardView_turkish);
            CardView cardView_chaines = dialog.findViewById(R.id.cardView_chaines);

            LinearLayout linearlayout_english = dialog.findViewById(R.id.linearlayout_english);
            LinearLayout linearlayout_hindi = dialog.findViewById(R.id.linearlayout_hindi);
            LinearLayout linearlayout_bengali = dialog.findViewById(R.id.linearlayout_bengali);
            LinearLayout linearlayout_spanish = dialog.findViewById(R.id.linearlayout_spanish);
            LinearLayout linearlayout_russian = dialog.findViewById(R.id.linearlayout_russian);
            LinearLayout linearlayout_turkish = dialog.findViewById(R.id.linearlayout_turkish);
            LinearLayout linearlayout_chaines = dialog.findViewById(R.id.linearlayout_chaines);

            TextView textview_english = dialog.findViewById(R.id.textview_english);
            TextView textview_hindi = dialog.findViewById(R.id.textview_hindi);
            TextView textview_bengali = dialog.findViewById(R.id.textview_bengali);
            TextView textview_spanish = dialog.findViewById(R.id.textview_spanish);
            TextView textview_russian = dialog.findViewById(R.id.textview_russian);
            TextView textview_turkish = dialog.findViewById(R.id.textview_turkish);
            TextView textview_chaines = dialog.findViewById(R.id.textview_chaines);

            TinyDB tinyDB = new TinyDB(context);
            String appLanguage = tinyDB.getString("appLanguage");


            if(appLanguage.equals("en") || appLanguage.equals("")) {
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Choose Your \nDisplay Language");
                languageDialogSubHeader.setText("Please select one");
                tempLanguage = "en";
            }
            else if(appLanguage.equals("hi")) {
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("अपनी प्रदर्शन भाषा चुनें");
                languageDialogSubHeader.setText("कृपया एक का चयन करें");
                tempLanguage = "hi";
            }
            else if(appLanguage.equals("bn")) {
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("আপনার প্রদর্শন ভাষা \nচয়ন করুন");
                languageDialogSubHeader.setText("অনুগ্রহপূর্বক একটা নির্বাচন করুন");
                tempLanguage = "bn";
            }
            else if(appLanguage.equals("es")) {
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Elija su idioma \nde visualización");
                languageDialogSubHeader.setText("Por favor, seleccione uno");
                tempLanguage = "es";
            }
            else if(appLanguage.equals("ru")) {
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Выберите язык \nотображения");
                languageDialogSubHeader.setText("Пожалуйста, выберите один");
                tempLanguage = "ru";
            }
            else if(appLanguage.equals("tr")) {
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Görüntüleme Dilinizi \nSeçin");
                languageDialogSubHeader.setText("Lütfen birini seçin");
                tempLanguage = "tr";
            }
            else if(appLanguage.equals("zh")) {
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                languageDialogHeader.setText("选择您的显示语言");
                languageDialogSubHeader.setText("请选择一项");
                tempLanguage = "zh";
            }



            cardView_english.setOnClickListener(v1->{
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Choose Your \nDisplay Language");
                languageDialogSubHeader.setText("Please select one");
                tempLanguage = "en";
            });
            cardView_hindi.setOnClickListener(v2->{
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("अपनी प्रदर्शन भाषा चुनें");
                languageDialogSubHeader.setText("कृपया एक का चयन करें");
                tempLanguage = "hi";
            });
            cardView_bengali.setOnClickListener(v3->{
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("আপনার প্রদর্শন ভাষা \nচয়ন করুন");
                languageDialogSubHeader.setText("অনুগ্রহপূর্বক একটা নির্বাচন করুন");
                tempLanguage = "bn";
            });
            cardView_spanish.setOnClickListener(v4->{
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Elija su idioma \nde visualización");
                languageDialogSubHeader.setText("Por favor, seleccione uno");
                tempLanguage = "es";
            });
            cardView_russian.setOnClickListener(v5->{
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Выберите язык \nотображения");
                languageDialogSubHeader.setText("Пожалуйста, выберите один");
                tempLanguage = "ru";
            });
            cardView_turkish.setOnClickListener(v6->{
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                textview_chaines.setTextColor(Color.WHITE);
                languageDialogHeader.setText("Görüntüleme Dilinizi \nSeçin");
                languageDialogSubHeader.setText("Lütfen birini seçin");
                tempLanguage = "tr";
            });
            cardView_chaines.setOnClickListener(v7->{
                linearlayout_english.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_hindi.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_bengali.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_spanish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_russian.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_turkish.setBackground(AppCompatResources.getDrawable(context, R.drawable.language_dialog_bg));
                linearlayout_chaines.setBackground(AppCompatResources.getDrawable(context, R.color.white));
                textview_english.setTextColor(Color.WHITE);
                textview_hindi.setTextColor(Color.WHITE);
                textview_bengali.setTextColor(Color.WHITE);
                textview_spanish.setTextColor(Color.WHITE);
                textview_russian.setTextColor(Color.WHITE);
                textview_turkish.setTextColor(Color.WHITE);
                textview_chaines.setTextColor(ContextCompat.getColor(context, R.color.Red_Smooth));
                languageDialogHeader.setText("选择您的显示语言");
                languageDialogSubHeader.setText("请选择一项");
                tempLanguage = "zh";
            });

            ImageView Coupan_Dialog_save = dialog.findViewById(R.id.Coupan_Dialog_save);
            Coupan_Dialog_save.setOnClickListener(vSave->{
                if(tempLanguage.equals("en")) {
                    tinyDB.putString("appLanguage", "en");
                } else if(tempLanguage.equals("hi")) {
                    tinyDB.putString("appLanguage", "hi");
                }
                else if(tempLanguage.equals("bn")) {
                    tinyDB.putString("appLanguage", "bn");
                }
                else if(tempLanguage.equals("es")) {
                    tinyDB.putString("appLanguage", "es");
                }
                else if(tempLanguage.equals("ru")) {
                    tinyDB.putString("appLanguage", "ru");
                } else if(tempLanguage.equals("tr")) {
                    tinyDB.putString("appLanguage", "tr");
                }
                else if(tempLanguage.equals("zh")) {
                    tinyDB.putString("appLanguage", "zh");
                }
                dialog.dismiss();
                Intent languageIntent = new Intent(Home.this, Splash.class);
                startActivity(languageIntent);
            });


            dialog.show();
        });

        CardView report = findViewById(R.id.report);
        report.setOnClickListener(v->{
            if(userData != null) {
                JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                String cUserID = String.valueOf(jsonObject.get("ID").getAsInt());

                final Dialog dialog = new Dialog(Home.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.custom_report_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);

                ImageView dialogClose = (ImageView) dialog.findViewById(R.id.Coupan_Dialog_Close);
                dialogClose.setOnClickListener(v1 -> dialog.dismiss());

                EditText titleEditText = dialog.findViewById(R.id.titleEditText);

                MaterialSpinner typeSpinner = (MaterialSpinner) dialog.findViewById(R.id.typeSpinner);
                typeSpinner.setItems("Custom", "Movie", "Web Series", "Live TV");


                EditText descriptionEditText = dialog.findViewById(R.id.descriptionEditText);

                Button submitBtnButton = dialog.findViewById(R.id.submitBtn);
                submitBtnButton.setOnClickListener(btnView -> {
                    RequestQueue queue = Volley.newRequestQueue(this);
                    StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/add_report.php", response -> {
                        if (helperUtils.isInt(response)) {
                            dialog.dismiss();
                            Snackbar snackbar = Snackbar.make(rootView, "Report Successfully Submited!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Close", v12 -> snackbar.dismiss());
                            snackbar.show();
                        } else {
                            dialog.dismiss();
                            Snackbar snackbar = Snackbar.make(rootView, "Error: Something went Wrong!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Close", v13 -> snackbar.dismiss());
                            snackbar.show();
                        }
                    }, error -> {
                        // Do nothing because There is No Error if error It will return 0
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", String.valueOf(cUserID));
                            params.put("title", titleEditText.getText().toString());
                            params.put("description", descriptionEditText.getText().toString());
                            params.put("report_type", String.valueOf(typeSpinner.getSelectedIndex()));
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("x-api-key", AppConfig.apiKey);
                            return params;
                        }
                    };
                    queue.add(sr);
                });
                dialog.show();
            } else {
                Snackbar snackbar = Snackbar.make(rootView, "Login to Report Content!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Login", v14 -> {
                    Intent lsIntent = new Intent(Home.this, LoginSignup.class);
                    startActivity(lsIntent);
                });
                snackbar.show();
            }
        });

        CardView request = findViewById(R.id.request);
        request.setOnClickListener(v->{
            if(userData != null) {
                JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
                String cUserID = String.valueOf(jsonObject.get("ID").getAsInt());

                final Dialog dialog = new Dialog(Home.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.request_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);

                ImageView dialogClose = (ImageView) dialog.findViewById(R.id.Coupan_Dialog_Close);
                dialogClose.setOnClickListener(v1 -> dialog.dismiss());

                EditText titleEditText = dialog.findViewById(R.id.titleEditText);

                MaterialSpinner typeSpinner = (MaterialSpinner) dialog.findViewById(R.id.typeSpinner);
                typeSpinner.setItems("Custom", "Movie", "Web Series", "Live TV");


                EditText descriptionEditText = dialog.findViewById(R.id.descriptionEditText);

                Button submitBtnButton = dialog.findViewById(R.id.submitBtn);
                submitBtnButton.setOnClickListener(btnView -> {
                    RequestQueue queue = Volley.newRequestQueue(this);
                    StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/api/add_request.php", response -> {
                        if (helperUtils.isInt(response)) {
                            dialog.dismiss();
                            Snackbar snackbar = Snackbar.make(rootView, "Request Successfully Submited!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Close", v12 -> snackbar.dismiss());
                            snackbar.show();
                        } else {
                            dialog.dismiss();
                            Snackbar snackbar = Snackbar.make(rootView, "Error: Something went Wrong!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Close", v13 -> snackbar.dismiss());
                            snackbar.show();
                        }
                    }, error -> {
                        // Do nothing because There is No Error if error It will return 0
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", String.valueOf(cUserID));
                            params.put("title", titleEditText.getText().toString());
                            params.put("description", descriptionEditText.getText().toString());
                            params.put("type", String.valueOf(typeSpinner.getSelectedIndex()));
                            params.put("status", String.valueOf(0));
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("x-api-key", AppConfig.apiKey);
                            return params;
                        }
                    };
                    queue.add(sr);
                });
                dialog.show();
            } else {
                Snackbar snackbar = Snackbar.make(rootView, "Login to Report Content!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Login", v14 -> {
                    Intent lsIntent = new Intent(Home.this, LoginSignup.class);
                    startActivity(lsIntent);
                });
                snackbar.show();
            }
        });

        ConstraintLayout logoutBtn = findViewById(R.id.Logout_btn);
        logoutBtn.setOnClickListener(view -> {
            MaterialDialog mDialog = new MaterialDialog.Builder(Home.this)
                    .setTitle("Logout!")
                    .setMessage("Want to Logout?")
                    .setCancelable(true)
                    .setAnimation(R.raw.logout)
                    .setNegativeButton("Cancel", R.drawable.close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .setPositiveButton("Logout", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                        SharedPreferences settings = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                        settings.edit().clear().apply();

                        Intent splashActivity = new Intent(Home.this, Splash.class);
                        startActivity(splashActivity);
                        finish();
                    })
                    .build();

            // Show dialog
            mDialog.show();
        });

        ConstraintLayout shareBtn = findViewById(R.id.Share_btn);
        shareBtn.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_app_text));
            startActivity(Intent.createChooser(sharingIntent, "Share app via"));
        });




        searchContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(searchContentEditText.getText()).equals("")) {
                    bigSearchLottieAnimation.setVisibility(View.VISIBLE);
                    searchLayoutRecyclerView.setVisibility(View.GONE);
                } else  {
                    bigSearchLottieAnimation.setVisibility(View.GONE);
                    searchLayoutRecyclerView.setVisibility(View.VISIBLE);

                    searchContent(String.valueOf(searchContentEditText.getText()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });



        //Ad Controller
        if(!removeAds) {
            loadAd();
        } else {
            admobNativeadTemplateLayout.setVisibility(View.GONE);
        }

        forgotPasswordLayout = findViewById(R.id.Forgot_Password_Layout);
        TextView changePassword = findViewById(R.id.Change_Password);
        changePassword.setOnClickListener(view -> {
            if(userData != null) {
                if (forgotPasswordLayout.getVisibility() == View.GONE) {
                    forgetPasswordTab(true);
                } else {
                    forgetPasswordTab(false);
                }
            } else {
                Intent lsIntent = new Intent(Home.this, LoginSignup.class);
                startActivity(lsIntent);
            }
        });

        LinearLayout forgetPassExtraSpace = findViewById(R.id.Forget_Pass_Extra_space);
        forgetPassExtraSpace.setOnClickListener(view -> forgetPasswordTab(false));

        TextView forgetPasswordEmailEditText = findViewById(R.id.Forget_Password_Email_EditText);
        View sendOtp = findViewById(R.id.Send_OTP);
        //---------Send OTP----------//
        sendOtp.setOnClickListener(view -> {
            if(forgetPasswordEmailEditText.getText().toString().matches("")) {
                Toasty.warning(context, "Please Enter Your Email Address Correctly.", Toast.LENGTH_SHORT, true).show();
            } else {
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/password_reset/password_reset_api.php?mail="+forgetPasswordEmailEditText.getText()+"&appurl="+AppConfig.url, response -> {
                    switch (response) {
                        case "":
                        case "Something Went Wrong!":
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "Email Not Registered":
                            Toasty.warning(context, "Email Not Registered", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:
                            Toasty.success(context, "instructions Sended To the Mail Address!", Toast.LENGTH_SHORT, true).show();
                            break;
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
        });

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

        liveTvLayout = findViewById(R.id.LiveTV_Layout);
        if(liveTvVisiableInHome == 0) {
            liveTvLayout.setVisibility(View.GONE);
        } else if(liveTvVisiableInHome == 1) {
            liveTvLayout.setVisibility(View.VISIBLE);
        } else  {
            liveTvLayout.setVisibility(View.VISIBLE);
        }

        SwitchMaterial includePremiumSwitch = findViewById(R.id.includePremiumSwitch);
        includePremiumSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    onlyPremium = 1;
                } else {
                    onlyPremium = 0;
                }
            }
        });

        ImageView clearContinuePlaying = findViewById(R.id.clearContinuePlaying);
        clearContinuePlaying.setOnClickListener(view -> {
            MaterialDialog mDialog = new MaterialDialog.Builder(Home.this)
                    .setTitle("Clear Continue Playing List?")
                    .setMessage("You can't Revert this!")
                    .setCancelable(false)
                    .setAnimation(R.raw.delete)
                    .setPositiveButton("Yes", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                        dialogInterface.dismiss();

                        db.resumeContentDao().clearDB();
                        loadResumeContents(db.resumeContentDao().getResumeContents());
                        resume_Layout.setVisibility(View.GONE);
                    })
                    .setNegativeButton("NO", R.drawable.ic_baseline_exit, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            mDialog.show();
        });
    }
    //OnCreate Finish

    void loadGenre() {
        List<GenreList> genreList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_featured_genre.php", response -> {
            if(!response.equals("No Data Avaliable")) {
                if(genre_visible_in_home == 1) {
                    genreLayout.setVisibility(View.VISIBLE);
                }
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String icon = rootObject.get("icon").getAsString();
                    String description = rootObject.get("description").getAsString();
                    int featured = rootObject.get("featured").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        genreList.add(new GenreList(id, name, icon, description, featured, status));
                    }
                }

                GenreListAdepter myadepter = new GenreListAdepter(context, genreList);
                genre_list_Recycler_View.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                genre_list_Recycler_View.setAdapter(myadepter);
            } else {
                genreLayout.setVisibility(View.GONE);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    void loadResumeContents(List resumeContents) {
        List<ResumeContent> mData = resumeContents;
        List<ContinuePlayingList> continuePlayingList = new ArrayList<>();

        for (int i=0; i<mData.size(); i++) {

            int id = mData.get(i).getId();
            int contentID = mData.get(i).getContent_id();

            String contentType = mData.get(i).getContent_type();

            String name = mData.get(i).getName();

            String year = "";
            if (!mData.get(i).getYear().equals("")) {
                year = getYearFromDate(mData.get(i).getYear());
            }
            String poster = mData.get(i).getPoster();
            String sourceType = mData.get(i).getSource_type();
            String sourceUrl = mData.get(i).getSource_url();
            int type = mData.get(i).getType();
            long position = mData.get(i).getPosition();
            long duration = mData.get(i).getDuration();

            continuePlayingList.add(new ContinuePlayingList(id, contentID, name, year, poster, sourceType, sourceUrl, type, contentType, position, duration));

            RecyclerView continuePlaying_list_Recycler_View = findViewById(R.id.continuePlaying_list_Recycler_View);
            ContinuePlayingListAdepter myadepter = new ContinuePlayingListAdepter(context, continuePlayingList);
            continuePlaying_list_Recycler_View.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
            continuePlaying_list_Recycler_View.setAdapter(myadepter);

        }

    }

    private void forgetPasswordTab(boolean show) {
        ViewGroup loginSignup = findViewById(R.id.Home);

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.Forgot_Password_Layout);

        TransitionManager.beginDelayedTransition(loginSignup, transition);
        forgotPasswordLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadAd() {
        adViewLayout = findViewById(R.id.ad_View_Layout);
        bannerViewLayout = findViewById(R.id.banner_View_Layout);

        if (adType == 1) {   //AdMob
            admobNativeadTemplateLayout.setVisibility(View.VISIBLE);
            adViewLayout.setVisibility(View.GONE);
            RelativeLayout fBanner_View_Layout = findViewById(R.id.fBanner_View_Layout);

            //Home Header Banner Ad
            AdView mAdView1 = new AdView(context);
            mAdView1.setAdSize(AdSize.BANNER);
            mAdView1.setAdUnitId(AppConfig.adMobBanner);
            (fBanner_View_Layout).addView(mAdView1);
            AdRequest adRequest1 = new AdRequest.Builder().build();
            mAdView1.loadAd(adRequest1);

            //Home Footer Banner Ad
            AdView mAdView = new AdView(context);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(AppConfig.adMobBanner);
            (bannerViewLayout).addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        } else if (adType == 2) { // StartApp
            admobNativeadTemplateLayout.setVisibility(View.GONE);
            // Define StartApp Banner
            Banner startAppBanner = new Banner(this);
            RelativeLayout.LayoutParams bannerParameters =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
            bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            // Add to main Layout
            adViewLayout.addView(startAppBanner, bannerParameters);

            // Define StartApp Mrec
            Mrec startAppMrec = new Mrec(this);
            RelativeLayout.LayoutParams mrecParameters =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            mrecParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mrecParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            // Add to main Layout
            bannerViewLayout.addView(startAppMrec, mrecParameters);

        } else if (adType == 3) { //Facebook
            admobNativeadTemplateLayout.setVisibility(View.GONE);

            AudienceNetworkAds.initialize(context);

            //Home Header Banner Ad
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this, AppConfig.facebook_banner_ads_placement_id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adViewLayout.addView(adView);
            adView.loadAd();

            //Home Footer Banner Ad
            com.facebook.ads.AdView adViewFooter = new com.facebook.ads.AdView(this, AppConfig.facebook_banner_ads_placement_id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            bannerViewLayout.addView(adViewFooter);
            adViewFooter.loadAd();

        } else if(adType == 4) { //AdColony
            admobNativeadTemplateLayout.setVisibility(View.GONE);

            String[] AdColony_AD_UNIT_Zone_Ids = new String[] {AppConfig.AdColony_BANNER_ZONE_ID,AppConfig.AdColony_INTERSTITIAL_ZONE_ID};
            AdColony.configure(this, AppConfig.AdColony_APP_ID, AdColony_AD_UNIT_Zone_Ids);

            AdColonyAdViewListener listener = new AdColonyAdViewListener() {
                @Override
                public void onRequestFilled(AdColonyAdView adColonyAdView) {
                    adViewLayout.addView(adColonyAdView);
                }
            };
            AdColony.requestAdView(AppConfig.AdColony_BANNER_ZONE_ID, listener, AdColonyAdSize.BANNER);
        } else if(adType == 5) { //unityads
            admobNativeadTemplateLayout.setVisibility(View.GONE);

            IUnityAdsListener unityAdsListener = new IUnityAdsListener() {
                @Override
                public void onUnityAdsReady(String s) {
                    BannerView topBanner = new BannerView(Home.this, AppConfig.Unity_Banner_ID, new UnityBannerSize(320, 50));
                    topBanner.load();
                    adViewLayout.addView(topBanner);
                    BannerView topBanner2 = new BannerView(Home.this, AppConfig.Unity_Banner_ID, new UnityBannerSize(320, 50));
                    topBanner2.load();
                    bannerViewLayout.addView(topBanner2);
                }

                @Override
                public void onUnityAdsStart(String s) {

                }

                @Override
                public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {

                }

                @Override
                public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                }
            };
            UnityAds.initialize (this, AppConfig.Unity_Game_ID, unityAdsListener, AppConfig.unity_ad_testMode);
        } else if(adType == 6) { //Custom Ads
            admobNativeadTemplateLayout.setVisibility(View.GONE);
            adViewLayout.setVisibility(View.GONE);
            bannerViewLayout.setVisibility(View.GONE);

            if(!AppConfig.Custom_Banner_url.equals("")) {
                ImageView custom_banner_ad = findViewById(R.id.custom_banner_ad);
                custom_banner_ad.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(AppConfig.Custom_Banner_url)
                        .into(custom_banner_ad);
                custom_banner_ad.setOnClickListener(view -> {
                    if(!AppConfig.Custom_Banner_click_url.equals("")) {
                        switch (AppConfig.Custom_Banner_click_url_type) {
                            case 1:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.Custom_Banner_click_url)));
                                break;
                            case 2:
                                Intent intent = new Intent(Home.this, WebView.class);
                                intent.putExtra("URL", AppConfig.Custom_Banner_click_url);
                                startActivity(intent);
                                break;
                            default:
                        }
                    }
                });
            }

            if(!AppConfig.Custom_Banner_url.equals("")) {
                ImageView custom_footer_banner_ad = findViewById(R.id.custom_footer_banner_ad);
                custom_footer_banner_ad.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(AppConfig.Custom_Banner_url)
                        .into(custom_footer_banner_ad);
                custom_footer_banner_ad.setOnClickListener(view -> {
                    if(!AppConfig.Custom_Banner_click_url.equals("")) {
                        switch (AppConfig.Custom_Banner_click_url_type) {
                            case 1:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.Custom_Banner_click_url)));
                                break;
                            case 2:
                                Intent intent = new Intent(Home.this, WebView.class);
                                intent.putExtra("URL", AppConfig.Custom_Banner_click_url);
                                startActivity(intent);
                                break;
                            default:
                        }
                    }
                });
            }

        } else {
            admobNativeadTemplateLayout.setVisibility(View.GONE);
            bannerViewLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //---------Login Layout------------//
        EditText searchContentEditText = findViewById(R.id.Search_content_editText);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (searchContentEditText.isFocused()) {
                Rect outRect = new Rect();
                searchContentEditText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    searchContentEditText.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    void searchContent(String text) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/search_content.php?search="+text+"&onlypremium="+onlyPremium, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, name, year, poster, contentType));
                    }
                }


                RecyclerView searchLayoutRecyclerView = findViewById(R.id.Search_Layout_RecyclerView);
                SearchListAdepter myadepter = new SearchListAdepter(context, searchList);
                searchLayoutRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                searchLayoutRecyclerView.setAdapter(myadepter);

            } else {
                View bigSearchLottieAnimation = findViewById(R.id.big_search_Lottie_animation);
                RecyclerView searchLayoutRecyclerView = findViewById(R.id.Search_Layout_RecyclerView);
                bigSearchLottieAnimation.setVisibility(View.VISIBLE);
                searchLayoutRecyclerView.setVisibility(View.GONE);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    void customImageSlider() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_image_slider_items.php", this::onResponse, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    void topMoviesImageSlider() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_image_slider_movie_list.php", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                int i = 0;
                int maxVisible = movieImageSliderMaxVisible;
                for (JsonElement r : jsonArray) {
                    if (i < maxVisible) {
                        JsonObject rootObject = r.getAsJsonObject();
                        int id = rootObject.get("id").getAsInt();
                        String name = rootObject.get("name").getAsString();
                        String banner = rootObject.get("banner").getAsString();
                        int status = rootObject.get("status").getAsInt();

                        if (status == 1) {
                            imageSliderItems.add(new ImageSliderItem(banner, name, 0, id, ""));
                        }
                        i++;
                    }
                }
                viewPager2.setAdapter(new ImageSliderAdepter(imageSliderItems, viewPager2));
            } else {
                viewPager2.setVisibility(View.GONE);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    void topWebSeriesImageSlider() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_image_slider_webseries_list.php", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                int i = 0;
                int maxVisible = webseriesImageSliderMaxVisible;
                for (JsonElement r : jsonArray) {
                    if (i < maxVisible) {
                        JsonObject rootObject = r.getAsJsonObject();
                        int id = rootObject.get("id").getAsInt();
                        String name = rootObject.get("name").getAsString();
                        String banner = rootObject.get("banner").getAsString();
                        int status = rootObject.get("status").getAsInt();

                        if (status == 1) {
                            imageSliderItems.add(new ImageSliderItem(banner, name, 1, id, ""));
                        }
                        i++;
                    }
                }
                viewPager2.setAdapter(new ImageSliderAdepter(imageSliderItems, viewPager2));
            } else {
                viewPager2.setVisibility(View.GONE);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    private void setData() {
        JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);

        TextView userTitle = findViewById(R.id.User_Title);
        TextView profileName = findViewById(R.id.Profile_Name);
        TextView profileEmail = findViewById(R.id.Profile_Email);
        TextView accountType = findViewById(R.id.Account_Type);
        TextView accountSubscriptionDate = findViewById(R.id.Account_Subscription_Date);
        TextView accountExp = findViewById(R.id.accountExp);
        LinearLayout loginBtnLayout = findViewById(R.id.loginBtnLayout);

        if(userData == null) {
            String guestName = "Guest";
            userTitle.setText(guestName);
            profileName.setText(guestName);
            accountExp.setVisibility(View.GONE);
            accountSubscriptionDate.setVisibility(View.GONE);
            profileEmail.setVisibility(View.GONE);

            accountType.setText(guestName);

            TextView changePassword = findViewById(R.id.Change_Password);
            changePassword.setText("SignUp");

            profileEmail.setVisibility(View.GONE);
            loginBtnLayout.setVisibility(View.VISIBLE);

            loginBtnLayout.setOnClickListener(view->{
                Intent loginSignupActivity = new Intent(Home.this, LoginSignup.class);
                startActivity(loginSignupActivity);
            });

            //Logout
            CardView cardView94 = findViewById(R.id.cardView94);
            cardView94.setVisibility(View.GONE);
        } else {
            String name = jsonObject.get("Name").getAsString();
            String email = jsonObject.get("Email").getAsString();
            int subscriptionType = jsonObject.get("subscription_type").getAsInt();
            String subscriptionExp = jsonObject.get("subscription_exp").getAsString();

            userTitle.setText(name.split(" (?!.* )")[0]);
            profileName.setText(name);
            profileEmail.setText(email);

            loginBtnLayout.setVisibility(View.GONE);
            profileEmail.setVisibility(View.VISIBLE);

            if(subscriptionType == 0) {
                accountType.setText("Free");
                accountSubscriptionDate.setText("NEVER");
            } else {
                accountType.setText("Premium");
                accountType.setTextColor(getColor(R.color.Deep_Yellow));
                accountSubscriptionDate.setText(subscriptionExp);
            }

        }
    }


    private void loadUserSubscriptionDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String subscriptionType = sharedPreferences.getString("subscription_type", null);

        String number = String.valueOf(subscriptionType);
        for(int i = 0; i < number.length(); i++) {
            int userSubType = Character.digit(number.charAt(i), 10);
            if(userSubType == 1) {
                removeAds = true;
            } else if(userSubType == 2) {
                playPremium = true;
            } else if(userSubType == 3) {
                downloadPremium = true;
            } else {
                removeAds = false;
                playPremium = false;
                downloadPremium = false;
            }
        }
    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        config = sharedPreferences.getString("Config", null);

        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        imageSliderType = jsonObject.get("image_slider_type").getAsString();
        movieImageSliderMaxVisible = jsonObject.get("movie_image_slider_max_visible").getAsInt();
        webseriesImageSliderMaxVisible = jsonObject.get("webseries_image_slider_max_visible").getAsInt();

        adType = jsonObject.get("ad_type").getAsInt();

        shuffleContents = jsonObject.get("shuffle_contents").getAsInt();

        showMessage  = jsonObject.get("Show_Message").getAsInt();
        messageTitle = jsonObject.get("Message_Title").getAsString();
        message = jsonObject.get("Message").getAsString();
        if(showMessage == 1) {
            helperUtils = new HelperUtils(Home.this);
            helperUtils.showMsgDialog(Home.this, messageTitle, message, R.raw.message_in_a_bottle);
        }

        liveTvVisiableInHome = jsonObject.get("LiveTV_Visiable_in_Home").getAsInt();

        genre_visible_in_home = jsonObject.get("genre_visible_in_home").getAsInt();

        if(genre_visible_in_home == 0) {
            genreLayout.setVisibility(View.GONE);
        } else if(genre_visible_in_home == 1) {
            genreLayout.setVisibility(View.VISIBLE);
        } else {
            genreLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userData = sharedPreferences.getString("UserData", null);

        setData();
    }

    @Override
    public void onBackPressed() {
        if(forgotPasswordLayout.getVisibility() == View.VISIBLE) {
            forgetPasswordTab(false);
        } else {
            finish();
        }
    }

    void loadhomecontentlist() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_rand_content_list.php?content_type=Movies", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> movieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        movieList.add(new MovieList(id, type, name, year, poster));
                    }
                }

                if(shuffleContents == 1) {
                    Collections.shuffle(movieList);
                }

                RecyclerView movieListRecycleview = findViewById(R.id.home_Movie_list_Recycler_View);
                MovieListAdepter myadepter = new MovieListAdepter(context, movieList);
                movieListRecycleview.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                movieListRecycleview.setAdapter(myadepter);

            } else {
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);

        StringRequest sr2 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_rand_content_list.php?content_type=WebSeries", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<WebSeriesList> webSeriesList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        webSeriesList.add(new WebSeriesList(id, type, name, year, poster));
                    }
                }
                if(shuffleContents == 1) {
                    Collections.shuffle(webSeriesList);
                }

                RecyclerView webSeriesListRecycleview = findViewById(R.id.home_Web_Series_list_Recycler_View);
                WebSeriesListAdepter myadepter = new WebSeriesListAdepter(context, webSeriesList);
                webSeriesListRecycleview.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                webSeriesListRecycleview.setAdapter(myadepter);

                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);

            } else {
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr2);



        ///////////////////////////////////////////////
        StringRequest sr3 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_resent_content_list.php?content_type=Movies", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> recentlyAddedMovieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        recentlyAddedMovieList.add(new MovieList(id, type, name, year, poster));
                    }
                }

                if(shuffleContents == 1) {
                    Collections.shuffle(recentlyAddedMovieList);
                }

                RecyclerView movieListRecycleview = findViewById(R.id.home_Recent_Movies_list_Recycler_View);
                MovieListAdepter myadepter = new MovieListAdepter(context, recentlyAddedMovieList);
                movieListRecycleview.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                movieListRecycleview.setAdapter(myadepter);

            } else {
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr3);


        StringRequest sr4 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_resent_content_list.php?content_type=WebSeries", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<WebSeriesList> recentlyAddedWebSeriesList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        recentlyAddedWebSeriesList.add(new WebSeriesList(id, type, name, year, poster));
                    }
                }
                if(shuffleContents == 1) {
                    Collections.shuffle(recentlyAddedWebSeriesList);
                }

                RecyclerView webSeriesListRecycleview = findViewById(R.id.home_Recent_Series_list_Recycler_View);
                WebSeriesListAdepter myadepter = new WebSeriesListAdepter(context, recentlyAddedWebSeriesList);
                webSeriesListRecycleview.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                webSeriesListRecycleview.setAdapter(myadepter);

                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);

            } else {
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr4);

        StringRequest sr5 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_live_tv_channel_list.php?filter=featured", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<LiveTvChannelList> liveTVChannelList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String banner = rootObject.get("banner").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    String streamType = rootObject.get("stream_type").getAsString();
                    String url = rootObject.get("url").getAsString();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        liveTVChannelList.add(new LiveTvChannelList(id, name, banner, streamType, url, contentType, type, playPremium));
                    }
                }

                if(shuffleContents == 1) {
                    Collections.shuffle(liveTVChannelList);
                }

                RecyclerView homeLiveTVlistRecyclerView = findViewById(R.id.home_Live_TV_list_Recycler_View);
                LiveTvChannelListAdepter myadepter = new LiveTvChannelListAdepter(context, liveTVChannelList);
                homeLiveTVlistRecyclerView.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                homeLiveTVlistRecyclerView.setAdapter(myadepter);

            } else {
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr5);





        //----------------------------------//
        String tempUserID = null;
        if(userData != null) {
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            tempUserID = String.valueOf(jsonObject.get("ID").getAsInt());
        } else {
            tempUserID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        /////=======================////
        StringRequest sr6 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/beacause_you_watched.php?userID="+tempUserID+"&limit=10&filter=Movies", response -> {
            if(!response.equals("No Data Avaliable")) {
                LinearLayout bywMovieLayoutLinearLayout= findViewById(R.id.bywMovieLayout);
                bywMovieLayoutLinearLayout.setVisibility(View.VISIBLE);
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> movieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        movieList.add(new MovieList(id, type, name, year, poster));
                    }
                }

                Collections.shuffle(movieList);

                RecyclerView home_bywm_list_Recycler_View = findViewById(R.id.home_bywm_list_Recycler_View);
                moviesOnlyForYouListAdepter myadepter = new moviesOnlyForYouListAdepter(context, movieList);
                home_bywm_list_Recycler_View.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                home_bywm_list_Recycler_View.setAdapter(myadepter);

            } else {
                LinearLayout bywMovieLayoutLinearLayout= findViewById(R.id.bywMovieLayout);
                bywMovieLayoutLinearLayout.setVisibility(View.GONE);
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr6);

        StringRequest sr7 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/beacause_you_watched.php?userID="+tempUserID+"&limit=10&filter=WebSeries", response -> {
            if(!response.equals("No Data Avaliable")) {
                LinearLayout bywWebSeriesLayout= findViewById(R.id.bywWebSeriesLayout);
                bywWebSeriesLayout.setVisibility(View.VISIBLE);
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<WebSeriesList> webSeriesList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        webSeriesList.add(new WebSeriesList(id, type, name, year, poster));
                    }
                }

                Collections.shuffle(webSeriesList);

                RecyclerView home_bywws_list_Recycler_View = findViewById(R.id.home_bywws_list_Recycler_View);
                webSeriesOnlyForYouListAdepter myadepter = new webSeriesOnlyForYouListAdepter(context, webSeriesList);
                home_bywws_list_Recycler_View.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                home_bywws_list_Recycler_View.setAdapter(myadepter);

                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);

            } else {
                LinearLayout bywWebSeriesLayout= findViewById(R.id.bywWebSeriesLayout);
                bywWebSeriesLayout.setVisibility(View.GONE);
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr7);

        StringRequest sr8 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_most_watched.php?filter=Movies&limit=10", response -> {
            if(!response.equals("No Data Avaliable")) {
                LinearLayout popularMoviesLayout= findViewById(R.id.popularMoviesLayout);
                popularMoviesLayout.setVisibility(View.VISIBLE);
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> movieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        movieList.add(new MovieList(id, type, name, year, poster));
                    }
                }

                Collections.shuffle(movieList);

                RecyclerView home_popularMovies_list_Recycler_View = findViewById(R.id.home_popularMovies_list_Recycler_View);
                moviesOnlyForYouListAdepter myadepter = new moviesOnlyForYouListAdepter(context, movieList);
                home_popularMovies_list_Recycler_View.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                home_popularMovies_list_Recycler_View.setAdapter(myadepter);

                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);

            } else {
                LinearLayout popularMoviesLayout= findViewById(R.id.popularMoviesLayout);
                popularMoviesLayout.setVisibility(View.GONE);
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr8);



        StringRequest sr9 = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_most_watched.php?filter=WebSeries&limit=10", response -> {
            if(!response.equals("No Data Avaliable")) {
                LinearLayout popularWebSeriesLayout= findViewById(R.id.popularWebSeriesLayout);
                popularWebSeriesLayout.setVisibility(View.VISIBLE);
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<WebSeriesList> webSeriesList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        webSeriesList.add(new WebSeriesList(id, type, name, year, poster));
                    }
                }

                Collections.shuffle(webSeriesList);

                RecyclerView home_popularWebSeries_list_Recycler_View = findViewById(R.id.home_popularWebSeries_list_Recycler_View);
                webSeriesOnlyForYouListAdepter myadepter = new webSeriesOnlyForYouListAdepter(context, webSeriesList);
                home_popularWebSeries_list_Recycler_View.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                home_popularWebSeries_list_Recycler_View.setAdapter(myadepter);

                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);

            } else {
                LinearLayout popularWebSeriesLayout= findViewById(R.id.popularWebSeriesLayout);
                popularWebSeriesLayout.setVisibility(View.GONE);
                SwipeRefreshLayout homeSwipeRefreshLayout = findViewById(R.id.Home_Swipe_Refresh_Layout);
                homeSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr9);

    }





    void movieList() {
        final int[] previousTotal = {0};
        final boolean[] loading = {true};
        int visibleThreshold = 3;
        final int[] firstVisibleItem = new int[1];
        final int[] visibleItemCount = new int[1];
        final int[] totalItemCount = new int[1];
        final int[] currentPage = {0};

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_movie_list.php?page="+ currentPage[0], new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    List<MovieList> movieList = new ArrayList<>();
                    for (JsonElement r : jsonArray) {
                        JsonObject rootObject = r.getAsJsonObject();
                        int id = rootObject.get("id").getAsInt();
                        String name = rootObject.get("name").getAsString();

                        String year = "";
                        if(!rootObject.get("release_date").getAsString().equals("")) {
                            year = getYearFromDate(rootObject.get("release_date").getAsString());
                        }

                        String poster = rootObject.get("poster").getAsString();
                        int type = rootObject.get("type").getAsInt();
                        int status = rootObject.get("status").getAsInt();

                        if (status == 1) {
                            //movieList.add(new MovieList(id, type, name, year, poster));
                        }
                    }

                    if(shuffleContents == 1) {
                        Collections.shuffle(movieList);
                    }

                    RecyclerView movieListRecycleview = findViewById(R.id.movie_list_recycleview);
                    //GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                    //movieListRecycleview.setLayoutManager(gridLayoutManager);

                    FlexboxLayoutManager gridLayoutManager = new FlexboxLayoutManager(context);
                    gridLayoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
                    movieListRecycleview.setLayoutManager(gridLayoutManager);


                    AllMovieListAdepter myadepter = new AllMovieListAdepter(context, movieList);
                    movieListRecycleview.setAdapter(myadepter);

                    movieListRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {

                        @Override
                        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            visibleItemCount[0] = movieListRecycleview.getChildCount();
                            totalItemCount[0] = gridLayoutManager.getItemCount();
                            firstVisibleItem[0] = gridLayoutManager.findFirstVisibleItemPosition();

                            if (loading[0]) {
                                if (totalItemCount[0] > previousTotal[0]) {
                                    loading[0] = false;
                                    previousTotal[0] = totalItemCount[0];
                                }
                            }
                            if (!loading[0] && (totalItemCount[0] - visibleItemCount[0])
                                    <= (firstVisibleItem[0] + visibleThreshold)) {
                                // End has been reached
                                loading[0] = true;

                                currentPage[0]++;

                                RequestQueue queue = Volley.newRequestQueue(Home.this);
                                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_movie_list.php?page="+ currentPage[0], response1 -> {
                                    if (!response1.equals("No Data Avaliable")) {
                                        JsonArray jsonArray1 = new Gson().fromJson(response1, JsonArray.class);
                                        for (JsonElement r : jsonArray1) {
                                            JsonObject rootObject = r.getAsJsonObject();
                                            int id = rootObject.get("id").getAsInt();
                                            String name = rootObject.get("name").getAsString();

                                            String year = "";
                                            if(!rootObject.get("release_date").getAsString().equals("")) {
                                                year = getYearFromDate(rootObject.get("release_date").getAsString());
                                            }

                                            String poster = rootObject.get("poster").getAsString();
                                            int type = rootObject.get("type").getAsInt();
                                            int status = rootObject.get("status").getAsInt();

                                            if (status == 1) {
                                                movieList.add(new MovieList(id, type, name, year, poster));
                                            }
                                        }
                                        myadepter.notifyDataSetChanged();
                                        loading[0] = false;
                                    }
                                }, error -> {
                                    // Do nothing because There is No Error if error It will return 0
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
                        }
                    });


                    View moviesShimmerLayout = findViewById(R.id.Movies_Shimmer_Layout);
                    moviesShimmerLayout.setVisibility(View.GONE);
                    movieListRecycleview.setVisibility(View.VISIBLE);

                    SwipeRefreshLayout movieSwipeRefreshLayout = findViewById(R.id.Movie_Swipe_Refresh_Layout);
                    movieSwipeRefreshLayout.setRefreshing(false);

                } else {
                    SwipeRefreshLayout movieSwipeRefreshLayout = findViewById(R.id.Movie_Swipe_Refresh_Layout);
                    movieSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    void webSeriesList() {
        final int[] previousTotal = {0};
        final boolean[] loading = {true};
        int visibleThreshold = 3;
        final int[] firstVisibleItem = new int[1];
        final int[] visibleItemCount = new int[1];
        final int[] totalItemCount = new int[1];
        final int[] currentPage = {0};

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_web_series_list.php?page="+ currentPage[0], new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    List<WebSeriesList> webSeriesList = new ArrayList<>();
                    for (JsonElement r : jsonArray) {
                        JsonObject rootObject = r.getAsJsonObject();
                        int id = rootObject.get("id").getAsInt();
                        String name = rootObject.get("name").getAsString();

                        String year = "";
                        if(!rootObject.get("release_date").getAsString().equals("")) {
                            year = getYearFromDate(rootObject.get("release_date").getAsString());
                        }

                        String poster = rootObject.get("poster").getAsString();
                        int type = rootObject.get("type").getAsInt();
                        int status = rootObject.get("status").getAsInt();

                        if (status == 1) {
                            //webSeriesList.add(new WebSeriesList(id, type, name, year, poster));
                        }
                    }

                    if(shuffleContents == 1) {
                        Collections.shuffle(webSeriesList);
                    }

                    RecyclerView webSeriesListRecycleview = findViewById(R.id.web_series_list_recycleview);
                    AllWebSeriesListAdepter myadepter = new AllWebSeriesListAdepter(context, webSeriesList);
                    //GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                    //webSeriesListRecycleview.setLayoutManager(gridLayoutManager);

                    FlexboxLayoutManager gridLayoutManager = new FlexboxLayoutManager(context);
                    gridLayoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);
                    webSeriesListRecycleview.setLayoutManager(gridLayoutManager);

                    webSeriesListRecycleview.setAdapter(myadepter);

                    ///////////////
                    webSeriesListRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {

                        @Override
                        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            visibleItemCount[0] = webSeriesListRecycleview.getChildCount();
                            totalItemCount[0] = gridLayoutManager.getItemCount();
                            firstVisibleItem[0] = gridLayoutManager.findFirstVisibleItemPosition();

                            if (loading[0]) {
                                if (totalItemCount[0] > previousTotal[0]) {
                                    loading[0] = false;
                                    previousTotal[0] = totalItemCount[0];
                                }
                            }
                            if (!loading[0] && (totalItemCount[0] - visibleItemCount[0])
                                    <= (firstVisibleItem[0] + visibleThreshold)) {
                                // End has been reached
                                loading[0] = true;
                                //
                                currentPage[0]++;
                                //
                                RequestQueue queue = Volley.newRequestQueue(Home.this);
                                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_web_series_list.php?page="+ currentPage[0], response1 -> {
                                    if(!response1.equals("No Data Avaliable")) {
                                        JsonArray jsonArray1 = new Gson().fromJson(response1, JsonArray.class);
                                        for (JsonElement r : jsonArray1) {
                                            JsonObject rootObject = r.getAsJsonObject();
                                            int id = rootObject.get("id").getAsInt();
                                            String name = rootObject.get("name").getAsString();

                                            String year = "";
                                            if(!rootObject.get("release_date").getAsString().equals("")) {
                                                year = getYearFromDate(rootObject.get("release_date").getAsString());
                                            }

                                            String poster = rootObject.get("poster").getAsString();
                                            int type = rootObject.get("type").getAsInt();
                                            int status = rootObject.get("status").getAsInt();

                                            if (status == 1) {
                                                webSeriesList.add(new WebSeriesList(id, type, name, year, poster));
                                            }
                                        }
                                        myadepter.notifyDataSetChanged();
                                        loading[0] = false;
                                    }
                                }, error -> {
                                    // Do nothing because There is No Error if error It will return 0
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String,String> params = new HashMap<>();
                                        params.put("x-api-key", AppConfig.apiKey);
                                        return params;
                                    }
                                };
                                queue.add(sr);
                                // Do something


                            }
                        }
                    });

                    View seriesShimmerLayout = findViewById(R.id.Series_Shimmer_Layout);
                    seriesShimmerLayout.setVisibility(View.GONE);
                    webSeriesListRecycleview.setVisibility(View.VISIBLE);

                    SwipeRefreshLayout webSeriesSwipeRefreshLayout = findViewById(R.id.Web_Series_Swipe_Refresh_Layout);
                    webSeriesSwipeRefreshLayout.setRefreshing(false);

                } else {
                    SwipeRefreshLayout webSeriesSwipeRefreshLayout = findViewById(R.id.Web_Series_Swipe_Refresh_Layout);
                    webSeriesSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    String getYearFromDate(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = null;
        try {
            parsedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(parsedDate);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Home.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Home.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        resumeContents = db.resumeContentDao().getResumeContents();
        loadResumeContents(resumeContents);
        loadhomecontentlist();

        if(resumeContents.isEmpty()) {
            resume_Layout.setVisibility(View.GONE);
        } else {
            resume_Layout.setVisibility(View.VISIBLE);
        }

    }

    private void onResponse(String response) {
        if (!response.equals("No Data Avaliable")) {
            JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
            for (JsonElement r : jsonArray) {
                JsonObject rootObject = r.getAsJsonObject();

                String title = rootObject.get("title").getAsString();
                String banner = rootObject.get("banner").getAsString();
                int contentType = rootObject.get("content_type").getAsInt();
                int contentId = rootObject.get("content_id").getAsInt();
                String url = rootObject.get("url").getAsString();
                int status = rootObject.get("status").getAsInt();

                if (status == 1) {
                    imageSliderItems.add(new ImageSliderItem(banner, title, contentType, contentId, url));
                }
            }
            viewPager2.setAdapter(new ImageSliderAdepter(imageSliderItems, viewPager2));
        } else {
            viewPager2.setVisibility(View.GONE);
        }
    }
}
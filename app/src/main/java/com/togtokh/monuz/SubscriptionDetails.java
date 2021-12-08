package com.togtokh.monuz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.togtokh.monuz.utils.HelperUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionDetails extends AppCompatActivity {
    private boolean vpnStatus;
    private HelperUtils helperUtils;
    LinearLayout payNow;
    View payment_dialog;

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

        setContentView(R.layout.activity_subscription_details);


        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(SubscriptionDetails.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(SubscriptionDetails.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
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

        Intent intent = getIntent();
        int id = intent.getExtras().getInt("ID");
        loadSubscriptionDetails(id);

        payment_dialog = findViewById(R.id.payment_dialog);

        payNow = findViewById(R.id.Pay_Now);
        payNow.setOnClickListener(view -> {
            if(payment_dialog.getVisibility() != View.VISIBLE) {
                paymentDialog(true);
            } else {
                paymentDialog(false);
            }
        });

        LinearLayout payment_dialog_Extra_space = findViewById(R.id.payment_dialog_Extra_space);
        payment_dialog_Extra_space.setOnClickListener(v -> {
            paymentDialog(false);
        });
    }

    private void paymentDialog(boolean show) {
        CardView Upgrade_to_premium = findViewById(R.id.Upgrade_to_premium);
        ViewGroup subscription_details = findViewById(R.id.subscription_details);

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(payment_dialog);

        TransitionManager.beginDelayedTransition(subscription_details, transition);
        Upgrade_to_premium.setVisibility(show ? View.GONE : View.VISIBLE);
        payment_dialog.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    void loadSubscriptionDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"/api/get_subscription_details.php?ID="+id, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                int id1 = jsonObject.get("id").getAsInt();
                String name = jsonObject.get("name").getAsString();
                int time = jsonObject.get("time").getAsInt();
                int amount = jsonObject.get("amount").getAsInt();
                int currency = jsonObject.get("currency").getAsInt();
                String background = jsonObject.get("background").getAsString();
                int subscriptionType = jsonObject.get("subscription_type").getAsInt();
                int status = jsonObject.get("status").getAsInt();


                TextView nameTextView = findViewById(R.id.Name_TextView);
                nameTextView.setText(name);

                TextView timeTextView = findViewById(R.id.Time_TextView);
                timeTextView.setText(String.valueOf(time+"Days"));

                TextView amountTextView= findViewById(R.id.Amount_TextView);
                amountTextView.setText(String.valueOf("₹"+amount));

                ImageView subscriptionTtemBg= findViewById(R.id.Subscription_item_bg);
                Glide.with(SubscriptionDetails.this)
                        .load(background)
                        .placeholder(R.drawable.thumbnail_placeholder)
                        .into(subscriptionTtemBg);

                LinearLayout GATAC = findViewById(R.id.GATAC);
                LinearLayout RAA = findViewById(R.id.RAA);
                LinearLayout WPC = findViewById(R.id.WPC);
                LinearLayout DPC = findViewById(R.id.DPC);


                String number = String.valueOf(subscriptionType);
                for(int i = 0; i < number.length(); i++) {
                    int userSubType = Character.digit(number.charAt(i), 10);
                    if(userSubType == 1) {
                        RAA.setVisibility(View.VISIBLE);
                    } else if(userSubType == 2) {
                        GATAC.setVisibility(View.VISIBLE);
                        WPC.setVisibility(View.VISIBLE);
                    } else if(userSubType == 3) {
                        DPC.setVisibility(View.VISIBLE);
                    } else {
                        GATAC.setVisibility(View.GONE);
                        RAA.setVisibility(View.GONE);
                        WPC.setVisibility(View.GONE);
                        DPC.setVisibility(View.GONE);
                    }
                }
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

    @Override
    public void onBackPressed() {
        if(payment_dialog.getVisibility() == View.VISIBLE) {
            paymentDialog(false);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(SubscriptionDetails.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(SubscriptionDetails.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}
package com.wwwpromoter.android_sdk;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;

public class WWWPromoterConnection {

     String finalUrl = "http://bid.las01.wwwpromoter.com/search/";
    HandleXML obj;
    public static String PACKAGE_NAME;
    String applicationNames;
    PackageManager pm;
    PackageManager pms;
    ApplicationInfo ai;
    String url;
    String userAgent, myApiKey;
    SessionManager sessionManager;
    HashMap<String, String> user;

    public void feedId(Context context, String FeedID) {
        String id = FeedID;
        sessionManager = new SessionManager(context);
        Log.e("FeedId", id);
        sessionManager.CreateUserId(id);
    }

    public String getAdvertisement(final Context context) {

        pm = context.getApplicationContext().getPackageManager();
        try {
            ai = pm.getApplicationInfo(context.getPackageName(), 0);

        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        applicationNames = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

        PACKAGE_NAME = context.getApplicationContext().getPackageName();

        userAgent = WebSettings.getDefaultUserAgent(context.getApplicationContext());

        sessionManager = new SessionManager(context);
        user = sessionManager.getUserID();

        if (user.get("user_id") == null) {

        } else {
            myApiKey = user.get("user_id");
        }
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        finalUrl = finalUrl + myApiKey + "?subid=" + applicationNames + "&url=" + PACKAGE_NAME +
                                "&ua=" + URLEncoder.encode(userAgent, "UTF-8");
                        Log.e("finalUrl", finalUrl);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    URL urls = new URL(finalUrl);
                    HttpURLConnection connection = (HttpURLConnection) urls.openConnection();
                    connection.setReadTimeout(10000 /* milliseconds */);
                    connection.setConnectTimeout(15000 /* milliseconds */);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream stream = connection.getInputStream();
                    obj = new HandleXML(finalUrl);
                    obj.fetchXML();
                    while (obj.parsingComplete) ;
                    url = obj.getRedirecturl();
                    Log.e("url", url);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);

                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return finalUrl;
    }

}
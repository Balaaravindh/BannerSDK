package com.wwwpromoter.android_sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.IDNA;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class WWWPromoterBannerView {

    public static String PACKAGE_NAME;
    public static int NO_OPTIONS = 0;
    String userAgent;
    PackageManager pm;
    ApplicationInfo ai;
    String applicationNames;
    //int feed_id = 22513;
    int verCode;
    int height, width;
    String html_adm, nurl, price;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    int connectiontype;
    private String SHAHash;
    private WebView mWebView;
    SessionManager sessionManager;
    HashMap<String, String> user;
    String securepassword;


    public static final int RequestPermissionCode = 1;

    ////Get Mobile Make Name
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;

        if (manufacturer.startsWith(manufacturer)) {
            return capitalize(manufacturer);
        }
        return capitalize(manufacturer);
    }

    ////Get Mobile Model Name
    public static String getDeviceModel() {
        String model = Build.MODEL;

        if (model.startsWith(model)) {
            return capitalize(model);
        }
        return capitalize(model);
    }

    ////Get Mobile OS
    public static String getDeviceOS() {
        String os = null;
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            os = fieldName;
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        if (os.startsWith(os)) {
            return capitalize(os);
        }
        return capitalize(os);
    }

    ////Get Mobile OS Version
    public static String getDeviceOSVersion() {
        String osv = Build.VERSION.RELEASE;

        if (osv.startsWith(osv)) {
            return capitalize(osv);
        }
        return capitalize(osv);
    }

    private String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public void data_get(final Context context, final BannerView web, String FeedID, int height, int width) {

        sessionManager = new SessionManager(context);
        sessionManager.CreateUserId(FeedID);

        mWebView = new WebView(context.getApplicationContext());
        web.addView(mWebView);

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                connectiontype = 2;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                connectiontype = 3;
            }
        }

        pm = context.getApplicationContext().getPackageManager();
        try {
            ai = pm.getApplicationInfo(context.getPackageName(), 0);

        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        applicationNames = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        verCode = pInfo.versionCode;

        PACKAGE_NAME = context.getApplicationContext().getPackageName();

        userAgent = WebSettings.getDefaultUserAgent(context.getApplicationContext());

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String Device_id = String.valueOf(telephonyManager.getDeviceId());

        //MD5
        securepassword = md5(String.valueOf(Device_id));

        try {
            SHAHash = SHA1(String.valueOf(Device_id));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);

        String URL = "http://172.93.5.66/rtb-bid/" + FeedID;
        Log.e("URL", URL);

        int aa = 0;
        int b = 1;
        JSONObject main_object = new JSONObject();
        JSONObject device_objects_inside_details = new JSONObject();
        JSONObject object = new JSONObject();
        final JSONObject banner_inside_datas = new JSONObject();

        JSONObject imp_inside_datas = new JSONObject();
        JSONArray imp_datas = new JSONArray();

        //App information
        try {
            object.put("name", applicationNames);
            object.put("ver", verCode);
            object.put("bundle", PACKAGE_NAME);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //IMP information Banner Height and Width
        try {
            imp_inside_datas.put("id", 1);

            banner_inside_datas.put("h", height);
            banner_inside_datas.put("w", width);

            imp_inside_datas.put("banner", banner_inside_datas);

            imp_datas.put(imp_inside_datas);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Device Information
        try {

            device_objects_inside_details.put("dnt", aa);
            device_objects_inside_details.put("ua", userAgent);
            device_objects_inside_details.put("make", getDeviceName());
            device_objects_inside_details.put("model", getDeviceModel());
            device_objects_inside_details.put("os", getDeviceOS());
            device_objects_inside_details.put("osv", getDeviceOSVersion());
            device_objects_inside_details.put("language", Locale.getDefault().getDisplayLanguage());
            device_objects_inside_details.put("dpidmd5", securepassword);
            device_objects_inside_details.put("dpidsha1", SHAHash);
            device_objects_inside_details.put("connectiontype", connectiontype);
            device_objects_inside_details.put("devicetype", 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            main_object.put("app", object);
            main_object.put("at", b);
            main_object.put("id", FeedID);
            main_object.put("imp", imp_datas);
            main_object.put("device", device_objects_inside_details);

            Log.e("main_object", main_object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL, main_object, new Response.Listener<JSONObject>() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, URL, main_object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("html_adm", response.toString());

                JSONArray jsonArray = null;
                JSONArray jsonArray1 = null;

                try {
                    jsonArray = response.getJSONArray("seatbid");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = (JSONObject) jsonArray.get(i);
                            jsonArray1 = jsonobject.getJSONArray("bid");

                            JSONObject jsonobject1 = null;
                            for (int k = 0; k < jsonArray1.length(); k++) {
                                jsonobject1 = (JSONObject) jsonArray1.get(k);
                                html_adm = jsonobject1.optString("adm");
                                nurl = jsonobject1.optString("nurl");
                                price = jsonobject1.optString("price");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    Log.e("html_adm", html_adm);
                    mWebView.loadData(html_adm, "text/html", null);
                    nurl = nurl.replace("${AUCTION_PRICE}", "${" + price + "}");

                  /*  web.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mWebView.setWebViewClient(new WebViewClient() {
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    url = nurl;
                                    if (url != null && url.startsWith("http://")) {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            });
                        }
                    });*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("Error", error.toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(jsObjRequest);
    }
}

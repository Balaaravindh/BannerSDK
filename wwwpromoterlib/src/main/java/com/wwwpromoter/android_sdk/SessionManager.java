package com.wwwpromoter.android_sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.HashMap;

public class SessionManager {
    public static final String KEY_ID = "user_id";

    private static final String PREF_NAME = "AndroidHivePref";
    private static final String IS_USERID = "IsLoggedIn";
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    HashMap<String, String> user;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void CreateUserId(String id) {
        editor.putBoolean(IS_USERID, true);

        editor.putString(KEY_ID, id);

        editor.commit();
    }


    public HashMap<String, String> getUserID() {

        user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        return user;
    }

    public void clear_user() {
        editor.clear();
        editor.commit();

    }

}

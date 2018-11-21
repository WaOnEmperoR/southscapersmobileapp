package id.co.reich.mockupsouthscape.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import id.co.reich.mockupsouthscape.LoginActivity;

public class SessionManager {
    SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private Context mContext;
    private int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(Utils.APP_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String userid, String username, String email, String gender, String birth_date, String address, String image_avatar) {
        editor.putBoolean(Utils.IS_LOGIN, true);
        editor.putString(Utils.KEY_USER_ID, userid );
        editor.putString(Utils.KEY_USERNAME, username);
        editor.putString(Utils.KEY_EMAIL, email);
        editor.putString(Utils.KEY_GENDER, gender);
        editor.putString(Utils.KEY_BIRTH_DATE, birth_date);
        editor.putString(Utils.KEY_ADDRESS, address);
        editor.putString(Utils.KEY_IMAGE_AVATAR, image_avatar);
        editor.commit();
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent i = new Intent(mContext, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(Utils.KEY_USER_ID, pref.getString(Utils.KEY_USER_ID, null));
        user.put(Utils.KEY_USERNAME, pref.getString(Utils.KEY_USERNAME, null));
        user.put(Utils.KEY_EMAIL, pref.getString(Utils.KEY_EMAIL, null));
        user.put(Utils.KEY_GENDER, pref.getString(Utils.KEY_GENDER, null));
        user.put(Utils.KEY_BIRTH_DATE, pref.getString(Utils.KEY_BIRTH_DATE, null));
        user.put(Utils.KEY_ADDRESS, pref.getString(Utils.KEY_ADDRESS, null));
        user.put(Utils.KEY_IMAGE_AVATAR, pref.getString(Utils.KEY_IMAGE_AVATAR, null));
        return user;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Utils.IS_LOGIN, false);
    }

    public Context getContext(){
        return this.mContext;
    }
}

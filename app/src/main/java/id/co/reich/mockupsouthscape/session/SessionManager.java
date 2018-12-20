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
        pref = mContext.getSharedPreferences(Constants.APP_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String userid, String username, String email, String gender, String birth_date, String address, String image_avatar) {
        editor.putBoolean(Constants.IS_LOGIN, true);
        editor.putString(Constants.KEY_USER_ID, userid );
        editor.putString(Constants.KEY_USERNAME, username);
        editor.putString(Constants.KEY_EMAIL, email);
        editor.putString(Constants.KEY_GENDER, gender);
        editor.putString(Constants.KEY_BIRTH_DATE, birth_date);
        editor.putString(Constants.KEY_ADDRESS, address);
        editor.putString(Constants.KEY_IMAGE_AVATAR, image_avatar);
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
        user.put(Constants.KEY_USER_ID, pref.getString(Constants.KEY_USER_ID, null));
        user.put(Constants.KEY_USERNAME, pref.getString(Constants.KEY_USERNAME, null));
        user.put(Constants.KEY_EMAIL, pref.getString(Constants.KEY_EMAIL, null));
        user.put(Constants.KEY_GENDER, pref.getString(Constants.KEY_GENDER, null));
        user.put(Constants.KEY_BIRTH_DATE, pref.getString(Constants.KEY_BIRTH_DATE, null));
        user.put(Constants.KEY_ADDRESS, pref.getString(Constants.KEY_ADDRESS, null));
        user.put(Constants.KEY_IMAGE_AVATAR, pref.getString(Constants.KEY_IMAGE_AVATAR, null));
        return user;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constants.IS_LOGIN, false);
    }

    public Context getContext(){
        return this.mContext;
    }
}

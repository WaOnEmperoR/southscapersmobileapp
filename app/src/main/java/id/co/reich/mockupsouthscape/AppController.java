package id.co.reich.mockupsouthscape;

import android.app.Application;
import android.app.ProgressDialog;
import android.util.TypedValue;
import android.widget.Toast;

import id.co.reich.mockupsouthscape.session.SessionManager;

public class AppController extends Application {
    private ProgressDialog mProgressDialog = null;
    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private SessionManager session;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public SessionManager getSession() {
        if (session == null) {
            session = new SessionManager(this);
        }
        return session;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}

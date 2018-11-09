package id.co.reich.mockupsouthscape.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class AuthenticatorService extends Service {
    private Authenticator authenticator;

    public AuthenticatorService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            ret = getAuthenticator().getIBinder();
        return ret;
    }

    private Authenticator getAuthenticator() {
        if (authenticator == null)
            authenticator = new Authenticator(this);
        return authenticator;
    }
}

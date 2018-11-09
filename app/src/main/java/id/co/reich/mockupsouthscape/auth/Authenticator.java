package id.co.reich.mockupsouthscape.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class Authenticator extends AbstractAccountAuthenticator {
    Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s1, String[] strings, Bundle bundle) throws NetworkErrorException {
        AccountManager am = AccountManager.get(mContext);

        if (ActivityCompat.checkSelfPermission(mContext,
                android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // Checking to see if you can have a look at accounts present on the device.
            Log.d("Authenticator", "GET_ACCOUNTS not present.");
        }

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);

        // This key can be anything. Try to use your domain/package
        intent.putExtra("id.co.reich.mockupsouthscape", s);

        // This key can be anything too. It's just a way of identifying the token's type (used when there are multiple permissions)
        intent.putExtra("full_access", s1);

        // This key can be anything too. Used for your reference. Can skip it too.
        intent.putExtra("is_adding_new_account", true);

        // Copy this exactly from the line below.
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);

        final Bundle mBundle = new Bundle();
        mBundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return mBundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }
}

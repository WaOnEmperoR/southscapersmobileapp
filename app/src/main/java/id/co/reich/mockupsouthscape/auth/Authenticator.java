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
import android.text.TextUtils;
import android.util.Log;

import id.co.reich.mockupsouthscape.LoginActivity;
import id.co.reich.mockupsouthscape.R;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static id.co.reich.mockupsouthscape.auth.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import static id.co.reich.mockupsouthscape.auth.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
import static id.co.reich.mockupsouthscape.auth.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY;
import static id.co.reich.mockupsouthscape.auth.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;

public class Authenticator extends AbstractAccountAuthenticator {
    Context mContext;

    private String APP_NAME;
    private String TAG = this.getClass().getSimpleName();

    public Authenticator(Context context) {
        super(context);
        mContext = context;
        APP_NAME = mContext.getString(R.string.app_name);
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

        final Intent intent = new Intent(mContext, LoginActivity.class);

        // This key can be anything. Try to use your domain/package
        intent.putExtra("id.co.reich.mockupsouthscape", s);

        // This key can be anything too. It's just a way of identifying the token's type (used when there are multiple permissions)
        intent.putExtra("user_access", s1);

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
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        String authToken = am.peekAuthToken(account, authTokenType);

        Log.d(APP_NAME, TAG + ">authToken : " + authToken);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if (password != null) {
                try {
                    Log.d(APP_NAME, TAG + "> re-authenticating with the existing password");
                    //authToken = sServerAuthenticate.userSignIn(account.name, password, authTokenType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, accountAuthenticatorResponse);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, account.name);

        final Bundle mBundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return mBundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }
}

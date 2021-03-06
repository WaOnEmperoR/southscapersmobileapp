package id.co.reich.mockupsouthscape;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.co.reich.mockupsouthscape.pojo.UserDetail;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static android.Manifest.permission.ACCOUNT_MANAGER;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.READ_SYNC_STATS;
import static android.Manifest.permission.WRITE_SYNC_SETTINGS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity GET_ACCOUNTS permission request.
     */
    private static final int REQUEST_PERMISSIONS = 0;
    private static final int REQUEST_CODE_EMAIL = 1;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ApiInterface mApiService;
    private String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String accountName;

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String PARAM_USER_PASS = "USER_PASS";

    private CompositeDisposable disposable = new CompositeDisposable();
    private Unbinder unbinder;

    private AppController app() {
        return AppController.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        populateAutoComplete();

        unbinder = ButterKnife.bind(this);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mCheckUserButton = findViewById(R.id.check_account);
        mCheckUserButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Enter");
                getAuthTokenUser(mEmailView.getText().toString());
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAccountManager = AccountManager.get(getBaseContext());

        // If this is a first time adding, then this will be null
        accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        if (mAuthTokenType == null)
            mAuthTokenType = getString(R.string.auth_type);

        findAccount(accountName);
   }

    private void populateAutoComplete() {
        if (!mayRequestPermissions()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(ACCOUNT_MANAGER) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_SYNC_STATS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(WRITE_SYNC_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(GET_ACCOUNTS)) {
            // TODO: alert the user with a Snackbar/AlertDialog giving them the permission rationale
            // To use the Snackbar from the design support library, ensure that the activity extends
            // AppCompatActivity and uses the Theme.AppCompat theme.
        } else {
            requestPermissions(new String[]{GET_ACCOUNTS, ACCOUNT_MANAGER, READ_SYNC_STATS, WRITE_SYNC_SETTINGS}, REQUEST_PERMISSIONS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);

            Observable<UserDetail> loginObservable = RXLoginTask(email, password);

            disposable.add(
                loginObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<UserDetail>() {
                            @Override
                            public void onNext(UserDetail userDetail) {
                                showProgress(false);

                                Log.d(TAG, userDetail.getEmail());
                                Log.d(TAG, userDetail.getName());
                                Log.d(TAG, userDetail.getAddress());
                                Log.d(TAG, userDetail.getBirth_date());
                                Log.d(TAG, userDetail.getGender());
                                Log.d(TAG, String.valueOf(userDetail.getUserid()));

                                app().getSession().createLoginSession(
                                        String.valueOf(userDetail.getUserid()),
                                        String.valueOf(userDetail.getName()),
                                        String.valueOf(userDetail.getEmail()),
                                        String.valueOf(userDetail.getGender()),
                                        String.valueOf(userDetail.getBirth_date()),
                                        String.valueOf(userDetail.getAddress()),
                                        String.valueOf(userDetail.getImg_avatar())
                                );

                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                showProgress(false);

                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();

                                Log.e(TAG, e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                showProgress(false);
                                Log.d(TAG, "onComplete from rxjava");
                            }
                    })
            );
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        unbinder.unbind();
    }

    private void userSignIn(String authToken, String accountName, String password)
    {
        if (accountName.length() > 0)
        {
            Bundle data = new Bundle();
            data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAuthTokenType);
            data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            data.putString(PARAM_USER_PASS, password);

            Bundle userData = new Bundle();
            data.putBundle(AccountManager.KEY_USERDATA, userData);

            //Make it an intent to be passed back to the Android Authenticator
            final Intent res = new Intent();
            res.putExtras(data);

            //Create the new account with Account Name and TYPE
            final Account account = new Account(accountName, mAuthTokenType);

            //Add the account to the Android System
            if (mAccountManager.addAccountExplicitly(account, password, userData)) {
                // worked
                Log.d(TAG, "Account added");
                mAccountManager.setAuthToken(account, mAuthTokenType, authToken);
                setAccountAuthenticatorResult(data);
                setResult(RESULT_OK, res);
            } else {
                // guess not
                Log.d(TAG, "Account NOT added");
            }
        }
    }

    public Account findAccount(String accountName) {
        for (Account account : mAccountManager.getAccounts()){
            Log.d(TAG, "Account : " + account.name + "--" + account.type);
            if (TextUtils.equals(account.name, accountName) && TextUtils.equals(account.type, mAuthTokenType)) {
                Log.d(TAG, "FOUND");
                return account;
            }
        }
        return null;
    }

    public void getAuthTokenUser(String userAccount)
    {
        final Account acc = findAccount(userAccount);

        if (acc!=null)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String token;
                        token = mAccountManager.blockingGetAuthToken(acc, mAuthTokenType, true);
                        Log.d(TAG, "UserToken >" + token);
                    } catch (OperationCanceledException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (AuthenticatorException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }).start();

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Log.d(TAG, "AccountName> " + accountName);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (app().getSession().isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private Observable<UserDetail> RXLoginTask(final String mEmail, final String mPassword)
    {
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        return mApiService.RxLogin(mEmail, mPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) throws Exception {
                        JSONObject jsonRESULTS = new JSONObject(responseBody.string());
                        String token = jsonRESULTS.get("token").toString();
                        Log.d(TAG, "token from RxJava = " + token);
                        userSignIn(token, mEmail, mPassword);

                        return jsonRESULTS.get("token").toString();
                    }
                })
                .flatMap(new Function<String, Observable<UserDetail>>() {
                    @Override
                    public Observable<UserDetail> apply(String authToken) throws Exception {
                        return mApiService.RxUserDetails("application/json", "Bearer " + authToken)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                });
    }

}


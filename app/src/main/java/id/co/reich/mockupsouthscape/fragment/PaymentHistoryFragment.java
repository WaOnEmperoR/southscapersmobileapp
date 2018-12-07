package id.co.reich.mockupsouthscape.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import id.co.reich.mockupsouthscape.AppController;
import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Payment;
import id.co.reich.mockupsouthscape.pojo.PaymentList;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import id.co.reich.mockupsouthscape.session.Utils;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private InfinitePlaceHolderView mLoadMoreView;
    private AccountManager mAccountManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String TAG = this.getClass().getSimpleName();
    private String mAuthTokenType;
    private ApiInterface mApiService;

    public PaymentHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PaymentHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentHistoryFragment newInstance() {
        PaymentHistoryFragment fragment = new PaymentHistoryFragment();
        return fragment;
    }

    private AppController app() {
        return AppController.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);

        mAuthTokenType = getString(R.string.auth_type);

        HashMap<String, String> hashMap = app().getSession().getUserDetails();
        String user_email = hashMap.get(Utils.KEY_EMAIL);

        Account account = findAccount(user_email);
        if (account!=null)
        {
            PaymentHistoryTask pht = new PaymentHistoryTask(account);
            pht.execute();
//            String mPassword = mAccountManager.getPassword(account);
//            Log.d(TAG, "Password : " + mPassword);
        }

        return view;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class PaymentHistoryTask extends AsyncTask<Void, Void, Boolean>
    {
        private Account mAccount;

        PaymentHistoryTask(Account account) {
            mAccount = account;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String mEmail = mAccount.name;
            Log.d(TAG, "Email : " + mEmail);
            String mPassword = mAccountManager.getPassword(mAccount);
            Log.d(TAG, "Password : " + mPassword);

            String token;
            try {
                token = mAccountManager.blockingGetAuthToken(mAccount, mAuthTokenType, true);
                Log.d(TAG, "Token : " + token);

                // synchronous Retrofit API calling inside asynchronous block
                mApiService = ApiClient.getClient().create(ApiInterface.class);
                Response<PaymentList> response = mApiService.getPayments("application/json", "Bearer " + token, 0, 5).execute();

                if (response.isSuccessful())
                {
                    ArrayList<Payment> paymentArrayList = response.body().getPaymentArrayList();

                    return true;
                }
                else
                {
                    // Failure to get response can be caused by expired auth token, so need to obtain new auth token
                    return false;
                }
            } catch (OperationCanceledException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (AuthenticatorException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success)
            {

            }
            else
            {
                UserDetailTask udt = new UserDetailTask(mAccount.name, mAccountManager.getPassword(mAccount));
                udt.execute();
            }
        }
    }

    public class UserDetailTask extends AsyncTask<Void, Void, Boolean>
    {
        private String mEmail;
        private String mPassword;

        public UserDetailTask(String email, String password)
        {
            this.mEmail = email;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }
    }
}

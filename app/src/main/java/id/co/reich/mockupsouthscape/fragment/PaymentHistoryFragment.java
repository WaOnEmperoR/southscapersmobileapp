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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;

import id.co.reich.mockupsouthscape.AppController;
import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Payment;
import id.co.reich.mockupsouthscape.pojo.PaymentList;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import id.co.reich.mockupsouthscape.session.Utils;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
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

        mAccountManager = AccountManager.get(getActivity());
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
            String mPassword = mAccountManager.getPassword(account);
            Log.d(TAG, "Password : " + mPassword);

        }

        io.reactivex.Observable<String> tokenObservable = getAuthToken(account, mAuthTokenType, true);

        tokenObservable
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onDispose");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "Token = " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
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

    private io.reactivex.Observable<String> getAuthToken(final Account account, final String auth, final boolean status)
    {
        return io.reactivex.Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return mAccountManager.blockingGetAuthToken(account, auth, status);
            }
        });
    }

    private void TestObservable(final String accept, String auth, final String email, final String password)
    {
        final ApiInterface mApiService = ApiClient.getClient().create(ApiInterface.class);
        mApiService.RxGetPayments(accept, auth, 0, 10)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Payment>, ObservableSource<Payment>>() {
                    @Override
                    public ObservableSource<Payment> apply(List<Payment> payments) throws Exception {
                        return io.reactivex.Observable.fromIterable(payments);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<Payment>>() {
                    @Override
                    public ObservableSource<Payment> apply(Throwable throwable) throws Exception {
                        if (!(throwable instanceof HttpException))
                        {
                            return io.reactivex.Observable.error(throwable);
                        }
                        else
                        {
                            return mApiService.RxLogin(email, password)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .map(new Function<ResponseBody, String>() {
                                        @Override
                                        public String apply(ResponseBody responseBody) throws Exception {
                                            JSONObject jsonRESULTS = new JSONObject(responseBody.string());
                                            String token = jsonRESULTS.get("token").toString();
                                            Log.d(TAG, "token from RxJava = " + token);

                                            return jsonRESULTS.get("token").toString();
                                        }
                                    })
                                    .flatMap(new Function<String, ObservableSource<Payment>>() {
                                        @Override
                                        public ObservableSource<Payment> apply(String s) throws Exception {
                                            return mApiService.RxGetPayments(accept, s, 0, 10)
                                                    .toObservable()
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .flatMap(new Function<List<Payment>, ObservableSource<Payment>>() {
                                                        @Override
                                                        public ObservableSource<Payment> apply(List<Payment> payments) throws Exception {
                                                            return io.reactivex.Observable.fromIterable(payments);
                                                        }
                                                    });

                                        }
                                    });
                        }

                    }
                })
                .subscribeWith(new Observer<Payment>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Payment payment) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })

        ;
    }
}

package id.co.reich.mockupsouthscape.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindorks.placeholderview.InfinitePlaceHolderView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.co.reich.mockupsouthscape.AppController;
import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Payment;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import id.co.reich.mockupsouthscape.session.Constants;
import id.co.reich.mockupsouthscape.view.ItemViewPayment;
import id.co.reich.mockupsouthscape.view.LoadMoreViewPayment;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private InfinitePlaceHolderView mLoadMoreView;
    private AccountManager mAccountManager;

    private CompositeDisposable disposable = new CompositeDisposable();
    private Unbinder unbinder;
    private View mProgressView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private final String TAG = this.getClass().getSimpleName();
    private String mAuthTokenType;

    public PaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
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
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        mLoadMoreView = view.findViewById(R.id.loadMore_Payment);
        mProgressView = view.findViewById(R.id.payment_progress);

        unbinder = ButterKnife.bind(this.getActivity());

        mAuthTokenType = getString(R.string.auth_type);

        HashMap<String, String> hashMap = app().getSession().getUserDetails();
        String user_email = hashMap.get(Constants.KEY_EMAIL);

        Account account = findAccount(user_email);
        if (account!=null)
        {
            String mPassword = mAccountManager.getPassword(account);
            Log.d(TAG, "Password : " + mPassword);

            SetupView(account, 0, LoadMoreViewPayment.LOAD_VIEW_SET_COUNT, user_email, mPassword);
        }

        Log.d(this.getClass().getSimpleName(), "onCreateView");

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

    private void SetupView(Account account, int begin, int end, String email, String password)
    {
        Log.d(this.getClass().getSimpleName(), "SetupView");
        mProgressView.setVisibility(View.VISIBLE);

        final ArrayList<Payment> arrayListPayment = new ArrayList<>();

        io.reactivex.Observable<List<Payment>> paymentListObservable = getPaymentList(account, begin, end, email, password);

        disposable.add(
                paymentListObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Function<List<Payment>, ObservableSource<Payment>>() {
                        @Override
                        public ObservableSource<Payment> apply(List<Payment> payments) throws Exception {
                            return io.reactivex.Observable.fromIterable(payments);
                        }
                    })
                    .subscribeWith(new DisposableObserver<Payment>() {
                        @Override
                        public void onNext(Payment payment) {
                            Log.d(TAG, payment.getPaymentSubmitted());
                            Log.d(TAG, payment.getPaymentType());
                            Log.d(TAG, payment.getPaymentSession());
                            arrayListPayment.add(payment);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete from Setup View");
                            mProgressView.setVisibility(View.GONE);

                            for (int i=0; i<arrayListPayment.size(); i++)
                            {
                                mLoadMoreView.addView(new ItemViewPayment(getActivity(), arrayListPayment.get(i)));
                            }

                            this.dispose();
                        }
                    })
        );

        mLoadMoreView.setLoadMoreResolver(new LoadMoreViewPayment(mLoadMoreView));
    }

    private io.reactivex.Observable<List<Payment>> getPaymentList(Account account, final int begin, final int end, final String email, final String password)
    {
        final ApiInterface mApiService = ApiClient.getClient().create(ApiInterface.class);

        io.reactivex.Observable<String> tokenObservable = getAuthToken(account, mAuthTokenType, true);

        return tokenObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String, ObservableSource<List<Payment>>>() {
                    @Override
                    public ObservableSource<List<Payment>> apply(String s) throws Exception {
                        return mApiService.RxGetPayments("application/json", "Bearer " + s, begin, end)
                                .toObservable()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .onErrorResumeNext(new Function<Throwable, ObservableSource<List<Payment>>>() {
                    @Override
                    public ObservableSource<List<Payment>> apply(Throwable throwable) throws Exception {
                        if (throwable.getMessage().equals("HTTP 401 "))
                        {
                            Log.d(TAG, "HTTP 401 Error Unauthorized : " + throwable.getMessage());
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
                                    .flatMap(new Function<String, ObservableSource<List<Payment>>>() {
                                        @Override
                                        public ObservableSource<List<Payment>> apply(String s) throws Exception {
                                            return mApiService.RxGetPayments("application/json", "Bearer " + s, begin, end)
                                                    .toObservable()
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread());
                                        }
                                    });
                        }
                        else
                        {
                            return io.reactivex.Observable.error(throwable);
                        }
                    }
                });
    }


}

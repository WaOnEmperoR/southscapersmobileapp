package id.co.reich.mockupsouthscape.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import id.co.reich.mockupsouthscape.AppController;
import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.Payment;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import id.co.reich.mockupsouthscape.session.Constants;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

@Layout(R.layout.load_more_view)
public class LoadMoreViewPayment {
    public static final int LOAD_VIEW_SET_COUNT = 3;

    private InfinitePlaceHolderView mLoadMoreView;
    private CompositeDisposable disposable = new CompositeDisposable();
    private final String TAG = this.getClass().getSimpleName();
    private String mAuthTokenType;
    private AccountManager mAccountManager;
    private String mPassword;

    public LoadMoreViewPayment(InfinitePlaceHolderView loadMoreView) {
        this.mLoadMoreView = loadMoreView;
        mAuthTokenType = "id.co.reich.auth_southscapers";

    }

    @LoadMore
    private void onLoadMore(){
        Log.d(TAG,"onLoadMore");
        mAccountManager = AccountManager.get(app().getApplicationContext());
        new ForcedWaitedLoading();
    }

    private AppController app() {
        return AppController.getInstance();
    }

    class ForcedWaitedLoading implements Runnable{
        public ForcedWaitedLoading() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            try {
                Thread.currentThread().sleep(1500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(new Runnable(){
                @Override
                public void run() {
                    int count = mLoadMoreView.getViewCount();
                    Log.d(TAG, "count : " + count);

                    HashMap<String, String> hashMap = app().getSession().getUserDetails();
                    String user_email = hashMap.get(Constants.KEY_EMAIL);

                    Account account = findAccount(user_email);
                    if (account!=null)
                    {
                        mPassword = mAccountManager.getPassword(account);
                        Log.d(TAG, "Password : " + mPassword);

                        final ArrayList<Payment> arrayListPayment = new ArrayList<>();

                        io.reactivex.Observable<List<Payment>> paymentListObservable = getPaymentList(account, count, LoadMoreViewPayment.LOAD_VIEW_SET_COUNT, user_email, mPassword);

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
                                                arrayListPayment.add(payment);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e(TAG, e.getMessage());
                                            }

                                            @Override
                                            public void onComplete() {
                                                Log.d(TAG, "onComplete from Load More View");
                                                Log.d(TAG, "Array List Size : " + arrayListPayment.size());
                                                if (arrayListPayment.size()==0)
                                                {
                                                    mLoadMoreView.noMoreToLoad();
                                                    this.dispose();
                                                }
                                                else
                                                {
                                                    for (int i=0; i<arrayListPayment.size(); i++)
                                                    {
                                                        mLoadMoreView.addView(new ItemViewPayment(mLoadMoreView.getContext(), arrayListPayment.get(i)));
                                                    }
                                                }

                                                mLoadMoreView.loadingDone();

                                                this.dispose();
                                            }
                                        })
                        );
                    }


                }
            });
        }
    }

    private io.reactivex.Observable<List<Payment>> getPaymentList(final Account account, final int begin, final int end, final String email, final String password)
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
                                            mAccountManager.setAuthToken(account, mAuthTokenType, token);

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

    private io.reactivex.Observable<String> getAuthToken(final Account account, final String auth, final boolean status)
    {
        return io.reactivex.Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return mAccountManager.blockingGetAuthToken(account, auth, status);
            }
        });
    }

    public Account findAccount(String accountName) {
        for (Account account : mAccountManager.getAccounts()){
            if (TextUtils.equals(account.name, accountName) && TextUtils.equals(account.type, mAuthTokenType)) {
                Log.d(TAG, "FOUND");
                return account;
            }
        }
        return null;
    }
}

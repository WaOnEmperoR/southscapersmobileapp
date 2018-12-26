package id.co.reich.mockupsouthscape.view;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;

import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.pojo.EventList;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@Layout(R.layout.load_more_view)
public class LoadMoreViewEvent {
    public static final int LOAD_VIEW_SET_COUNT = 3;

    private InfinitePlaceHolderView mLoadMoreView;
    private int mChoice;
    private CompositeDisposable disposable = new CompositeDisposable();

    public LoadMoreViewEvent(InfinitePlaceHolderView loadMoreView, int choice) {
        this.mLoadMoreView = loadMoreView;
        this.mChoice = choice;
    }

    @LoadMore
    private void onLoadMore(){
        Log.d("DEBUG", "onLoadMore");
        new ForcedWaitedLoading();
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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int count = mLoadMoreView.getViewCount();
                    Log.d("DEBUG", "count " + count);

                    Observable<EventList> eventsAheadObservable = getEventAheadList(count);

                    disposable.add(
                            eventsAheadObservable
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableObserver<EventList>() {
                                        @Override
                                        public void onNext(EventList eventList) {
                                            Log.d(this.getClass().getSimpleName(), "Response Successful");
                                            Log.d(this.getClass().getSimpleName(), "ArrayListSize" + eventList.getEventArrayList().size());

                                            if (eventList.getEventArrayList().size()==0)
                                            {
                                                mLoadMoreView.noMoreToLoad();
                                                Log.d(this.getClass().getSimpleName(), "No More Load");
                                                this.dispose();
                                            }

                                            for (int i=0; i<eventList.getEventArrayList().size(); i++)
                                            {
                                                mLoadMoreView.addView(new ItemViewEvent(mLoadMoreView.getContext(), eventList.getEventArrayList().get(i)));
                                            }
                                            mLoadMoreView.loadingDone();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e(this.getClass().getSimpleName(), e.getMessage());
                                        }

                                        @Override
                                        public void onComplete() {
                                            Log.e(this.getClass().getSimpleName(), "onComplete from RXJava");
                                            this.dispose();
                                        }
                                    })
                    );

                }
            });
        }
    }

    private Observable<EventList> getEventAheadList(int start)
    {
        final ApiInterface mApiService = ApiClient.getClient().create(ApiInterface.class);
        return mApiService.RxGetEvents(start, LoadMoreViewEvent.LOAD_VIEW_SET_COUNT, mChoice)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

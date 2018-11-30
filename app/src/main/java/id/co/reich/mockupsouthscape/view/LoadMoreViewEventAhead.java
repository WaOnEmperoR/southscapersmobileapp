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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Layout(R.layout.load_more_view)
public class LoadMoreViewEventAhead {
    public static final int LOAD_VIEW_SET_COUNT = 3;

    private InfinitePlaceHolderView mLoadMoreView;
    private ApiInterface mApiService;

    public LoadMoreViewEventAhead(InfinitePlaceHolderView loadMoreView) {
        this.mLoadMoreView = loadMoreView;
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

                    mApiService = ApiClient.getClient().create(ApiInterface.class);

                    Call<EventList> call = mApiService.getEvents(count, LoadMoreViewEventAhead.LOAD_VIEW_SET_COUNT);
                    call.enqueue(new Callback<EventList>() {
                        @Override
                        public void onResponse(Call<EventList> call, Response<EventList> response) {
                            if (response.isSuccessful())
                            {
                                Log.d(this.getClass().getSimpleName(), "Response Successful");

                                if (response.body().getEventArrayList().size()==0)
                                {
                                    mLoadMoreView.noMoreToLoad();
                                }

                                for (int i=0; i<response.body().getEventArrayList().size(); i++)
                                {
                                    mLoadMoreView.addView(new ItemViewEventAhead(mLoadMoreView.getContext(), response.body().getEventArrayList().get(i)));
                                }
                                mLoadMoreView.loadingDone();
                            }
                        }

                        @Override
                        public void onFailure(Call<EventList> call, Throwable t) {
                            Log.e(this.getClass().getSimpleName(), t.getMessage());
                        }
                    });

                }
            });
        }
    }

}

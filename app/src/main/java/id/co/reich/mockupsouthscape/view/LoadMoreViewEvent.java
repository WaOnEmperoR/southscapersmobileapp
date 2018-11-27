package id.co.reich.mockupsouthscape.view;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;

import java.io.IOException;
import java.util.List;

import id.co.reich.mockupsouthscape.R;
import id.co.reich.mockupsouthscape.model.InfiniteFeedInfo;
import id.co.reich.mockupsouthscape.pojo.EventList;
import id.co.reich.mockupsouthscape.rest.ApiClient;
import id.co.reich.mockupsouthscape.rest.ApiInterface;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Layout(R.layout.load_more_view)
public class LoadMoreViewEvent {
    public static final int LOAD_VIEW_SET_COUNT = 1;

    private InfinitePlaceHolderView mLoadMoreView;
    private ApiInterface mApiService;

    public LoadMoreViewEvent(InfinitePlaceHolderView loadMoreView) {
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
                    try {
                        Response<EventList> response = mApiService.getEvents(count-1, LOAD_VIEW_SET_COUNT).execute();
                        if (response.isSuccessful())
                        {
                            for (int i=0; i<response.body().getEventArrayList().size(); i++)
                            {
                                mLoadMoreView.addView(new ItemViewEvent(mLoadMoreView.getContext(), response.body().getEventArrayList().get(i)));
                            }
                        }
                    } catch (IOException e) {
                        Log.e("LoadMore", e.getMessage());
                    }

//                    for (int i = count - 1;
//                         i < (count - 1 + LoadMoreView.LOAD_VIEW_SET_COUNT) && mFeedList.size() > i;
//                         i++) {
//                        mLoadMoreView.addView(new ItemView(mLoadMoreView.getContext(), mFeedList.get(i)));
//
//                        if(i == mFeedList.size() - 1){
//                            mLoadMoreView.noMoreToLoad();
//                            break;
//                        }
//                    }
                    mLoadMoreView.loadingDone();
                }
            });
        }
    }

}

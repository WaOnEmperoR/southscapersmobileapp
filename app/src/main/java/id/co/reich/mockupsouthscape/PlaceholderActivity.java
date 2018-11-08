package id.co.reich.mockupsouthscape;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mindorks.placeholderview.InfinitePlaceHolderView;

import java.util.List;

import id.co.reich.mockupsouthscape.model.InfiniteFeedInfo;
import id.co.reich.mockupsouthscape.util.Utils;
import id.co.reich.mockupsouthscape.view.ItemView;
import id.co.reich.mockupsouthscape.view.LoadMoreView;

public class PlaceholderActivity extends AppCompatActivity {

    private InfinitePlaceHolderView mLoadMoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_placeholder);
        mLoadMoreView = findViewById(R.id.loadMoreView);
        setupView();
    }

    private void setupView(){

        List<InfiniteFeedInfo> feedList = Utils.loadInfiniteFeeds(this.getApplicationContext());
        Log.d("MockUp", "LoadMoreView.LOAD_VIEW_SET_COUNT " + LoadMoreView.LOAD_VIEW_SET_COUNT);
        for(int i = 0; i < LoadMoreView.LOAD_VIEW_SET_COUNT; i++){
            mLoadMoreView.addView(new ItemView(this.getApplicationContext(), feedList.get(i)));
        }
        mLoadMoreView.setLoadMoreResolver(new LoadMoreView(mLoadMoreView, feedList));
    }

}

package id.co.reich.mockupsouthscape.infinity;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import id.co.reich.mockupsouthscape.model.InfiniteFeedInfo;

public class Utils {
    private static final String TAG = "Constants";

    public static List<InfiniteFeedInfo> loadInfiniteFeeds(Context context){
        try{
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONArray array = new JSONArray(loadJSONFromAsset(context, "infinite_news.json"));
            List<InfiniteFeedInfo> feedList = new ArrayList<>();
            Log.d("ListLength", String.valueOf(array.length()));
            for(int i=0;i<array.length();i++){
                //Log.d("Inner", array.getString(i));
                InfiniteFeedInfo feed = gson.fromJson(array.getString(i), InfiniteFeedInfo.class);
                Log.d("inner", feed.getTitle());
                feedList.add(feed);
            }

            return feedList;
        }catch (Exception e){
            Log.d(TAG,"seedGames parseException " + e);
            e.printStackTrace();
            return null;
        }
    }

    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is=null;
        try {
            AssetManager manager = context.getAssets();
            Log.d(TAG,"path "+jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

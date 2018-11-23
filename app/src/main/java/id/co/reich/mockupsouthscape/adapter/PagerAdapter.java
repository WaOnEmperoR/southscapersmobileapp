package id.co.reich.mockupsouthscape.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Rachmawan on 8/7/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                TabbedFragment_01 tab1 = new TabbedFragment_01();
//                return tab1;
//            case 1:
//                TabbedFragment_02 tab2 = new TabbedFragment_02();
//                return tab2;
//            case 2:
//                TabbedFragment_03 tab3 = new TabbedFragment_03();
//                return tab3;
//            default:
//                return null;
//        }
        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

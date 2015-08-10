package biz.franch.protoi2.carousel;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import biz.franch.protoi2.main_menu.Banner;
import biz.franch.protoi2.main_menu.MainActivity;
import biz.franch.protoi2.R;

public class MyPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private MyLinearLayout cur = null;
    private MyLinearLayout next = null;
    private MainActivity context;
    private FragmentManager fm;
    private float scale;
    ArrayList<Banner> bannerList;
    public MyPagerAdapter(MainActivity context, FragmentManager fm, ArrayList<Banner> bannerList) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == bannerList.size() * 1000 / 2)
            scale = MainActivity.BIG_SCALE;
        else
            scale = MainActivity.SMALL_SCALE;

        position = position % bannerList.size();
        return MyFragment.newInstance(context, position, scale, bannerList);
    }

    @Override
    public int getCount() {
        return bannerList.size() * 1000;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            cur = getRootView(position);
            next = getRootView(position + 1);

            cur.setScaleBoth(MainActivity.BIG_SCALE
                    - MainActivity.DIFF_SCALE * positionOffset);
            next.setScaleBoth(MainActivity.SMALL_SCALE
                    + MainActivity.DIFF_SCALE * positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private MyLinearLayout getRootView(int position) {
        return (MyLinearLayout)
                fm.findFragmentByTag(this.getFragmentTag(position))
                        .getView().findViewById(R.id.root);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + context.pager.getId() + ":" + position;
    }
}

package biz.franch.protoi2.information;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import biz.franch.protoi2.R;
import biz.franch.protoi2.carousel.MyLinearLayout;

public class CarouselPagerAdapterPhotos extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private MyLinearLayout cur = null;
    private MyLinearLayout next = null;
    private Information activity;
    ScrollView scrollView;
    FrameLayout header;
    private FragmentManager fm;
    private float scale;
    String[] photoUrl;
    public static int colors[];
    public CarouselPagerAdapterPhotos(Information activity, FragmentManager fm, String[] photoUrl, ScrollView scrollView, FrameLayout header) {
        super(fm);
        this.fm = fm;
        this.activity = activity;
        this.photoUrl = photoUrl;
        this.scrollView = scrollView;
        this.header = header;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == photoUrl.length * Information.LOOPS/2)
            scale = Information.BIG_SCALE;
        else
            scale = Information.SMALL_SCALE;

        position = position % photoUrl.length;
        return CarouselMyFragmentPhoto.newInstance(activity, position, scale, photoUrl, scrollView, header);
    }

    @Override
    public int getCount() {
        return photoUrl.length * Information.LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (positionOffset >= 0 && positionOffset <= 1) {
            cur = getRootView(position);
            next = getRootView(position + 1);

            cur.setScaleBoth(Information.BIG_SCALE
                    - Information.DIFF_SCALE * positionOffset);
            next.setScaleBoth(Information.SMALL_SCALE
                    + Information.DIFF_SCALE * positionOffset);
        }
        Log.i("SCROLLED", position+"");
    }

    @Override
    public void onPageSelected(int position) {
//        scrollView.setBackgroundColor(colors[position]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private MyLinearLayout getRootView(int position) {
        return (MyLinearLayout)
                fm.findFragmentByTag(this.getFragmentTag(position))
                        .getView().findViewById(R.id.root_photo);
    }

    public static void getColor(int[] color) {
        colors = color;
        Log.i("SUCCESS", "+");
    }
    private String getFragmentTag(int position) {
        return "android:switcher:" + activity.viewPager.getId() + ":" + position;
    }
}

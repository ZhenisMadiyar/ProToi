package biz.franch.protoi2.information;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import biz.franch.protoi2.R;
import biz.franch.protoi2.carousel.MyLinearLayout;

public class CarouselPagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private MyLinearLayout cur = null;
    private MyLinearLayout next = null;
    private Information activity;
    private FragmentManager fm;
    private float scale;
    String[] videoIds;
    public static int colors[];

    private int dotsCountVideo;
    private TextView[] dotsVideo;
    public CarouselPagerAdapter(Information activity, FragmentManager fm, String[] videoIds) {
        super(fm);
        this.fm = fm;
        this.activity = activity;
        this.videoIds = videoIds;
        dotsCountVideo = videoIds.length;
        dotsVideo = new TextView[dotsCountVideo];
        for (int j = 0; j < dotsCountVideo; j++) {
            dotsVideo[j] = new TextView(activity);
            dotsVideo[j].setText(Html.fromHtml("&#8226;"));
            dotsVideo[j].setTextSize(30);
            dotsVideo[j].setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
            Information.dotsLayoutVideo.addView(dotsVideo[j]);
        }
        dotsVideo[0].setTextColor(activity.getResources().getColor(android.R.color.holo_blue_light));
    }

    @Override
    public Fragment getItem(int position) {
        // make the first pager bigger than others
        if (position == videoIds.length * Information.LOOPS / 2)
            scale = Information.BIG_SCALE;
        else
            scale = Information.SMALL_SCALE;

        position = position % videoIds.length;
        return CarouselMyFragment.newInstance(activity, position, scale, videoIds);
    }

    @Override
    public int getCount() {
        return videoIds.length * Information.LOOPS;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            cur = getRootView(position);
            next = getRootView(position + 1);

            cur.setScaleBoth(Information.BIG_SCALE
                    - Information.DIFF_SCALE * positionOffset);
            next.setScaleBoth(Information.SMALL_SCALE
                    + Information.DIFF_SCALE * positionOffset);
        }
        Log.i("SCROLLED", "+");
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("Selected position", position+"");
        if (position >= dotsCountVideo) {
            if (dotsCountVideo == 0) {
                Log.i("EQUAL TO", position + "");
            } else {
                position = position % dotsCountVideo;
                Log.i("Selected mod position", position+"");
                for (int i = 0; i < dotsCountVideo; i++) {
                    if (dotsCountVideo + 1 <= position) {
                        i = 0;
                    }
                    dotsVideo[i].setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                }
                dotsVideo[position].setTextColor(activity.getResources().getColor(android.R.color.holo_blue_light));
            }
        } else {
            for (int i = 0; i < dotsCountVideo; i++) {
                if (dotsCountVideo + 1 <= position) {
                    i = 0;
                }
                dotsVideo[i].setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
            }
            dotsVideo[position].setTextColor(activity.getResources().getColor(android.R.color.holo_blue_light));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private MyLinearLayout getRootView(int position) {
        return (MyLinearLayout)
                fm.findFragmentByTag(this.getFragmentTag(position))
                        .getView().findViewById(R.id.root1);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + activity.viewPagerVideo.getId() + ":" + position;
    }


    public static void getColor(int[] color) {
        colors = color;
    }
}

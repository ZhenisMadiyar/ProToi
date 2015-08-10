package biz.franch.protoi2.information;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import biz.franch.protoi2.R;
import biz.franch.protoi2.carousel.MyLinearLayout;

public class CarouselMyFragment extends Fragment {
    String videoId[];
    RelativeLayout l;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	public static Fragment newInstance(Activity context, int pos, float scale, String[] videoIds) {
		Bundle b = new Bundle();
		b.putInt("pos", pos);
		b.putFloat("scale", scale);
        b.putStringArray("videoId", videoIds);
		return Fragment.instantiate(context, CarouselMyFragment.class.getName(), b);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.novideo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();
        l = (RelativeLayout) inflater.inflate(R.layout.carousel_item_video, container, false);

		final int pos = this.getArguments().getInt("pos");
        videoId = this.getArguments().getStringArray("videoId");
        ImageView button = (ImageView) l.findViewById(R.id.play_btn);
        ImageView imageView = (ImageView) l.findViewById(R.id.imageViewVideo);
        Log.i("VIDEO_ID", videoId[pos]);
        if (videoId[pos].equals("-")) {
            button.setVisibility(View.GONE);
        } else {
            ImageLoader.getInstance().displayImage("http://img.youtube.com/vi/" + videoId[pos] + "/hqdefault.jpg", imageView, options, animateFirstListener);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), VideoActivity.class);
                    intent.putExtra("videoid", videoId[pos]);
                    startActivity(intent);
                }
            });
        }
        MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root1);
		float scale = this.getArguments().getFloat("scale");
		root.setScaleBoth(scale);
		return l;
	}


    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}

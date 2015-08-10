package biz.franch.protoi2.information;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.michaelevans.colorart.library.ColorArt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import biz.franch.protoi2.R;
import biz.franch.protoi2.carousel.MyLinearLayout;

public class CarouselMyFragmentPhoto extends Fragment {
    ArrayList<String> urls = new ArrayList<String>();
    ImageView imageView;
    static ScrollView scrollView1;
    static FrameLayout header1;
    int color[];
    String[] photoUrl;

    public static Fragment newInstance(Activity context, int pos, float scale, String[] photoUrl, ScrollView scrollView, FrameLayout header) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putFloat("scale", scale);
        b.putStringArray("photoUrl", photoUrl);
        scrollView1 = scrollView;
        header1 = header;
        return Fragment.instantiate(context, CarouselMyFragmentPhoto.class.getName(), b);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        final RelativeLayout l = (RelativeLayout) inflater.inflate(R.layout.carousel_item_photo, container, false);
//        final FrameLayout lo = (FrameLayout) inflater.inflate(R.layout.info_activity, container, false);
        final int pos = this.getArguments().getInt("pos");
        photoUrl = this.getArguments().getStringArray("photoUrl");
        imageView = (ImageView) l.findViewById(R.id.imageViewPhoto);
        final ProgressBar progressBar = (ProgressBar) l.findViewById(R.id.progressBarPhoto);
        progressBar.setVisibility(View.GONE);
//        Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                imageView.setImageBitmap(bitmap);
//                ColorArt colorArt = new ColorArt(bitmap);
////                        Information.setColor(colorArt);
//                l.setBackgroundColor(colorArt.getBackgroundColor());
//                Information.scrollView.setBackgroundColor(colorArt.getBackgroundColor());
////                progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable drawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable drawable) {
//
//            }
//        };
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        imageView.setImageBitmap(bitmapArrayList.get(pos));
        final RelativeLayout backgrounGradient = (RelativeLayout) l.findViewById(R.id.gradientBackground);


        Picasso.with(getActivity()).invalidate(photoUrl[pos]);
        Picasso.with(getActivity())
                .load(photoUrl[pos])
                .into(imageView, new Callback() {

                    @Override
                    public void onSuccess() {
                        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        ColorArt colorArt = new ColorArt(bitmap);
//                        Information.setColor(colorArt);
//                        l.setBackgroundColor(colorArt.getBackgroundColor());
//                        Information.scrollView.setBackgroundColor(colorArt.getBackgroundColor());
//                        scrollView1.setBackgroundColor(l.getDrawingCacheBackgroundColor());
//                        header1.setBackgroundColor(colorArt.getBackgroundColor());
//                        String hexColor = String.format("#%06X", (0xFFFFFF & colorArt.getBackgroundColor()));
//                        Log.i("COLOR_ART", hexColor);
//                        Information.textViewTitle.setTextColor(colorArt.getBackgroundColor());
                        GradientDrawable gd = new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                new int[] {0x00000000,colorArt.getBackgroundColor()});
                        gd.setCornerRadius(0f);
                        backgrounGradient.setBackgroundDrawable(gd);
                    }

                    @Override
                    public void onError() {

                    }
                });
//        Bitmap album = BitmapFactory.decodeResource(getResources(), imageView);
//        ColorArt colorArt = new ColorArt(album);

        MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root_photo);
        float scale = this.getArguments().getFloat("scale");
        root.setScaleBoth(scale);
        for (String s : photoUrl) {
            urls.add(s);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FullScreenImage.class);
                intent.putStringArrayListExtra("urlArray", urls);
                intent.putExtra("position", pos + "");
                startActivity(intent);
            }
        });
        return l;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog.setVisibility(View.VISIBLE);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            bmImage.setImageBitmap(result);
//            dialog.setVisibility(View.GONE);
        }
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

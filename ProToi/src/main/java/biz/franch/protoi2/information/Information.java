package biz.franch.protoi2.information;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.michaelevans.colorart.library.ColorArt;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import biz.franch.protoi2.R;
import biz.franch.protoi2.animator.IO2014HeaderAnimator;
import biz.franch.protoi2.cache.ImageCache;
import biz.franch.protoi2.cache.ImageFetcher;
import biz.franch.protoi2.database.StoreDatabaseInformation;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

/**
 * Created by Admin on 25.06.2015.
 */
public class Information extends FragmentActivity {

    //    public static int PAGES = 0;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public static int LOOPS = 1000;
    //    public static int FIRST_PAGE = PAGES * LOOPS / 2;
    public static float BIG_SCALE = 1.0f;
    public static float SMALL_SCALE = 0.7f;
    public static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    public CarouselPagerAdapter adapter;
    public ViewPager pager;
    public CarouselPagerAdapterPhotos adapterPhoto;

    TextView desc;
    Map<String, Object> parameters;
    Gson gson;
    public static String description;
    public static String photos;
    public static String videoId;
    public static String name;
    public static String phoneNumber;

    public ViewPager viewPager;
    public ViewPager viewPagerVideo;
    ArrayList<String> arrayListImageUrls;

    ProgressDialog pDialog;
    VideoView videoview;
    String arrayPhotoUrl[];

//    protected View mHeader;
//    protected int mFlexibleSpaceImageHeight;
//    protected View mHeaderBar;
//    protected View mListBackgroundView;
//    protected int mActionBarSize;
//    protected int mIntersectionHeight;

//    private View mImage;
//    private View mImageHolder;
//    private View mHeaderBackground;
//    private int mPrevScrollY;
//    private boolean mGapIsChanging;
//    private boolean mGapHidden;
//    private boolean mReady;

    Button call;
    ImageButton back;
    // Insert your Video URL

    StoreDatabaseInformation storeDatabase;
    SQLiteDatabase sqdbWrite;
    SQLiteDatabase sqdbRead;

    private ImageFetcher mImageFetcher;
    private static final String TAG = "ImageGridFragment";
    private static String IMAGE_CACHE_DIR;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    public static ScrollView scrollView;
    public static TextView textViewTitle;

    private LinearLayout dotsLayout;
    public static LinearLayout dotsLayoutVideo;
    private int dotsCount;
    private TextView[] dots;

    FrameLayout header;
    static ColorArt colorArt;
    static Bitmap bitmap1;

    ArrayList<Integer> backgroundColor;
    List<Bitmap> arrayListBitmap;
    public ClassTarget targetClass = new ClassTarget();
    ProgressBar progressBar;
    public static String databaseColors = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        progressBar = (ProgressBar) findViewById(R.id.progressBarViewPager);
        backgroundColor = new ArrayList<>();
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        back = (ImageButton) findViewById(R.id.imageButtonBack);
        call = (Button) findViewById(R.id.button);
        desc = (TextView) findViewById(R.id.textView);
        textViewTitle = (TextView) findViewById(R.id.title2);
        dotsLayout = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        dotsLayoutVideo = (LinearLayout) findViewById(R.id.viewPagerCountDotsVideo);
        header = (FrameLayout) findViewById(R.id.header_text_layout);
//        scrollView.setBackgroundColor(-11385788);
        //carousle
        viewPagerVideo = (ViewPager) findViewById(R.id.view_pagerVideo);

        final Intent intent = getIntent();
        arrayListImageUrls = new ArrayList<String>();

        gson = new Gson();
        //map lists
        parameters = new HashMap<String, Object>();
        Log.i("OBJECT_ID_INFO=", intent.getStringExtra("objectId"));
        parameters.put("list_id", intent.getStringExtra("objectId"));

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        storeDatabase = new StoreDatabaseInformation(this);
        sqdbWrite = storeDatabase.getWritableDatabase();
        sqdbRead = storeDatabase.getReadableDatabase();

        IO2014HeaderAnimator animator = new IO2014HeaderAnimator(this);
        StikkyHeaderBuilder.stickTo(scrollView)
                .setHeader(R.id.header, (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content))
                .minHeightHeaderDim(R.dimen.min_height_header_materiallike)
                .animator(animator)
                .build();

        Cursor cursor = sqdbRead.query("information", new String[]
                        {"id, tag, name, imageUrl, videoUrl, desc, color"},
//                "id="+getPostition()+" AND tag="+getTag(), // The columns for the WHERE clause
                "name=" + "'" + intent.getStringExtra("name") + "'", // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
//        parameters.put("page", 1);
//        parameters.put("limit", 10);
        if (cursor.getCount() == 0) {
            backgroundColor.clear();
            arrayListBitmap = new ArrayList<Bitmap>();
            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
            ParseCloud.callFunctionInBackground("list", parameters, new FunctionCallback<Object>() {

                @Override
                public void done(Object response, ParseException e) {
                    if (e == null) {
                        String json = gson.toJson(response);
                        Log.e("INFO", json);
                        try {
                            JSONArray jsonArray = new JSONArray(json);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            JSONObject estimatedData = jsonObject.getJSONObject("estimatedData");
                            name = estimatedData.getString("name");
                            videoId = estimatedData.getString("videos");
                            photos = estimatedData.getString("photos");
                            description = estimatedData.getString("description");
                            description = description.replace("'", "");
                            phoneNumber = estimatedData.getString("phoneNumber");
                            arrayPhotoUrl = photos.split(",");
                            int pos = intent.getIntExtra("pos", 0);
                            int tag = intent.getIntExtra("tag", 0);
                            String nameIntent = intent.getStringExtra("name");

                            new DownloadImageTask(pos, tag, nameIntent, photos, videoId, description).execute(arrayPhotoUrl);
//                            Log.i("SIZE_COLOR_ARRAY", backgroundColor.size()+"");
//                            for (int i = 0; i < backgroundColor.size(); i++) {
//                                String hexColor = String.format("#%06X", (0xFFFFFF & backgroundColor.get(i)));
//                                databaseColors = hexColor+"";
//                                Log.i("COLOR", databaseColors);
//                            }
//                            Log.i("DATABASE_COLOR", databaseColors);
//                            for (int l = 0; l < arrayPhotoUrl.length; l++) {
////                                Target target = new Target() {
////                                    @Override
////                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
////                                        ColorArt colorArt = new ColorArt(bitmap);
//////                            scrollView.setBackgroundColor(colorArt.getBackgroundColor());
////                                        backgroundColor.add(colorArt.getBackgroundColor());
////                                    }
////
////                                    @Override
////                                    public void onBitmapFailed(Drawable drawable) {
////
////                                    }
////
////                                    @Override
////                                    public void onPrepareLoad(Drawable drawable) {
////
////                                    }
////                                };
//                                Picasso.with(Information.this)
//                                        .load(arrayPhotoUrl[l])
//                                        .into(targetClass);
//                            }
//                            String insertQuery2 = "INSERT INTO information (id, tag, name, imageUrl, videoUrl, desc)" +
//                                    " VALUES ('" + intent.getIntExtra("pos", 0) + "','" + intent.getIntExtra("tag", 0)
//                                    + "', '" + intent.getStringExtra("name") + "','" + photos + "','" + videoId + "'," +
//                                    "'" + description + "')";
//                            sqdbWrite.execSQL(insertQuery2);
                            for (String s : arrayPhotoUrl) {
                                arrayListImageUrls.add(s);
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        if (photos == null && videoId == null) {
                            desc.setText("404-Not found :(");
                            call.setVisibility(View.GONE);
                        } else {
                            String photoUrl[] = photos.split(",");

//                            for (int i = 0; i < photoUrl.length; i++) {
//                                Log.i("SIZE_BITMAP_photoUrl", photoUrl[i]);
//                                Target target = new Target() {
//                                    @Override
//                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                        arrayListBitmap.add(bitmap);
//                                        Log.i("SIZE_BITMAP_loaded", arrayListBitmap.size() + "");
//                                    }
//
//                                    @Override
//                                    public void onBitmapFailed(Drawable drawable) {
//
//                                    }
//
//                                    @Override
//                                    public void onPrepareLoad(Drawable drawable) {
//
//                                    }
//                                };
//
//                                Picasso.with(Information.this)
//                                        .load(photoUrl[i])
//                                        .into(target);
//                                Log.i("SIZE_BITMAP_for", arrayListBitmap.size() + "");
//                            }
                            dotsCount = photoUrl.length;
                            Log.i("SIZE_BITMAP", arrayListBitmap.size() + "");
                            adapterPhoto = new CarouselPagerAdapterPhotos(Information.this, Information.this.getSupportFragmentManager(), photoUrl, scrollView, header);
                            viewPager.setAdapter(adapterPhoto);
                            viewPager.setOffscreenPageLimit(1);

                            dots = new TextView[dotsCount];
                            for (int j = 0; j < dotsCount; j++) {
                                dots[j] = new TextView(Information.this);
                                dots[j].setText(Html.fromHtml("&#8226;"));
                                dots[j].setTextSize(30);
                                dots[j].setTextColor(getResources().getColor(android.R.color.darker_gray));
                                dotsLayout.addView(dots[j]);
                            }
                            dots[0].setTextColor(getResources().getColor(android.R.color.holo_blue_light));


//                            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                                @Override
//                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                                }
//
//                                @Override
//                                public void onPageSelected(int position) {
//                                    Log.i("POSITION_PAGER=", position + "");
//
////                                    for (int i = 0; i < dotsCount; i++) {
////                                        if (dotsCount+1 <= position) {
////                                            i = 0;
////                                        }
////                                        dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
////                                    }
////                                    dots[position].setTextColor(getResources().getColor(android.R.color.holo_blue_light));
//                                }
//
//                                @Override
//                                public void onPageScrollStateChanged(int state) {
//
//                                }
//                            });
//                            viewPagerVideo.setOnPageChangeListener(adapter);
//                            viewPagerVideo.setCurrentItem(photoUrl.length * LOOPS / 2);
////                    viewPagerVideo.setOffscreenPageLimit(3);
//                            viewPagerVideo.setPageMargin(-200);
//                    viewPagerVideo.setAdapter(adapterVideoView);
                            //VIDEO ADAPTER
                            if (videoId.equals("-")) {
                                viewPagerVideo.setVisibility(View.GONE);
                            } else {
                                String videoIds[] = videoId.split(",");
                                adapter = new CarouselPagerAdapter(Information.this, Information.this.getSupportFragmentManager(), videoIds);
                                viewPagerVideo.setAdapter(adapter);
                                viewPagerVideo.setOnPageChangeListener(adapter);
                                // Set current item to the middle page so we can fling to both
                                // directions left and right
                                viewPagerVideo.setCurrentItem(videoIds.length * LOOPS / 2);
                                // Necessary or the pager will only have one extra page to show
                                // make this at least however many pages you can see
                                viewPagerVideo.setOffscreenPageLimit(3);
                                // Set margin for pages as a negative number, so a part of next and
                                // previous pages will be showed
                                viewPagerVideo.setPageMargin(-200);
                            }
                        }
                    } else {
                        Log.e("Error", e.toString());
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "NOT-NULL", Toast.LENGTH_SHORT).show();
            int i = 0;
            while (cursor.moveToNext()) {
                backgroundColor.clear();
                progressBar.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                String videoUrl = cursor.getString(cursor.getColumnIndex("videoUrl"));
                String descStr = cursor.getString(cursor.getColumnIndex("desc"));
                String backgroundColors = cursor.getString(cursor.getColumnIndex("color"));
                String magicColor[] = backgroundColors.split(",");


                for (int p = 1; p < magicColor.length; p++) {
                    int c = Color.parseColor(magicColor[p]);
                    Log.i("MAGIC_COLOR_INT", c + "");
                    backgroundColor.add(c);
                }
                String videoIds[] = videoUrl.split(",");
                String photoUrl[] = imageUrl.split(",");
                Log.i("MAGIC_COLOR", magicColor + "");
                Log.i("MAGIC_COLOR_STR", backgroundColors);

                dotsCount = photoUrl.length;
                dots = new TextView[dotsCount];
                for (int j = 0; j < dotsCount; j++) {
                    dots[j] = new TextView(Information.this);
                    dots[j].setText(Html.fromHtml("&#8226;"));
                    dots[j].setTextSize(30);
                    dots[j].setTextColor(getResources().getColor(android.R.color.darker_gray));
                    dotsLayout.addView(dots[j]);
                }
                dots[0].setTextColor(getResources().getColor(android.R.color.holo_blue_light));
//                new MyTask().execute(photoUrl);
//                new DownloadImageTask().execute(photoUrl);
//                fillArray(photoUrl);
                Log.i("background_color_razmer", backgroundColor.size() + "");
                desc.setText(descStr);
                textViewTitle.setText(name);
//                for (int k = 0; k < photoUrl.length; k++) {
//                    Log.i("SIZE_BITMAP_photoUrl", photoUrl[k]);
//                    Target target = new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            Color colo
//                            arrayListBitmap.add(bitmap);
//                            Log.i("SIZE_BITMAP_loaded", arrayListBitmap.size() + "");
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable drawable) {
//
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable drawable) {
//
//                        }
//                    };
//
//                    Picasso.with(Information.this)
//                            .load(photoUrl[k])
//                            .into(target);
//                    Log.i("SIZE_BITMAP_for", arrayListBitmap.size() + "");
//                }

                adapterPhoto = new CarouselPagerAdapterPhotos(Information.this, Information.this.getSupportFragmentManager(), photoUrl, scrollView, header);
                viewPager.setAdapter(adapterPhoto);
                viewPager.setOffscreenPageLimit(1);

//                viewPagerVideo.setOnPageChangeListener(adapter);
//                viewPagerVideo.setCurrentItem(photoUrl.length * LOOPS / 2);
////                    viewPagerVideo.setOffscreenPageLimit(3);
//                viewPagerVideo.setPageMargin(-200);
//                    viewPagerVideo.setAdapter(adapterVideoView);
                //VIDEO ADAPTER
                if (videoUrl.equals("-")) {
                    viewPagerVideo.setVisibility(View.GONE);
                } else {
                    adapter = new CarouselPagerAdapter(Information.this, Information.this.getSupportFragmentManager(), videoIds);
                    viewPagerVideo.setAdapter(adapter);
                    viewPagerVideo.setOnPageChangeListener(adapter);
                    // Set current item to the middle page so we can fling to both
                    // directions left and right
                    viewPagerVideo.setCurrentItem(videoIds.length * LOOPS / 2);
                    // Necessary or the pager will only have one extra page to show
                    // make this at least however many pages you can see
                    viewPagerVideo.setOffscreenPageLimit(3);
                    // Set margin for pages as a negative number, so a part of next and
                    // previous pages will be showed
                    viewPagerVideo.setPageMargin(-200);
                }
            }
        }

        videoId = null;
        photos = null;
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("POSITION_SIZE_COLOR=", backgroundColor.size() + "");
                Log.i("POSITION_SELECTED", position + "");
                if (backgroundColor != null) {
                    if (position >= backgroundColor.size()) {
                        if (backgroundColor.size() == 0) {
                            Log.i("EQUAL TO", position + "");
                        } else {
                            position = position % backgroundColor.size();
                            Log.i("MOD_POSITION", position + "");
                            String hexColor2 = String.format("#%06X", (0xFFFFFF & backgroundColor.get(position)));
                            Log.i("hexColor", hexColor2 + " " + hexColor2.substring(1, 2).toLowerCase());
                            if (hexColor2.substring(1, 2).toLowerCase().equals("a") || hexColor2.substring(1, 2).toLowerCase().equals("b")
                                    || hexColor2.substring(1, 2).toLowerCase().equals("c") || hexColor2.substring(1, 2).toLowerCase().equals("d")
                                    || hexColor2.substring(1, 2).toLowerCase().equals("e") || hexColor2.substring(1, 2).toLowerCase().equals("f")) {
                                Log.i("HERE", "KIRDI");
                                desc.setTextColor(Color.BLACK);
                                textViewTitle.setTextColor(Color.BLACK);
                                back.setImageResource(R.mipmap.ic_action_back_black);
                            } else {
                                desc.setTextColor(Color.WHITE);
                                textViewTitle.setTextColor(Color.WHITE);
                                back.setImageResource(R.mipmap.ic_action_back_white);
                            }
                            scrollView.setBackgroundColor(backgroundColor.get(position));
                            header.setBackgroundColor(backgroundColor.get(position));
                        }
                    } else {
                        scrollView.setBackgroundColor(backgroundColor.get(position));
                        header.setBackgroundColor(backgroundColor.get(position));
                        String hexColor2 = String.format("#%06X", (0xFFFFFF & backgroundColor.get(position)));
                        Log.i("hexColor", hexColor2 + " " + hexColor2.substring(1, 2).toLowerCase());
                        if (hexColor2.substring(1, 2).toLowerCase().equals("a") || hexColor2.substring(1, 2).toLowerCase().equals("b")
                                || hexColor2.substring(1, 2).toLowerCase().equals("c") || hexColor2.substring(1, 2).toLowerCase().equals("d")
                                || hexColor2.substring(1, 2).toLowerCase().equals("e") || hexColor2.substring(1, 2).toLowerCase().equals("f")) {
                            Log.i("HERE", "KIRDI");
                            desc.setTextColor(Color.BLACK);
                            textViewTitle.setTextColor(Color.BLACK);
                            back.setImageResource(R.mipmap.ic_action_back_black);
                        } else {
                            desc.setTextColor(Color.WHITE);
                            textViewTitle.setTextColor(Color.WHITE);
                            back.setImageResource(R.mipmap.ic_action_back_white);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("POSITION_PAGER=", position + "");
                if (position >= dotsCount) {
                    if (dotsCount == 0) {
                        Log.i("EQUAL TO", position + "");
                    } else {
                        position = position % dotsCount;
                        for (int i = 0; i < dotsCount; i++) {
                            if (dotsCount + 1 <= position) {
                                i = 0;
                            }
                            dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
                        }
                        dots[position].setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                    }
                } else {
                    for (int i = 0; i < dotsCount; i++) {
                        if (dotsCount + 1 <= position) {
                            i = 0;
                        }
                        dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                    dots[position].setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                }
//                                    for (int i = 0; i < dotsCount; i++) {
//                                        if (dotsCount+1 <= position) {
//                                            i = 0;
//                                        }
//                                        dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
//                                    }
//                                    dots[position].setTextColor(getResources().getColor(android.R.color.holo_blue_light));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
                Log.i("Phone", phoneNumber);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void fillArray(String[] photoUrl) {
        for (final String url : photoUrl) {
//                    Toast.makeText(getApplicationContext(), "KIRDI"+url, Toast.LENGTH_SHORT).show();
//                    Target target = new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            Toast.makeText(getApplicationContext(), "LOADED"+url, Toast.LENGTH_SHORT).show();
//                            ColorArt colorArt = new ColorArt(bitmap);
////                            scrollView.setBackgroundColor(colorArt.getBackgroundColor());
//                            backgroundColor.add(colorArt.getBackgroundColor());
//                            Log.i("BitmapLoaded", backgroundColor.size()+"");
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Drawable drawable) {
//
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable drawable) {
//
//                        }
//                    };
            Log.i("URL", url);
            Picasso.with(Information.this)
                    .load(url)
                    .into(targetClass);
        }
    }


    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        int pos;
        int tag;
        String name;
        String photos;
        String videoId;
        String description;

        public DownloadImageTask(int pos, int tag, String nameIntent, String photos, String videoId, String description) {
            this.pos = pos;
            this.tag = tag;
            this.name = nameIntent;
            this.photos = photos;
            this.videoId = videoId;
            this.description = description;
        }

        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            for (String urldisplay : urls) {
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    ColorArt colorArt = new ColorArt(mIcon11);
                    backgroundColor.add(colorArt.getBackgroundColor());
//                    String hexColor = String.format("#%06X", (0xFFFFFF & colorArt.getBackgroundColor()));
//                    databaseColors = hexColor+",";
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            databaseColors = "";
//            bmImage.setImageBitmap(result);
            progressBar.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            scrollView.setBackgroundColor(backgroundColor.get(0));
            header.setBackgroundColor(backgroundColor.get(0));
            String hexC = String.format("#%06X", (0xFFFFFF & backgroundColor.get(0)));
            Log.i("hexColor", hexC + " " + hexC.substring(1, 2).toLowerCase());
            if (hexC.substring(1, 2).toLowerCase().equals("a") || hexC.substring(1, 2).toLowerCase().equals("b")
                    || hexC.substring(1, 2).toLowerCase().equals("c") || hexC.substring(1, 2).toLowerCase().equals("d")
                    || hexC.substring(1, 2).toLowerCase().equals("e") || hexC.substring(1, 2).toLowerCase().equals("f")) {
                desc.setTextColor(Color.BLACK);
                textViewTitle.setTextColor(Color.BLACK);
                back.setImageResource(R.mipmap.ic_action_back_black);
                desc.setText(description);
                textViewTitle.setText(name);
            } else {
                desc.setTextColor(Color.WHITE);
                textViewTitle.setTextColor(Color.WHITE);
                back.setImageResource(R.mipmap.ic_action_back_white);
                desc.setText(description);
                textViewTitle.setText(name);
            }

            for (int i = 0; i < backgroundColor.size(); i++) {
                String hColor = String.format("#%06X", (0xFFFFFF & backgroundColor.get(i)));
                databaseColors = databaseColors + "," + hColor;
            }
            Log.i("DATABASE_COLOR#2", databaseColors);
            String insertQuery2 = "INSERT INTO information (id, tag, name, imageUrl, videoUrl, desc, color)" +
                    " VALUES ('" + pos + "','" + tag
                    + "', '" + name + "','" + photos + "','" + videoId + "'," +
                    "'" + description + "', '" + databaseColors + "')";
            sqdbWrite.execSQL(insertQuery2);
        }
    }

    public static void setColor(ColorArt i) {
        Log.i("COLOR_BKG_SCROLL", i + "");
        colorArt = i;
//        int []colors = i;
//        Log.i("COLOR_SIZE", colors.length+"");
    }

    public static void setBitmap(Bitmap bitmap) {
        bitmap1 = bitmap;
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        private DisplayImageOptions options;

//        private int[] mImages = new int[] {
//                R.drawable.audi1,
//                R.drawable.audi2,
//                R.drawable.audi3,
//                R.drawable.audi4
//        };

        @Override
        public int getCount() {
            return arrayPhotoUrl.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Context context = Information.this;

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(20)).build();

            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_medium);
            imageView.setPadding(padding, padding, padding, padding);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(parms);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageView.setImageResource(mImages[position]);
            ImageLoader.getInstance().displayImage(arrayPhotoUrl[position], imageView, options, animateFirstListener);
            ((ViewPager) container).addView(imageView, 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), FullScreenImage.class);
                    intent.putStringArrayListExtra("urlArray", arrayListImageUrls);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }


    private class VideoPagerAdapter extends PagerAdapter {

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        private DisplayImageOptions options;
        private String[] videoIds;
        Activity activity;
//        private int[] mImages = new int[] {
//                R.drawable.play,
//                R.drawable.play,
//                R.drawable.play,
//                R.drawable.play
//        };

        public VideoPagerAdapter(Activity activity, String[] videoIds) {
            this.activity = activity;
            this.videoIds = videoIds;
        }

        @Override
        public int getCount() {
            return videoIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Context context = Information.this;

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.not_found_video)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(20)).build();

            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_medium);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            ImageLoader.getInstance().displayImage("http://img.youtube.com/vi/" + videoIds[position] + "/hqdefault.jpg", imageView, options, animateFirstListener);
            ((ViewPager) container).addView(imageView, 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, VideoActivity.class);
                    intent.putExtra("videoid", videoIds[position]);
                    startActivity(intent);
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    public class ClassTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            ColorArt colorArt = new ColorArt(bitmap);
//                            scrollView.setBackgroundColor(colorArt.getBackgroundColor());
            backgroundColor.add(colorArt.getBackgroundColor());
            Log.i("BitmapLoaded", backgroundColor.size() + "");
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {

        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
    }

    private class FillArray {
        String[] photoUrl;

        public FillArray(String[] photoUrl) {
            this.photoUrl = photoUrl;
        }
    }
}

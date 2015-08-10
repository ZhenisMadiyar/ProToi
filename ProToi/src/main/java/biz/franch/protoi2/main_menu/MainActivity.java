package biz.franch.protoi2.main_menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import biz.franch.protoi2.R;
import biz.franch.protoi2.cache.ImageCache;
import biz.franch.protoi2.cache.ImageFetcher;
import biz.franch.protoi2.carousel.MyPagerAdapter;
import biz.franch.protoi2.database.StoreDatabase;
import biz.franch.protoi2.database.StoreDatabaseInformation;
import biz.franch.protoi2.database.StoreDatabaseLists;
import biz.franch.protoi2.lists.ListActivity;


public class MainActivity extends FragmentActivity {
    private ImageFetcher mImageFetcher;
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";


    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    int FIRST_PAGE;
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    public MyPagerAdapter adapter;
    public ViewPager pager;

    ListView listView;
    AdapterServices _adapter;
    ArrayList<AttributesMainMenu> arrayNameServices;
    Map<String, Object> map;
    Map<String, Object> mapBanner;
    Gson gson;
    ProgressBar progressBar;
    TextView textView;

    Timer timer;
    int page = 1;
    boolean switchPage = true;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    StoreDatabase storeDatabase;
    SQLiteDatabase sqdbWrite;
    SQLiteDatabase sqdbRead;
    public static final String MyPrefs = "MyPrefs";
    public static final String MyPrefs2 = "MyPrefs2";
    SharedPreferences sp;
    SharedPreferences preferences;
//    String images[] = {"http://protoi.parseapp.com/photos/main/show.png",
//    "http://protoi.parseapp.com/photos/main/music.png",
//    "http://protoi.parseapp.com/photos/main/photovideo.png",
//    "http://protoi.parseapp.com/photos/main/mic.png",
//    "http://protoi.parseapp.com/photos/main/auto.png",
//    "http://protoi.parseapp.com/photos/main/restaurant.png",
//    "http://protoi.parseapp.com/photos/main/dance.png",
//    "http://protoi.parseapp.com/photos/main/showman.png",
//    "http://protoi.parseapp.com/photos/main/cater.png",
//    "http://protoi.parseapp.com/photos/main/dj.png"};

    String nameSqlTables[] = {"estrada", "vedushii", "shou", "dj", "zhivye_komandy", "limuziny", "keitering",
            "restaran", "dance", "photovideo"};
    ArrayList<Banner> bannerList;
    public static ImageView imageViewSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewSplash = (ImageView) findViewById(R.id.imageViewSplash);
        gson = new Gson();
        bannerList = new ArrayList<Banner>();
        preferences = getSharedPreferences(MyPrefs2, Context.MODE_PRIVATE);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        if (preferences.getString("last_date", null) == null) {
            String now = format.format(Calendar.getInstance().getTime());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("last_date", now);
            editor.apply();
            Log.i("PREFERENCES", "@null");
        } else {
            String lastDate = preferences.getString("last_date", "");
            String now = format.format(Calendar.getInstance().getTime());
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(lastDate);
                d2 = format.parse(now);

                //in milliseconds!
                long diff = d2.getTime() - d1.getTime();

                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);

                Log.i("diffDays", diffDays + "");
                Log.i("diffHours", diffHours + "");
                Log.i("diffMinutes", diffMinutes + "");
                Log.i("diffSeconds", diffSeconds + "");
                if (diffDays == 5) {
                    refersh();
                    Toast.makeText(getApplicationContext(), "REFRESHED!", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //carousel
        pager = (ViewPager) findViewById(R.id.myviewpager);
        mapBanner = new HashMap<String, Object>();
//        mapBanner.put("order", 1);
        if (isOnline()) {
            ParseCloud.callFunctionInBackground("banner", mapBanner, new FunctionCallback<Object>() {
                public void done(Object response, ParseException e) {
                    if (e != null) {
                    } else {
                        String json = gson.toJson(response);
                        if (json.equals("[]")) {
//                        Toast.makeText(getApplicationContext(), "banner NULL", Toast.LENGTH_LONG).show();
                            pager.setVisibility(View.GONE);
                        } else {
                            Log.i("JSON_BANNER", json);
//                        Toast.makeText(getApplicationContext(), "banner not NULL", Toast.LENGTH_LONG).show();
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(json);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject estimatedData = jsonObject.getJSONObject("estimatedData");
                                JSONObject jsonImage = estimatedData.getJSONObject("image");
                                String imageUrl = jsonImage.getString("url");
//                                    String imageUrl = images[i];
                                String name = estimatedData.getString("name");
                                String objectUrl = estimatedData.getString("url");
                                bannerList.add(new Banner(name, objectUrl, imageUrl));
                            }
                            int PAGES = bannerList.size();
                            int LOOPS = 1000;
                            FIRST_PAGE = PAGES * LOOPS / 2;
                            adapter = new MyPagerAdapter(MainActivity.this, MainActivity.this.getSupportFragmentManager(), bannerList);
                            pager.setAdapter(adapter);
                            pager.setOnPageChangeListener(adapter);
                            // Set current item to the middle page so we can fling to both
                            // directions left and right
                            pager.setCurrentItem(FIRST_PAGE);
                            // Necessary or the pager will only have one extra page to show
                            // make this at least however many pages you can see
                            pager.setOffscreenPageLimit(3);
                            // Set margin for pages as a negative number, so a part of next and
                            // previous pages will be showed
                            pager.setPageMargin(-200);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        } else {
            pager.setVisibility(View.GONE);
        }

        sp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.progressBarMainMenu);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory


        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
//        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        initialPage();
//        if (isOnline()) {
//        } else {
//            progressBar.setVisibility(View.GONE);
//        }
    }

    private void refersh() {
        StoreDatabaseLists storeDatabase2 = new StoreDatabaseLists(this);
        SQLiteDatabase sqdbRead2 = storeDatabase2.getReadableDatabase();
        storeDatabase2.cleanTable(sqdbRead2);

        StoreDatabaseInformation storeDatabase3 = new StoreDatabaseInformation(this);
        SQLiteDatabase sqdbRead3 = storeDatabase3.getReadableDatabase();
        storeDatabase3.cleanTable(sqdbRead3);

        storeDatabase.cleanTable(sqdbRead);
        mImageFetcher.clearCache();

        Toast.makeText(getApplicationContext(), "All databases cleaned!", Toast.LENGTH_SHORT).show();
    }

    private void initialPage() {
        textView = (TextView) findViewById(R.id.textViewNet);
        textView.setVisibility(View.GONE);
        listView = (ListView) findViewById(R.id.listViewMainMenu);
        //map services
        map = new HashMap<String, Object>();
        map.put("order", 1);


        storeDatabase = new StoreDatabase(this);
        sqdbWrite = storeDatabase.getWritableDatabase();
        sqdbRead = storeDatabase.getReadableDatabase();
        arrayNameServices = new ArrayList<AttributesMainMenu>();
        Cursor cursor = sqdbRead.query("services", new String[]
                        {"nameService, imageUrl, objectId"},
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        if (cursor.getCount() == 0) {//true
            Log.i("Null", "Cursor_NULL");
            storeDatabase.cleanTable(sqdbRead);
            mImageFetcher.clearCache();
//            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
            if (isOnline()) {
                ParseCloud.callFunctionInBackground("categories", map, new FunctionCallback<Object>() {
                    public void done(Object response, ParseException e) {
                        if (e != null) {
//                            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_LONG).show();
                        } else {
                            String json = gson.toJson(response);
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONObject estimatedData = jsonObject.getJSONObject("estimatedData");
                                    JSONObject jsonImage = estimatedData.getJSONObject("image");
                                    String imageUrl = jsonImage.getString("url");
//                                    String imageUrl = images[i];
                                    String name = estimatedData.getString("name");
                                    String objectId = jsonObject.getString("objectId");
                                    Log.i("INFORMATION", name + " " + imageUrl + "objectId:" + objectId);
                                    arrayNameServices.add(new AttributesMainMenu(name, "", imageUrl, objectId));
                                    String insertQuery = "INSERT INTO services (nameService, imageUrl, objectId)" +
                                            "VALUES ('" + name + "','" + imageUrl + "','" + objectId + "')";
                                    sqdbWrite.execSQL(insertQuery);
                                }
//                                imageViewSplash.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                _adapter = new AdapterServices(getApplicationContext(), arrayNameServices);
                                listView.setAdapter(_adapter);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("first", true);
                                editor.commit();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        } else {
            Log.i("Null", "Cursor_NOT_NULL");
//            Toast.makeText(getApplicationContext(), "NOT-NULL", Toast.LENGTH_SHORT).show();
            while (cursor.moveToNext()) {
                String nameService = cursor.getString(cursor.getColumnIndex("nameService"));
                String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                String objectId = cursor.getString(cursor.getColumnIndex("objectId"));
                Log.i("nameService", nameService);
                Log.i("imageUrl", imageUrl);
                Log.i("objectId", objectId);
                arrayNameServices.add(new AttributesMainMenu(nameService, "", imageUrl, objectId));
            }
//            imageViewSplash.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            _adapter = new AdapterServices(getApplicationContext(), arrayNameServices);
            listView.setAdapter(_adapter);
//            new CountDownTimer(2200, 1000) {
//
//                public void onTick(long millisUntilFinished) {
////                mTextField.setText("Seconds remaining: " + millisUntilFinished / 1000);
//                }
//
//                public void onFinish() {
//                    imageViewSplash.setVisibility(View.GONE);
//                }
//
//            }.start();
            cursor.close();
        }
        if (switchPage) {
            pageSwitcher(4);
        }
//        Interpolator sInterpolator = new AccelerateInterpolator();
//        try {
//            Field mScroller;
//            mScroller = ViewPager.class.getDeclaredField("mScroller");
//            mScroller.setAccessible(true);
//            FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), sInterpolator);
//            // scroller.setFixedDuration(5000);
//            mScroller.set(pager, scroller);
//        } catch (NoSuchFieldException e) {
//        } catch (IllegalArgumentException e) {
//        } catch (IllegalAccessException e) {
//        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("objectId", arrayNameServices.get(i).getObjectId());
                intent.putExtra("name", arrayNameServices.get(i).getName());
                intent.putExtra("sqlName", nameSqlTables[i]);
                intent.putExtra("tag", i);
                startActivity(intent);
            }
        });
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public class FixedSpeedScroller extends Scroller {

        private int mDuration = 4000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 500); // delay
        // in
        // milliseconds
    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {
                    if (page > bannerList.size()-1) { // In my case the number of pages are 5
//                        timer.cancel();
                        // Showing a toast for just testing purpose
//                        Toast.makeText(getApplicationContext(), "Timer stoped",
//                                Toast.LENGTH_LONG).show();
                        page = 0;
                    } else {
                        int fb = bannerList.size() * 1000 / 2;
                        pager.setCurrentItem(fb++);
                    }
                }
            });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        switchPage = false;

        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        switchPage = true;

        mImageFetcher.setExitTasksEarly(false);
//        _adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AdapterServices extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList<AttributesMainMenu> services;

        public AdapterServices(Context context, ArrayList<AttributesMainMenu> arrayNameServices) {
            this.context = context;
            this.services = arrayNameServices;
            inflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return services.size();
        }

        @Override
        public Object getItem(int i) {
            return services.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder view;
            Log.i("POSITION", position+"");
            if (position >= 4) {
                Log.i("HERE", "SIZE_EQUAL");
                imageViewSplash.setVisibility(View.GONE);
            }
            if (convertView == null) {
                view = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_main_menu, null);
                view.name = (TextView) convertView.findViewById(R.id.textViewMainMenu);
                view.imageView = (ImageView) convertView.findViewById(R.id.imageViewMainMenu);
                convertView.setTag(view);
            } else {
                view = (ViewHolder) convertView.getTag();
            }
            view.name.setText(services.get(position).getName());
            Log.i("URL", services.get(position).getImage());
            mImageFetcher.loadImage(services.get(position).getImage(), view.imageView);
//        ImageLoader.getInstance().displayImage(services.get(position).getImage(), view.imageView, options, animateFirstListener);

//            Picasso.with(context)
//                    .load(services.get(position).getImage())
//                    .into(view.imageView);

            return convertView;
        }

        public class ViewHolder {
            public TextView name;
            public ImageView imageView;
        }
    }
}

package biz.franch.protoi2.lists;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import biz.franch.protoi2.R;
import biz.franch.protoi2.cache.ImageCache;
import biz.franch.protoi2.cache.ImageFetcher;
import biz.franch.protoi2.database.StoreDatabase;
import biz.franch.protoi2.database.StoreDatabaseLists;
import biz.franch.protoi2.information.Information;
import biz.franch.protoi2.main_menu.ExpandableHeightGridView;
import biz.franch.protoi2.main_menu.ExpandableHeightListView;

/**
 * Created by Admin on 25.06.2015.
 */
public class ListActivity extends FragmentActivity {
    ExpandableHeightListView listView;
    ExpandableHeightGridView gridView;

    AdapterList adapter;
    AdapterGridServices adapterGrid;

    ArrayList<AttributesList> arrayLists;
    ArrayList<AttributesList> arrayListsGrid;
    Map<String, Object> parametersList;
    Gson gson;
    ProgressBar progressBar;

    private ImageFetcher mImageFetcher;
    private static final String TAG = "ImageGridFragment";
    private static String IMAGE_CACHE_DIR;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    StoreDatabaseLists storeDatabase;
    SQLiteDatabase sqdbWrite;
    SQLiteDatabase sqdbRead;
    public static final String MyPrefs = "MyPrefs2";
    SharedPreferences sp;
    String tableName;
    int tag;
    TextView textViewTitle;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.list_activity);

        final Intent intent = getIntent();
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        backButton = (ImageButton) findViewById(R.id.backButton);
        textViewTitle.setText(intent.getStringExtra("name"));
        IMAGE_CACHE_DIR = intent.getStringExtra("name");
        tableName = intent.getStringExtra("sqlName");
        tag = intent.getIntExtra("tag", 0);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sp = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, mImageThumbSize);
//        mImageFetcher.setLoadingImage(R.drawable.loading);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);


        listView = (ExpandableHeightListView) findViewById(R.id.listView);
        gridView = (ExpandableHeightGridView) findViewById(R.id.gridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        gson = new Gson();
        //map lists
        parametersList = new HashMap<String, Object>();
        arrayLists = new ArrayList<AttributesList>();
        arrayListsGrid = new ArrayList<AttributesList>();
        Log.i("OBJECT_ID=", intent.getStringExtra("objectId"));
        parametersList.put("category_id", intent.getStringExtra("objectId"));

        storeDatabase = new StoreDatabaseLists(this);
        sqdbWrite = storeDatabase.getWritableDatabase();
        sqdbRead = storeDatabase.getReadableDatabase();
//        String queryTop = "select top 20 percent * from "+tableName+" order by RANDOM()";
//        Cursor cursor = sqdbRead.query(tableName+" Order BY RANDOM() LIMIT 20", new String[]
        Cursor cursor = sqdbRead.query(tableName, new String[]
                        {"name, desc, imageUrl, objectId"},
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );

//        if (!sp.getBoolean("second", false)) {//true

        if (cursor.getCount() == 0) {
            Log.i("Null", "Cursor_NULL");
            Toast.makeText(getApplicationContext(), "NULL", Toast.LENGTH_SHORT).show();
            if (isOnline()) {
//                storeDatabase.cleanTable(sqdbRead);
//                mImageFetcher.clearCache();
                ParseCloud.callFunctionInBackground("lists", parametersList, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object response, ParseException e) {
                        if (e == null) {
//                    arrayLists.clear();
                            String json = gson.toJson(response);
                            Log.e("INFO", json);
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                Log.i("SIZE ARRAY JSON", jsonArray.length() + "");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONObject estimatedData = jsonObject.getJSONObject("estimatedData");
                                    JSONObject jsonImage = estimatedData.getJSONObject("image");
                                    String imageUrl = jsonImage.getString("url");
                                    String name = estimatedData.getString("name");
                                    String desc = estimatedData.getString("short_description");
                                    String objectId = jsonObject.getString("objectId");
                                    arrayLists.add(new AttributesList(name, desc, 0, 0, imageUrl, objectId));
                                    String insertQuery = "INSERT INTO " + tableName + " (name, desc, imageUrl, objectId)" +
                                            "VALUES ('" + name + "','" + desc + "','" + imageUrl + "','" + objectId + "')";
                                    sqdbWrite.execSQL(insertQuery);
                                    Log.i("Before 20", name);
                                    if (i >= 15) {
                                        for (int j = i + 1; j < jsonArray.length(); j++) {
                                            jsonObject = jsonArray.getJSONObject(j);
                                            estimatedData = jsonObject.getJSONObject("estimatedData");
                                            jsonImage = estimatedData.getJSONObject("image");
                                            imageUrl = jsonImage.getString("url");
                                            name = estimatedData.getString("name");
                                            desc = estimatedData.getString("short_description");
                                            objectId = jsonObject.getString("objectId");
//                                    order = estimatedData.getInt("order");
//                                    rating = estimatedData.getInt("rating");
                                            Log.i("After 20", name);
                                            arrayListsGrid.add(new AttributesList(name, desc, 0, 0, imageUrl, objectId));
                                            String insertQuery2 = "INSERT INTO " + tableName + " (name, desc, imageUrl, objectId)" +
                                                    "VALUES ('" + name + "','" + desc + "','" + imageUrl + "','" + objectId + "')";
                                            sqdbWrite.execSQL(insertQuery2);
                                        }
                                        break;
                                    }
                                }
                                progressBar.setVisibility(View.GONE);
//                            //List
                                adapter = new AdapterList(ListActivity.this, arrayLists);
                                listView.setAdapter(adapter);
                                listView.setExpanded(true);
                                //Grid
                                adapterGrid = new AdapterGridServices(ListActivity.this, arrayListsGrid);
                                gridView.setAdapter(adapterGrid);
                                gridView.setExpanded(true);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putBoolean("second", true);
                                editor.commit();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            Log.e("Error", e.toString());
                        }
                    }
                });
            } else {
//                Toast.makeText(getApplicationContext(), "no connection", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i("Null", "Cursor_NOT_NULL");
            Toast.makeText(getApplicationContext(), "NOT-NULL", Toast.LENGTH_SHORT).show();
            int i = 0;
            while (cursor.moveToNext()) {
                i++;
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"));
                String objectId = cursor.getString(cursor.getColumnIndex("objectId"));
                String desc = cursor.getString(cursor.getColumnIndex("desc"));
                arrayLists.add(new AttributesList(name, desc, 0, 0, imageUrl, objectId));
                if (i >= 20) {
                    while (cursor.moveToNext()) {
                        String name2 = cursor.getString(cursor.getColumnIndex("name"));
                        String imageUrl2 = cursor.getString(cursor.getColumnIndex("imageUrl"));
                        String objectId2 = cursor.getString(cursor.getColumnIndex("objectId"));
                        String desc2 = cursor.getString(cursor.getColumnIndex("desc"));
                        arrayListsGrid.add(new AttributesList(name2, desc2, 0, 0, imageUrl2, objectId2));
                    }
                }
            }

            progressBar.setVisibility(View.GONE);
            adapter = new AdapterList(getApplicationContext(), arrayLists);
            listView.setAdapter(adapter);
            listView.setExpanded(true);
            //Grid
            adapterGrid = new AdapterGridServices(ListActivity.this, arrayListsGrid);
            gridView.setAdapter(adapterGrid);
            gridView.setExpanded(true);
            cursor.close();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                Intent intent1 = new Intent(ListActivity.this, Information.class);
                                                intent1.putExtra("objectId", arrayLists.get(i).getObjectId());
                                                intent1.putExtra("name", arrayLists.get(i).getName());
                                                intent1.putExtra("pos", i);
                                                intent1.putExtra("tag", tag);
                                                startActivity(intent1);
                                            }
                                        }

        );
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                Intent intent1 = new Intent(ListActivity.this, Information.class);
                                                intent1.putExtra("objectId", arrayListsGrid.get(i).getObjectId());
                                                intent1.putExtra("name", arrayListsGrid.get(i).getName());
                                                intent1.putExtra("pos", 15 + i);
                                                intent1.putExtra("tag", tag);
                                                startActivity(intent1);
                                            }
                                        }

        );
    }


    public class AdapterList extends BaseAdapter {
        ArrayList<AttributesList> arrayLists;
        LayoutInflater inflater;
        Context context;
        StoreDatabase colordb;
        SQLiteDatabase sqdb;

        public AdapterList(Context context, ArrayList<AttributesList> arrayLists) {
            this.context = context;
            this.arrayLists = arrayLists;
            colordb = new StoreDatabase(context);
            sqdb = colordb.getWritableDatabase();
            inflater = LayoutInflater.from(context);
            Collections.shuffle(arrayLists);
        }

        @Override
        public int getCount() {
            return arrayLists.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder view;
            if (convertView == null) {
                view = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_lists, null);
                view.name = (TextView) convertView.findViewById(R.id.textViewName);
                view.desc = (TextView) convertView.findViewById(R.id.textViewDesc);
                view.imageView = (ImageView) convertView.findViewById(R.id.imageViewLists);
                view.list_bg = (RelativeLayout) convertView.findViewById(R.id.list_bg);
                view.pb = (ProgressBar) convertView.findViewById(R.id.progressBarList);
                convertView.setTag(view);
            } else {
                view = (ViewHolder) convertView.getTag();
            }

            view.name.setText(arrayLists.get(position).getName());
            view.desc.setText(arrayLists.get(position).getShort_description());
//            mImageFetcher.loadImage(arrayLists.get(position).getUrlImage(), view.imageView);
//            if(view.imageView.isShown()) {
//                view.pb.setVisibility(View.GONE);
//            }
//            Picasso.with(context).invalidate(arrayLists.get(position).getUrlImage());
            Picasso.with(context)
                    .load(arrayLists.get(position).getUrlImage())
                    .into(view.imageView, new Callback() {

                        @Override
                        public void onSuccess() {
                            view.pb.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });


            return convertView;
        }

        public class ViewHolder {
            public TextView name;
            public TextView desc;
            public ImageView imageView;
            RelativeLayout list_bg;
            ProgressBar pb;
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        mImageFetcher.setExitTasksEarly(false);
//        _adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
}

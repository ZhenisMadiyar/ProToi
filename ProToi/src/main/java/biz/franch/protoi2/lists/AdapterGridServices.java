package biz.franch.protoi2.lists;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import biz.franch.protoi2.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 24.06.2015.
 */
public class AdapterGridServices extends BaseAdapter{
    ArrayList<AttributesList> arrayLists;
    LayoutInflater inflater;
    Context context;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;

    public AdapterGridServices(Context context, ArrayList<AttributesList> arrayLists) {
        this.context = context;
        this.arrayLists = arrayLists;
        inflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        if(convertView==null) {
            view = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lists_view_grid, null);
            view.imageView = (CircleImageView) convertView.findViewById(R.id.imageViewGrid);
            convertView.setTag(view);
        }
        else {
            view = (ViewHolder) convertView.getTag();
        }
//        ImageLoader.getInstance().displayImage(arrayLists.get(position).getUrlImage(), view.imageView, options, animateFirstListener);
        Picasso.with(context)
                .load(arrayLists.get(position).getUrlImage())
                .into(view.imageView);
        return convertView;
    }

    public class ViewHolder {
        public CircleImageView imageView;
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

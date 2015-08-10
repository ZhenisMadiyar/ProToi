package biz.franch.protoi2.lists;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import biz.franch.protoi2.R;
import biz.franch.protoi2.database.StoreDatabase;

/**
 * Created by Admin on 25.06.2015.
 */
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

        ViewHolder view;
        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_lists, null);
            view.name = (TextView) convertView.findViewById(R.id.textViewName);
            view.desc = (TextView) convertView.findViewById(R.id.textViewDesc);
            view.imageView = (ImageView) convertView.findViewById(R.id.imageViewLists);
            view.list_bg = (RelativeLayout) convertView.findViewById(R.id.list_bg);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        view.name.setText(arrayLists.get(position).getName());
        view.desc.setText(arrayLists.get(position).getShort_description());
        Picasso.with(context)
                .load(arrayLists.get(position).getUrlImage())
                .placeholder(R.drawable.loading) // optional
                .error(R.drawable.ic_error)
                .into(view.imageView);


        return convertView;
    }

    public class ViewHolder {
        public TextView name;
        public TextView desc;
        public ImageView imageView;
        RelativeLayout list_bg;
    }
}

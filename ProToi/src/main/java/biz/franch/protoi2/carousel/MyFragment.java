package biz.franch.protoi2.carousel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import biz.franch.protoi2.R;
import biz.franch.protoi2.main_menu.Banner;

public class MyFragment extends Fragment {
	
	public static Fragment newInstance(Activity context, int pos, float scale, ArrayList<Banner> bannerList) {
		Bundle b = new Bundle();
		b.putInt("pos", pos);
		b.putFloat("scale", scale);
		b.putString("name", bannerList.get(pos).getName());
		b.putString("image", bannerList.get(pos).getImage());
		b.putString("objectUrl", bannerList.get(pos).getUrl());
		return Fragment.instantiate(context, MyFragment.class.getName(), b);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		
		RelativeLayout l = (RelativeLayout) inflater.inflate(R.layout.mf, container, false);


		int pos = this.getArguments().getInt("pos");
		TextView tv = (TextView) l.findViewById(R.id.text);
		ImageView iv = (ImageView) l.findViewById(R.id.imageViewBanner);
		String name = this.getArguments().getString("name");
		Picasso.with(getActivity())
				.load(getArguments().getString("image"))
				.into(iv);
		tv.setText(name);
		
		MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root);

		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getArguments().getString("objectUrl")));
				startActivity(browserIntent);
			}
		});
		float scale = this.getArguments().getFloat("scale");
		root.setScaleBoth(scale);
		
		return l;
	}
}

package easyway.Mobile.SiteRules;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import easyway.Mobile.util.ImageLoader;

/*
 * 本地文件adapter
 */
public class SRLocalAdapter extends BaseAdapter {
	private ArrayList<SiteRule> mLists;
	private LayoutInflater mInflater;
	private ImageLoader mImageDownLoader;

	public SRLocalAdapter(Context context, ArrayList<SiteRule> list) {
		super();
		this.mLists = list;
		mInflater = LayoutInflater.from(context);
		mImageDownLoader = new ImageLoader(context);
	}

	public int getCount() {
		if (mLists == null)
			return 0;
		else
			return mLists.size();
	}

	public Object getItem(int i) {
		if (mLists == null)
			return null;
		else
			return mLists.get(i);
	}

	public long getItemId(int i) {
		return i;
	}

	public void setData(ArrayList<SiteRule> list) {
		this.mLists = list;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
//		ViewHolder holder;
//		SiteRule obj = (SiteRule) getItem(position);
//		if (obj == null)
//			return null;
//
//		if (null == convertView) {
//			convertView = mInflater.inflate(R.layout.sr_local_item, null);
//			holder = new ViewHolder();
//			holder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
//			holder.txtFileName = (TextView) convertView
//					.findViewById(R.id.txtFileName);
//
//			LayoutParams paramsimg = holder.imgIcon.getLayoutParams();
//			paramsimg.width = Property.screenwidth / 4;
//			paramsimg.height = Property.screenwidth / 4;
//			holder.imgIcon.setLayoutParams(paramsimg);
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//
//		Bitmap bitmap = mImageDownLoader.showCacheBitmap(obj.Cover
//				.replaceAll("[^\\w]", ""));
//		if (bitmap != null) {
//			holder.imgIcon.setImageBitmap(bitmap);
//		} else {
//			holder.imgIcon.setImageResource(R.drawable.img_pdf);
//		}
//
//		holder.txtFileName.setText("种类：" + obj.Type + "," + obj.Title);
//
		return convertView;
	}

	class ViewHolder {
		ImageView imgIcon;
		TextView txtFileName;
	}
}

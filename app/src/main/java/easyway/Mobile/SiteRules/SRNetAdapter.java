package easyway.Mobile.SiteRules;

import java.util.ArrayList;

import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.util.ImageLoader;
import easyway.Mobile.util.ImageLoader.onImageLoaderListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/*
 * 站内规章（平台） Adapter
 */
public class SRNetAdapter extends BaseAdapter implements OnScrollListener {
	private LayoutInflater mInflater;
	private ArrayList<SiteRule> mList;
	private Context mContext;

	/**
	 * 记录是否刚打开程序，用于解决进入程序不滚动屏幕，不会下载图片的问题。
	 * 参考http://blog.csdn.net/guolin_blog/article/details/9526203#comments
	 */
	private boolean isFirstEnter = true;

	/**
	 * 一屏中第一个item的位置
	 */
	private int mFirstVisibleItem;

	/**
	 * 一屏中所有item的个数
	 */
	private int mVisibleItemCount;

	/**
	 * Image 下载器
	 */
	private ImageLoader mImageDownLoader;

	private IDataChange iDataChange;

	public SRNetAdapter(Context context, ArrayList<SiteRule> list) {
		super();
		mInflater = LayoutInflater.from(context);
		mList = list;
		mContext = context;
		mImageDownLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		if (mList == null)
			return 0;
		int size = mList.size();

		if (size % 2 == 0)
			return size / 2;
		else
			return size / 2 + 1;
	}

	@Override
	public Object getItem(int position) {
		if (mList == null)
			return null;
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(ArrayList<SiteRule> models) {
		mList = models;
		showImage(0, mList.size());
	}

	public void setIDataChange(IDataChange iDataChange) {
		this.iDataChange = iDataChange;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.sr_net_item, null);
			holder = new ViewHolder();
			holder.leftItem = (LinearLayout) convertView
					.findViewById(R.id.leftItem);
			holder.leftImgIcon = (ImageView) convertView
					.findViewById(R.id.leftImgIcon);
			holder.leftTxtFileName = (TextView) convertView
					.findViewById(R.id.leftTxtFileName);

			holder.rightItem = (LinearLayout) convertView
					.findViewById(R.id.rightItem);
			holder.rightImgIcon = (ImageView) convertView
					.findViewById(R.id.rightImgIcon);
			holder.rightTxtFileName = (TextView) convertView
					.findViewById(R.id.rightTxtFileName);

			convertView.setTag(holder);

			LayoutParams params = holder.leftItem.getLayoutParams();
			params.width = Property.screenwidth / 2;
			holder.leftItem.setLayoutParams(params);
			holder.rightItem.setLayoutParams(params);

			LayoutParams paramsimg = holder.leftImgIcon.getLayoutParams();
			paramsimg.width = Property.screenwidth / 4;
			paramsimg.height = Property.screenwidth / 4;
			holder.leftImgIcon.setLayoutParams(paramsimg);
			holder.rightImgIcon.setLayoutParams(paramsimg);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.leftItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (iDataChange != null)
					iDataChange.ItemClick(position * 2);
			}
		});

		holder.rightItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (iDataChange != null)
					iDataChange.ItemClick(position * 2 + 1);
			}
		});

		if (position * 2 < mList.size()) { // 左边区域
			SiteRule LeftObj = mList.get(position * 2);

			// holder.leftImgIcon.setTag(LeftObj.Cover);
			Bitmap bitmap = mImageDownLoader.showCacheBitmap(LeftObj.Cover
					.replaceAll("[^\\w]", ""));
			if (bitmap != null) {
				holder.leftImgIcon.setImageBitmap(bitmap);
			} else {
				holder.leftImgIcon.setImageResource(R.drawable.img_pdf);
			}

			switch (LeftObj.downloaded) {
			case SiteRule.DOWNLOAD_ED:
				holder.leftTxtFileName.setText(LeftObj.Title + "("
						+ mContext.getString(R.string.SR_DownloadEd) + ")");
				break;
			case SiteRule.DOWNLOAD_ING:
				holder.leftTxtFileName.setText(LeftObj.Title + "("
						+ (LeftObj.downloadpro * 100 / LeftObj.downloadmax)
						+ "%)");
				break;
			case SiteRule.DOWNLOAD_FAIL:
				holder.leftTxtFileName.setText(LeftObj.Title + "("
						+ mContext.getString(R.string.SR_DownloadFail) + ")");
				break;
			case SiteRule.DOWNLOAD_UN:
			default:
				holder.leftTxtFileName.setText(LeftObj.Title);
				break;
			}

		} else {
			holder.leftItem.setVisibility(View.INVISIBLE);
		}

		if (position * 2 + 1 < mList.size()) { // 右边区域
			holder.rightItem.setVisibility(View.VISIBLE);
			SiteRule RightObj = mList.get(position * 2 + 1);

			// holder.rightImgIcon.setTag(RightObj.Cover);
			Bitmap bitmap = mImageDownLoader.showCacheBitmap(RightObj.Cover
					.replaceAll("[^\\w]", ""));
			if (bitmap != null) {
				holder.rightImgIcon.setImageBitmap(bitmap);
			} else {
				holder.rightImgIcon.setImageResource(R.drawable.img_pdf);
			}

			switch (RightObj.downloaded) {
			case SiteRule.DOWNLOAD_ED:
				holder.rightTxtFileName.setText(RightObj.Title + "("
						+ mContext.getString(R.string.SR_DownloadEd) + ")");
				break;
			case SiteRule.DOWNLOAD_ING:
				holder.rightTxtFileName.setText(RightObj.Title + "("
						+ (RightObj.downloadpro * 100 / RightObj.downloadmax)
						+ "%)");
				break;
			case SiteRule.DOWNLOAD_FAIL:
				holder.rightTxtFileName.setText(RightObj.Title + "("
						+ mContext.getString(R.string.SR_DownloadFail) + ")");
				break;
			case SiteRule.DOWNLOAD_UN:
			default:
				holder.rightTxtFileName.setText(RightObj.Title);
				break;
			}

		} else { // 当总数为奇数时，最后一行右边区域无数据
			holder.rightItem.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	static class ViewHolder {
		LinearLayout leftItem;
		ImageView leftImgIcon;
		TextView leftTxtFileName;

		LinearLayout rightItem;
		ImageView rightImgIcon;
		TextView rightTxtFileName;
	}

	/**
	 * 显示当前屏幕的图片，先会去查找LruCache，LruCache没有就去sd卡或者手机目录查找，在没有就开启线程去下载
	 * 
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount) {
		// Bitmap bitmap = null;
		for (int i = firstVisibleItem * 2; i < (firstVisibleItem + visibleItemCount) * 2; i++) {
			if (i < mList.size()) {
				String mImageUrl = mList.get(i).Cover;
				mImageDownLoader.downloadImage(mImageUrl,
						new onImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								notifyDataSetInvalidated();
							}
						});
			}
		}
	}

	/**
	 * 取消下载任务
	 */
	public void cancelTask() {
		mImageDownLoader.cancelTask();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
			isFirstEnter = false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelTask();
		}
	}
}

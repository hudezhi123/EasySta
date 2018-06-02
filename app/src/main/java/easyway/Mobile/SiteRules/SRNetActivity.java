package easyway.Mobile.SiteRules;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import easyway.Mobile.ActivityEx;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.DownloadHelper;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.MD5;

/*
 * 站内规章（平台）
 */
public class SRNetActivity extends ActivityEx {
	private ArrayList<SiteRule> mRules = new ArrayList<SiteRule>();
	private int startIndex = 0;
	private final int limit = 20;
	private long totalItems = 0;
	private String mtype = "";
	private String mkey = "";
	
	MyFileAdapter adapter;
	ArrayList<String> typeList;
	Map<String, ArrayList<SiteRule>> dataList;
	Activity act;
	
	private ExpandableListView mExListView;
	
	private SearchReceiver receiver;
	
	private final int MSG_GETDATA_FAIL = 0;
	private final int MSG_GETDATA_SUCCEED = 1;
	private final int MSG_RECEIVE_BROADCAST = 2;
	private final int MSG_DATA_CHANGE = 3;

	@SuppressLint("HandlerLeak")
	private Handler myhandle = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			closeProgressDialog();

			switch (msg.what) {
			case MSG_GETDATA_FAIL:
				showToast(errMsg);
				break;
			case MSG_GETDATA_SUCCEED:
				if (mRules == null)
					mRules = new ArrayList<SiteRule>();

				if (startIndex == 0)
					mRules.clear();

				mRules.addAll((ArrayList<SiteRule>) msg.obj);
				typeList = new ArrayList<String>();
				dataList = new HashMap<String, ArrayList<SiteRule>>();
				
				int t = mRules.size();
				for(int i = 0; i < t; i++){
					SiteRule s = mRules.get(i);
					boolean ok = typeList.contains(s.Type);
					if(!ok){
						typeList.add(s.Type);
					}
				}
				int k = typeList.size();
				for(int i = 0; i < k; i++){
					String temp = typeList.get(i);
					ArrayList<SiteRule> temp1 = new ArrayList<SiteRule>();
					for(int l = 0; l < t; l++){
						SiteRule s = mRules.get(l);
						if(s.Type.equals(temp)){
							temp1.add(s);
						}
					}
					dataList.put(temp, temp1);
				}
				
				adapter = new MyFileAdapter(typeList, dataList, act);
				mExListView.setAdapter(adapter);
				mExListView.setOnChildClickListener(new OnChildClickListener() {
					
					@Override
					public boolean onChildClick(ExpandableListView parent, View v,
							int groupPosition, int childPosition, long id) {
						
						final SiteRule child = (SiteRule)adapter.getChild(groupPosition, childPosition);
						
						if (child.downloaded == SiteRule.DOWNLOAD_UN
								|| child.downloaded == SiteRule.DOWNLOAD_FAIL) {
							final String url = child.FileName;
							if (url.equals("")) {
								showToast(R.string.SR_FileNotExists);
							}
							
							final Handler myHandler = new Handler() {
								public void handleMessage(Message msg) {

									switch (msg.what) {
									case 0:
										DataSet(url, SiteRule.DOWNLOAD_ING, 0, Integer.valueOf(msg.obj.toString()));
										break;
									case 1:
										DataSet(url, SiteRule.DOWNLOAD_ING, Integer.valueOf(msg.obj.toString()), 0);
										break;
									case  2:
										int state = (Integer) msg.obj;
										if (state == -1) {
											Toast.makeText(SRNetActivity.this, "文件下载失败", 0).show();
											DataSet(url, SiteRule.DOWNLOAD_FAIL, 0, 0);
										} else if (state == 0) {
											Toast.makeText(SRNetActivity.this, "文件下载成功", 0).show();
											DataSet(url, SiteRule.DOWNLOAD_ED, 0, 0);
										} else if (state == 1) {
											Toast.makeText(SRNetActivity.this, "文件已存在", 0).show();
											DataSet(url, SiteRule.DOWNLOAD_ED, 0, 0);
										}
										
										break;
									default:
										break;
									}
									super.handleMessage(msg);
								}
							};
							
							new Thread() {
								public void run() {
									Looper.prepare();
									DownloadFile(url,myHandler,child.Type);
									Looper.loop();
								}
							}.start();
						} else if (child.downloaded == SiteRule.DOWNLOAD_ED) {
							String path = SRNetActivity.this
									.getString(R.string.config_doc_dir)
									+ DownloadHelper.GetRemoteFileName(child.FileName);

							File file = new File(path);
							if (file.exists()) {
								LogUtil.i("MD5:" + MD5.md5sum(path));
								if (MD5.md5sum(path).equalsIgnoreCase(child.MD5)) {
//									showDocument(file); // 打开文件
								} else {
									showToast(R.string.SR_FileNotSafe);	// 文件完整性验证失败
								}
							}
						}
							
						return true;
					}
				});
				break;
			case MSG_RECEIVE_BROADCAST:
				startIndex = 0;
				getData();
				break;
			case MSG_DATA_CHANGE:
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sr_net);
		act = this;
		initView();
		startIndex = 0;
		getData();
	}

	public void onPause() {
		super.onPause();

		if (receiver != null) {
			try {
				unregisterReceiver(receiver);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	
	@Override
	protected void onDestroy () {
		super.onDestroy();
		
	}
	
	private void initView() {
		
		mExListView = (ExpandableListView)findViewById(R.id.mlExpandableListView);
		
	}
	
	private void DataSet(String url, int state, int progress, int max) {
		if (url == null)
			return;
		
		for (SiteRule rule : mRules) {
			if (rule.FileName.equals(url)) {
				rule.downloaded = state;
				rule.downloadpro = progress;
				if (max != 0)
					rule.downloadmax = max;
				myhandle.sendEmptyMessage(MSG_DATA_CHANGE);
				
				if (state == SiteRule.DOWNLOAD_ED && !SiteRule.CheckExists(SRNetActivity.this, rule)) {
					SiteRule.Insert(SRNetActivity.this, rule);		// 插入数据
				}
				
				break;
			}
		}
	}

	// 下载文件
	private void DownloadFile(String url,Handler handler,String type) {
		LogUtil.e("DownloadFile start!!!");
		DownloadHelper downloadHelper = new DownloadHelper();
		downloadHelper.handler=handler;
		String fileShortName = DownloadHelper.GetRemoteFileName(url);
		String localPath = SRNetActivity.this.getString(R.string.config_doc_dir);
		//保存路径加上种类。
		localPath += type + "/";
		int dwResult = downloadHelper.DownFile(url, localPath,
				fileShortName);
		
		Message msg = new Message();
		msg.what = 2;
		msg.obj = dwResult;
		handler.sendMessage(msg);
			
		if (downloadHelper.StopDownload) {
			LogUtil.e("downloadHelper.StopDownload!!!!");
			return;
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		regReceiver();
	}

	private void regReceiver() {
		receiver = new SearchReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SRTabActivity.ACTION_SITERULE_SEARCH);
		registerReceiver(receiver, filter);
	}
	
	// 获取数据
	private void getData() {
		showProgressDialog(R.string.GettingData);
		new Thread() {
			public void run() {
				GetSiteRules();
				mkey = "";
				mtype = "";
			}
		}.start();
	}
		
	// 下载文件。
	private void GetSiteRules() {
		HashMap<String, String> parmValues = new HashMap<String, String>();
		parmValues.put("sessionId", Property.SessionId);
		parmValues.put("keyword", mkey);
		parmValues.put("rType", mtype);
		parmValues.put("limit", String.valueOf(limit));
		parmValues.put("start", String.valueOf(startIndex));

		String methodPath = Constant.MP_REGULATIONS;
		String methodName = Constant.MN_GET_REGULATIONSALL;
		WebServiceManager webServiceManager = new WebServiceManager(
				getApplicationContext(), methodName, parmValues);
		String result = webServiceManager.OpenConnect(methodPath);

		if (result == null || result.equals("")) {
			errMsg = getString(R.string.exp_getdata);
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			return;
		}

		int Code = JsonUtil.GetJsonInt(result, "Code");
		switch (Code) {
		case Constant.NORMAL:
			ArrayList<SiteRule> list = SiteRule.ParseFromString(result);
			totalItems = JsonUtil.GetJsonLong(result, "total");
			
			for (SiteRule rule : list) {
				if (SiteRule.CheckExists(SRNetActivity.this, rule))
					rule.downloaded = SiteRule.DOWNLOAD_ED;
				else
					rule.downloaded = SiteRule.DOWNLOAD_UN;
			}
			
			Message message = new Message();
			message.what = MSG_GETDATA_SUCCEED;
			message.obj = list;
			myhandle.sendMessage(message);
			break;
		case Constant.EXCEPTION:
		default:
			errMsg = JsonUtil.GetJsonString(result, "Msg");
			myhandle.sendEmptyMessage(MSG_GETDATA_FAIL);
			break;
		}
	}

	// 自定义一个广播接收器
	public class SearchReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			mtype = bundle.getString(SRTabActivity.EXTRA_TYPE);
			mkey = bundle.getString(SRTabActivity.EXTRA_KEY);

			myhandle.sendEmptyMessage(MSG_RECEIVE_BROADCAST);
		}
	}
	
		
	
}

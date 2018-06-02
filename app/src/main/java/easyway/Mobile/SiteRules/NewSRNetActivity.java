package easyway.Mobile.SiteRules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import easyway.Mobile.ActivityEx;
import easyway.Mobile.Net.Constant;
import easyway.Mobile.Net.DownloadHelper;
import easyway.Mobile.Net.WebServiceManager;
import easyway.Mobile.Property;
import easyway.Mobile.R;
import easyway.Mobile.treeview.ChildNodeBinder;
import easyway.Mobile.treeview.ChildTab;
import easyway.Mobile.treeview.ParentNodeBinder;
import easyway.Mobile.treeview.RuleTypeTreeUtil;
import easyway.Mobile.treeview.databean.ChildRuleFile;
import easyway.Mobile.treeview.recyclertreeview_lib.LayoutItemType;
import easyway.Mobile.treeview.recyclertreeview_lib.TreeNode;
import easyway.Mobile.treeview.recyclertreeview_lib.TreeViewAdapter;
import easyway.Mobile.util.FileUtils;
import easyway.Mobile.util.JsonUtil;
import easyway.Mobile.util.LogUtil;
import easyway.Mobile.util.MD5;


public class NewSRNetActivity extends ActivityEx {

    private String mtype = "";
    private String mkey = "";
    private final int limit = 20;
    private long totalItems = 0;
    private int startIndex = 0;
    private final int MSG_GETDATA_FAIL = 0;
    private final int MSG_GETDATA_SUCCEED = 1;
    private final int MSG_RECEIVE_BROADCAST = 2;
    private final int MSG_DATA_CHANGE = 3;
    private SearchReceiver receiver;
    private RecyclerView recyclerView;
    private TreeViewAdapter adapter;
    private List<ChildRuleFile> childRuleFileList = new ArrayList<ChildRuleFile>();
    private List<TreeNode> treeNodes;

    private Handler myhandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            switch (msg.what) {
                case MSG_GETDATA_FAIL:
                    showToast(errMsg);
                    break;
                case MSG_GETDATA_SUCCEED:
                    List<ChildRuleFile> childList = (List<ChildRuleFile>) msg.obj;
                    SharedPreferences sharedPreferences = getSharedPreferences(SRTabActivity.SHARE_TYPE_FILE_NAME, Context.MODE_PRIVATE);
                    String jsonResult = sharedPreferences.getString(SRTabActivity.SHARE_TYPE_KEY, "");
                    if (!TextUtils.isEmpty(jsonResult)) {
                        HashMap<String, ChildRuleFile> hashChild = new HashMap<String, ChildRuleFile>();
                        for (int i = 0; i < childList.size(); i++) {
                            ChildRuleFile ruleFile = childList.get(i);
                            hashChild.put(ruleFile.RFileName, ruleFile);
                        }
                        if (Property.StationCode.equals("SYB")) {
                            treeNodes = RuleTypeTreeUtil.BuildTree(jsonResult, hashChild);
                        } else {
                            treeNodes = RuleTypeTreeUtil.BuildTree(jsonResult, hashChild, 1);
                        }
                        adapter = new TreeViewAdapter(treeNodes, Arrays.asList(new ChildNodeBinder(), new ParentNodeBinder()));
                        recyclerView.setAdapter(adapter);
                        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
                            @Override
                            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                                if (!node.isLeaf()) {
                                    onToggle(!node.isExpand(), holder);
                                } else {
                                    if (node.getContent().getType() == LayoutItemType.TYPE_PARENT_TYPE) {

                                    } else {

                                        final ChildRuleFile childRuleFile = ((ChildTab) node.getContent()).childRuleFile;
                                        if (childRuleFile.downloaded == ChildRuleFile.DOWNLOAD_UN || childRuleFile.downloaded == ChildRuleFile.DOWNLOAD_FAIL) {
                                            String urlStr = "";
                                            try {
                                                urlStr = java.net.URLDecoder.decode(childRuleFile.RFileName, "utf-8");
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                            final String url = childRuleFile.RFileName;
                                            if (url.equals("")) {
                                                showToast(R.string.SR_FileNotExists);
                                            }
                                            final Handler myHandler = new Handler() {
                                                public void handleMessage(Message msg) {

                                                    switch (msg.what) {
                                                        case 0:
                                                            DataSet(url, ChildRuleFile.DOWNLOAD_ING, 0, Integer.valueOf(msg.obj.toString()));
                                                            break;
                                                        case 1:
                                                            DataSet(url, ChildRuleFile.DOWNLOAD_ING, Integer.valueOf(msg.obj.toString()), 0);
                                                            break;
                                                        case 2:
                                                            try {
                                                                int state = msg.arg1;
                                                                if (state == -1) {
                                                                    Toast.makeText(NewSRNetActivity.this, "文件下载失败", Toast.LENGTH_SHORT).show();
                                                                    DataSet(url, ChildRuleFile.DOWNLOAD_FAIL, 0, 0);
                                                                } else if (state == 0) {
                                                                    Toast.makeText(NewSRNetActivity.this, "文件下载成功", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = FileUtils.openFileIntent((String) msg.obj);
                                                                    if (intent != null) {
                                                                        startActivity(intent);
                                                                    }
                                                                    DataSet(url, ChildRuleFile.DOWNLOAD_ED, 0, 0);
                                                                } else if (state == 1) {
                                                                    Toast.makeText(NewSRNetActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = FileUtils.openFileIntent((String) msg.obj);
                                                                    if (intent != null) {
                                                                        if (intent != null) {
                                                                            startActivity(intent);
                                                                        }
                                                                    }
                                                                    DataSet(url, ChildRuleFile.DOWNLOAD_ED, 0, 0);
                                                                }
                                                            } catch (Exception e) {
                                                                Toast.makeText(NewSRNetActivity.this, "请安装阅读软件！", Toast.LENGTH_SHORT).show();
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
                                                    DownloadFile(url, myHandler, childRuleFile.RType, childRuleFile.RTitle);
                                                    Looper.loop();
                                                }
                                            }.start();

                                        } else if (childRuleFile.downloaded == ChildRuleFile.DOWNLOAD_ED) {
                                            String path = NewSRNetActivity.this
                                                    .getString(R.string.config_doc_dir)
                                                    + DownloadHelper.GetRemoteFileName(childRuleFile.RFileName);

                                            File file = new File(path);
                                            if (file.exists()) {
                                                LogUtil.i("MD5:" + MD5.md5sum(path));
                                                if (MD5.md5sum(path).equalsIgnoreCase(childRuleFile.MD5)) {
//									showDocument(file); // 打开文件
                                                } else {
                                                    showToast(R.string.SR_FileNotSafe);    // 文件完整性验证失败
                                                }
                                            }
                                        }
                                    }
                                }
                                return false;
                            }

                            @Override
                            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                                ParentNodeBinder.ViewHolder parentHolder = (ParentNodeBinder.ViewHolder) holder;
                                final ImageView ivArrow = parentHolder.getIvArrow();
                                if (isExpand) {
                                    ivArrow.setRotation(90);
                                } else {
                                    ivArrow.setRotation(0);
                                }
                            }
                        });
//                        recyclerView.setAdapter(adapter);
                    }
                    break;
                case MSG_RECEIVE_BROADCAST:
                    startIndex = 0;
                    getData();
                    break;
                case MSG_DATA_CHANGE:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_srnet);
        init();
        getData();
    }

    private void init() {
        treeNodes = new ArrayList<TreeNode>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_srnet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    // 下载文件。
    private void GetSiteRules() {
        HashMap<String, String> parmValues = new HashMap<String, String>();
        parmValues.put("sessionId", Property.SessionId);
        parmValues.put("keyword", mkey);
        parmValues.put("rType", mtype);
        parmValues.put("limit", String.valueOf(1000));
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

                List<ChildRuleFile> list = ChildRuleFile.parseRuleFile(result);
                totalItems = JsonUtil.GetJsonLong(result, "total");
                for (ChildRuleFile rule : list) {
                    if (ChildRuleFile.CheckExists(NewSRNetActivity.this, rule))
                        rule.downloaded = ChildRuleFile.DOWNLOAD_ED;
                    else
                        rule.downloaded = ChildRuleFile.DOWNLOAD_UN;
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


    // 下载文件
    private void DownloadFile(String url, Handler handler, String type, String fileShortName) {
        DownloadHelper downloadHelper = new DownloadHelper();
        downloadHelper.handler = handler;
        if (TextUtils.isEmpty(fileShortName)) {
            fileShortName = DownloadHelper.GetRemoteFileName(url);
        }
        String localPath = NewSRNetActivity.this.getString(R.string.config_doc_dir);
        //保存路径加上种类。
        localPath += type + "/";
        String affix = url.substring(url.lastIndexOf("."));
        fileShortName = fileShortName + affix;
        int dwResult = downloadHelper.DownFile(url, localPath,
                fileShortName);
        String filePath = localPath + fileShortName;
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = dwResult;
        msg.obj = filePath;
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

    private void DataSet(String url, int state, int progress, int max) {
        if (url == null)
            return;

        for (ChildRuleFile rule : childRuleFileList) {

            if (rule.RFileName.equals(url)) {
                rule.downloaded = state;
                rule.downloadpro = progress;
                if (max != 0)
                    rule.downloadmax = max;
                myhandle.sendEmptyMessage(MSG_DATA_CHANGE);

                if (state == ChildRuleFile.DOWNLOAD_ED && !ChildRuleFile.CheckExists(this, rule)) {
                    ChildRuleFile.Insert(this, rule);        // 插入数据
                }

                break;
            }
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

package easyway.Mobile.SiteRules;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import easyway.Mobile.ActivityEx;
import easyway.Mobile.R;
import easyway.Mobile.util.FileUtils;


/*
 * 本地文件（已下载）
 */
public class SRLocalActivity extends ActivityEx {
    private List<DownloadedFile> downloadedFileList;
    private ListView listView;
    private Button btnRefresh;
    private NewLocalFileAdapter adapter;
    private final static String ROOT = "ZWT/doc";
    private boolean isOncreate = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeProgressDialog();
            if (msg.what == 1) {
                adapter.setDataList(downloadedFileList);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sr_local);
        isOncreate = true;
        initView();
        getData();
    }

    private void initView() {
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        downloadedFileList = new ArrayList<DownloadedFile>();
        listView = (ListView) findViewById(R.id.listView_sr_local);
        adapter = new NewLocalFileAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DownloadedFile downloadFile = adapter.getItem(i);
                try {
                    Intent intent = FileUtils.openFileIntent(downloadFile.getFilePath());
                    if (intent != null) {
                        if (intent != null) {
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(SRLocalActivity.this, "请安装阅读软件！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(SRLocalActivity.this, "请安装阅读软件！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getData() {
        showProgressDialog(R.string.GettingData);
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadedFileList.clear();
                getFileList(getDownloadFileRootDir());
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private File getDownloadFileRootDir() {
        File baseDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            baseDir = Environment.getExternalStorageDirectory();
        }
        File filepath = new File(baseDir, ROOT);
        if (!filepath.exists()) {
            filepath.mkdirs();
        }
        return filepath;
    }

    private void getFileList(File rootFile) {
        if (rootFile.length() <= 0) {
            return;
        }
        File[] fileList = rootFile.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                getFileList(file);
            } else {
                DownloadedFile downloadFile = new DownloadedFile();
                String fileName = file.getName();
                downloadFile.setFileName(fileName);
                downloadFile.setFilePath(file.getAbsolutePath());
                downloadFile.setFileType(FileUtils.getFileType(fileName));
                downloadedFileList.add(downloadFile);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOncreate) {

        }
    }

    public void onClick(View view) {
        getData();
    }


}

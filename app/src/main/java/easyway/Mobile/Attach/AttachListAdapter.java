package easyway.Mobile.Attach;

import java.io.File;
import java.util.ArrayList;

import easyway.Mobile.R;
import easyway.Mobile.Net.DownloadHelper;
import easyway.Mobile.Net.OpenFileIntent;
import easyway.Mobile.util.CommonUtils;
import easyway.Mobile.util.LogUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

/*
 * 附件Adapter
 */
public class AttachListAdapter extends BaseAdapter {
    private ArrayList<String> list = null;
    private Context context;
    private DownloadHelper downloadHelper;
    private LayoutInflater infl = null;

    private IAudioPlay iAudioPlay;

    public void StopDownload() {
        if (downloadHelper != null)
            downloadHelper.StopDownload = true;

//		try {
//			if (mediaplayer != null && mediaplayer.isPlaying()) {
//				IntercomCtrl.open_intercom(context);
//				mediaplayer.reset();
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
    }

    public AttachListAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        this.infl = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (list == null)
            return 0;

        return list.size();
    }

    public CharSequence getItem(int position) {
        if (list == null)
            return null;

        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setIAudioPlay(IAudioPlay iAudioPlay) {
        this.iAudioPlay = iAudioPlay;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = infl.inflate(R.layout.attach_list_item, null);
        String url = getItem(position).toString();
        ProgressBar progressDownload = (ProgressBar) convertView
                .findViewById(R.id.progressDownload);
        Button labFileName = (Button) convertView
                .findViewById(R.id.labFileName);
        labFileName.setOnClickListener(showAttach(url, progressDownload));
        String fileName = CommonUtils.getFileNameFromPath(url);
        labFileName.setText(fileName);
        return convertView;
    }

    private void showErrMsg(String errMsg) {
        if (errMsg == null)
            return;
        if (errMsg.equals(""))
            return;
        AlertDialog.Builder builder = new Builder(context);
        builder.setIcon(R.drawable.exit);
        builder.setMessage(errMsg);
        builder.setTitle(R.string.Prompt);
        builder.setPositiveButton(R.string.OK, null);
        builder.create().show();
        errMsg = "";
    }

    private void DownloadFile(String url, Handler handler) {
        LogUtil.e("DownloadFile start!!!");
        downloadHelper = new DownloadHelper();
        downloadHelper.handler = handler;
        String fileShortName = DownloadHelper.GetRemoteFileName(url);
        String localPath = context.getString(R.string.config_attach_dir);

        int dwResult = downloadHelper.DownFile(url, localPath,
                fileShortName);
        if (downloadHelper.StopDownload) {
            LogUtil.e("downloadHelper.StopDownload!!!!");
            return;
        }


        String fileName = localPath + fileShortName;

        if (dwResult == -1) {
            showErrMsg(context.getString(R.string.exp_download));
        } else {
            File currentPath = new File(fileName);
            if (currentPath.isFile()) {
                Intent intent;
                if (OpenFileIntent
                        .checkEndsWithInStringArray(
                                fileName,
                                context.getResources().getStringArray(
                                        R.array.fileEndingImage))) {
                    intent = OpenFileIntent.getImageFileIntent(currentPath);
                    context.startActivity(intent);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingWebText))) {
                    intent = OpenFileIntent.getHtmlFileIntent(currentPath);
                    context.startActivity(intent);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingPackage))) {
                    intent = OpenFileIntent.getApkFileIntent(currentPath);
                    context.startActivity(intent);

                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingAudio))) {
                    playVoice(fileName);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingVideo))) {
                    intent = OpenFileIntent.getVideoFileIntent(currentPath);
                    context.startActivity(intent);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingText))) {
                    intent = OpenFileIntent.getTextFileIntent(currentPath);
                    context.startActivity(intent);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingPdf))) {
                    intent = OpenFileIntent.getPdfFileIntent(currentPath);
                    context.startActivity(intent);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingWord))) {
                    intent = OpenFileIntent.getWordFileIntent(currentPath);
                    context.startActivity(intent);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingExcel))) {
                    intent = OpenFileIntent.getExcelFileIntent(currentPath);
                    context.startActivity(intent);
                } else if (OpenFileIntent.checkEndsWithInStringArray(
                        fileName,
                        context.getResources().getStringArray(
                                R.array.fileEndingPPT))) {
                    intent = OpenFileIntent.getPPTFileIntent(currentPath);
                    context.startActivity(intent);
                } else {
                    showErrMsg(context.getString(R.string.exp_open));
                }
            } else {
                showErrMsg(context.getString(R.string.exp_not_file));
            }
        }
    }

    private void playVoice(String voicePath) {
        if (iAudioPlay != null)
            iAudioPlay.OnPlayVoice(voicePath);
//		if (!voicePath.equals("")) {
//			try {
//				IntercomCtrl.close_intercom(context);
//				mediaplayer.reset();
//				mediaplayer.setDataSource(voicePath);
//				mediaplayer.prepare();
//				mediaplayer.start();
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
    }

    private OnClickListener showAttach(final String url, final ProgressBar progressDownload) {
        return new OnClickListener() {

            @SuppressLint("HandlerLeak")
            public void onClick(View v) {
                final Handler myHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 0:
                                int max = Integer.valueOf(msg.obj.toString());
                                progressDownload.setVisibility(View.VISIBLE);
                                progressDownload.setMax(max);
                                break;
                            case 1:
                                int progress = Integer.valueOf(msg.obj.toString());
                                progressDownload.setProgress(progress);
                                if (progress >= progressDownload.getMax()) {
                                    progressDownload.setVisibility(View.GONE);
                                } else {
                                    progressDownload.setVisibility(View.VISIBLE);
                                }
                                break;
                            default:
                                break;
                        }
                        super.handleMessage(msg);
                    }
                };

                Thread thread = new Thread(null, new Runnable() {

                    @Override
                    public void run() {
                        Looper.prepare();
                        DownloadFile(url, myHandler);
                        Looper.loop();
                    }
                }, "DownloadFile");
                thread.start();
            }
        };
    }

}

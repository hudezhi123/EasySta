package easyway.Mobile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import easyway.Mobile.Application.ExitApplication;
import easyway.Mobile.Data.Permission;
import easyway.Mobile.util.BitmapUtils;
import easyway.Mobile.util.CommonFunc;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/*
 * 主界面Adapter
 */

public class MainFrameworkAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<Permission> list = null;
    private HashMap<String, Integer> listFuncTotal = null;
    private Handler handler = null;
    private int width, height;
    private LayoutInflater mInflater;

    public MainFrameworkAdapter(Context context, ArrayList<Permission> list,
                                Handler handler, int width, int height,
                                HashMap<String, Integer> listFuncTotal) {
        this.context = context;
        this.list = list;
        this.handler = handler;
        this.width = width;
        this.height = height;
        this.listFuncTotal = listFuncTotal;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    @Override
    public Permission getItem(int position) {
        if (getCount() == 0)
            return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 从assets目录获取图片
    private Bitmap getImageFromAssetsFile(String fileName) {
        // if(fileName!=null && !fileName.equals("easyway.Mobile")){
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);

            is.close();
        } catch (IOException e) {
        }
        return image;

    }

    // 保存文件
    private void saveBitmap2Local(byte[] bytes, String fileName) {
        if (bytes == null)
            return;
        try {
            FileOutputStream fout = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fout.write(bytes);
            fout.flush();
            fout.close();
        } catch (Exception ex) {
        }
    }

    // 使用本地图片
    private Bitmap GetFormLocal(String shortName) {
        Bitmap image = null;
        try {
            BitmapFactory.Options opts = new Options();
            opts.inJustDecodeBounds = true;
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;

            FileInputStream fis = context.openFileInput(shortName);
            if (fis != null) {
                BufferedInputStream bis = new BufferedInputStream(fis);
                image = BitmapFactory.decodeStream(bis);
                bis.close();
            }
            return image;
        } catch (Exception ex) {

        }
        return null;
    }

    // 从平台下载图片
    private Bitmap getImageFormServer(String url, String imageName) {
        Bitmap image = null;
        try {
            image = GetFormLocal(imageName);
            if (image != null) {
                return image;
            }

            BitmapFactory.Options opts = new Options();
            opts.inJustDecodeBounds = true;
            opts.inJustDecodeBounds = false;
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            BitmapUtils bitmapUtils = new BitmapUtils();
            byte[] bytes = bitmapUtils.GetHttpBitmap(CommonFunc
                    .GetServer(context) + url);

            if (bytes == null)
                return null;

            saveBitmap2Local(bytes, imageName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Permission permission = getItem(position);
        if (permission == null)
            return null;

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.permission_item, null);
            holder = new ViewHolder();
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.layout);
            holder.btnPermission = (ImageButton) convertView
                    .findViewById(R.id.btnPermission);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LayoutParams params = holder.layout.getLayoutParams();
        if (params != null) {
            params.width = width / 3;
        } else {
            params = new AbsListView.LayoutParams(width / 3, height / 4);
        }

        holder.layout.setLayoutParams(params);

        LayoutParams par = holder.btnPermission.getLayoutParams();
        if (par != null) {
            par.width = width / 4 - 10;
            par.height = height / 5 - 5;
        }
        holder.btnPermission.setLayoutParams(par);
        String funcCode = permission.FuncCode;

        Bitmap btImg = getImageFromAssetsFile("www/img/" + permission.ImgName);

        if (btImg == null) {
            btImg = getImageFormServer(permission.ImageUrl, permission.ImgName);
        }

        int total = 0;
        if (listFuncTotal.containsKey(funcCode)) {
            total = listFuncTotal.get(funcCode);
        }
        if (!permission.Title.equals("easyway.Mobile")) {
            if (btImg == null) {
                holder.btnPermission.setBackgroundResource(R.color.nothing);
                if (total > 0) {
//					holder.btnPermission.setText(permission.Title + "("
//							+ String.valueOf(1) + ")");
                } else {
//					holder.btnPermission.setText(permission.Title);
                }
                holder.btnPermission
                        .setBackgroundResource(android.R.drawable.btn_default);

            } else {
                Bitmap bt = zoomBitmap(btImg, width / 4, height / 5 - 15);
                if (total > 0) {
                    CommonFunc.AddNum2Img(bt, total);
                }
                holder.btnPermission.setImageBitmap(bt);
            }
            holder.btnPermission.setOnClickListener(sendMsgLis(funcCode));
        }

        return convertView;
    }

    // 缩放图片
    public Bitmap zoomBitmap(Bitmap org, int newWidth, int newHeight) {
        int width = org.getWidth();
        int height = org.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        int destWidth = newWidth;
        if (scaleWidth > scaleHeight) {
            scaleWidth = scaleHeight;
            destWidth = (int) (width * scaleWidth);
        }
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap result = Bitmap.createScaledBitmap(org, destWidth, newHeight,
                true);
        return result;
    }

    private OnClickListener sendMsgLis(final String funcCode) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message message = Message.obtain(handler, 1, funcCode);
                handler.sendMessage(message);
            }
        };
    }

    static class ViewHolder {
        LinearLayout layout;
        ImageButton btnPermission;
    }
}

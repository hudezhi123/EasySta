package easyway.Mobile.SiteRules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import easyway.Mobile.R;


/**
 * Created by boy on 2017/5/22.
 */

public class NewLocalFileAdapter extends BaseAdapter {
    private List<DownloadedFile> fileList;
    private Context context;

    public NewLocalFileAdapter(Context context) {
        this.context = context;
        fileList = new ArrayList<>();
    }

    public void setDataList(List<DownloadedFile> list) {
        this.fileList = list;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        fileList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public DownloadedFile getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sr_local_item, viewGroup, false);
            holder = new ViewHolder();
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon_file_type);
            holder.textName = (TextView) convertView.findViewById(R.id.text_file_name);
            holder.btnDelete = (Button) convertView.findViewById(R.id.btn_delete_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final DownloadedFile downloadedFile = fileList.get(position);
        holder.textName.setText(downloadedFile.getFileName());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDownloadFile(downloadedFile.getFilePath());
                removeItem(position);
            }
        });
        int type = downloadedFile.getFileType();
        switch (type) {
            case DownloadedFile.IMG:
                holder.imgIcon.setImageResource(R.drawable.jpg_file);
                break;
            case DownloadedFile.PDF:
                holder.imgIcon.setImageResource(R.drawable.pdf_file);
                break;
            case DownloadedFile.WORD:
                holder.imgIcon.setImageResource(R.drawable.word_file);
                break;
            case DownloadedFile.XLSX:
                holder.imgIcon.setImageResource(R.drawable.excel_file);
                break;
        }
        return convertView;
    }

    private boolean deleteDownloadFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    private static class ViewHolder {
        TextView textName;
        ImageView imgIcon;
        Button btnDelete;
    }
}

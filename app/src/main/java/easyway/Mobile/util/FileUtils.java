package easyway.Mobile.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by boy on 2018/1/18.
 */

public class FileUtils {

    private String SDPATH;

    public String getSDPATH() {
        return SDPATH;
    }

    public FileUtils(String savePath) {
        // 得到当前外部存储设备的目录( /SDCARD )
        SDPATH = Environment.getExternalStorageDirectory() + "/";
        if (savePath == null) {
            SDPATH = Environment.getExternalStorageDirectory() + "/";
        } else if (savePath == "") {
            SDPATH = Environment.getExternalStorageDirectory() + "/";
        } else {
            SDPATH = savePath;
        }
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public File createSDFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdirs();
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path
     * @param fileName
     * @param input
     * @return
     */
    public File write2SDFromInput(String path, String fileName, byte[] input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            file = createSDFile(path + fileName);
			/*
			 * output = new FileOutputStream(file); byte[] buffer = new
			 * byte[FILESIZE]; while((input.read(buffer)) != -1){
			 * output.write(buffer); } output.flush(); output.close();
			 */
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bos.write(input);
            bos.flush();
            bos.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            file.deleteOnExit();
        } finally {
            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    static class FileType {
        public static final int WORD = 1;
        public static final int XLSX = 2;
        public static final int PDF = 3;
        public static final int IMG = 4;
    }


    public static int getFileType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return 0;
        } else {
            if (fileName.endsWith("pdf")) {
                return FileType.PDF;
            } else if (fileName.endsWith("bmp") || fileName.endsWith("png") || fileName.endsWith("jpeg") || fileName.endsWith("jpg")) {
                return FileType.IMG;
            } else if (fileName.endsWith("doc") || fileName.endsWith("docx")) {
                return FileType.WORD;
            } else if (fileName.endsWith("xls") || fileName.endsWith("xlsx")) {
                return FileType.XLSX;
            } else {
                return -1;
            }
        }
    }


    /**
     * 打开文件
     *
     * @param filePath
     * @return
     */
    public static Intent openFileIntent(String filePath) {
        File file = new File(filePath);
        if ((file == null) || !file.exists() || file.isDirectory())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1,
                file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("xls") || end.equals("xlsx")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc") || end.equals("docx")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else {
            return null;
        }
    }


    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }
}

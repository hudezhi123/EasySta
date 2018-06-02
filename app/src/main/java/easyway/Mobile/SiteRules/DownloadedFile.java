package easyway.Mobile.SiteRules;

/**
 * Created by boy on 2017/5/22.
 */

public class DownloadedFile {
    public static final int WORD = 1;
    public static final int XLSX = 2;
    public static final int PDF = 3;
    public static final int IMG = 4;
    private String fileName;
    private String filePath;
    private int fileType;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}

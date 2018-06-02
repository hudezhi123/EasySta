package easyway.Mobile.Media;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class FileAccessEx implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int PIECE_LENGHT = 102400;
    RandomAccessFile oSavedFile;
    long nPos;

    public FileAccessEx() throws IOException {
        this("", 0);
    }

    public FileAccessEx(String sName, long nPos) throws IOException {
        oSavedFile = new RandomAccessFile(sName, "rw");// 创建一个随机访问文件类，可读写模式
        this.nPos = nPos;
        oSavedFile.seek(nPos);
    }

    public synchronized int write(byte[] b, int nStart, int nLen) {
        int n = -1;
        try {
            oSavedFile.write(b, nStart, nLen);
            n = nLen;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    // 每次读取102400字节
    public synchronized Detail getContent(long nStart) {
        Detail detail = new Detail();
        detail.b = new byte[PIECE_LENGHT];
        try {
            oSavedFile.seek(nStart);
            detail.length = oSavedFile.read(detail.b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return detail;
    }

    public class Detail {
        public byte[] b;
        public int length;
    }

    // 获取文件长度
    public long getFileLength() {
        Long length = 0l;
        try {
            length = oSavedFile.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return length;
    }
}

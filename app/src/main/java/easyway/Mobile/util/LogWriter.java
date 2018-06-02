package easyway.Mobile.util;

import android.annotation.SuppressLint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWriter {
	private static LogWriter mLogWriter;
	private static String mPath;
	private static Writer mWriter;
	private static SimpleDateFormat df;
	
	private LogWriter(String file_path) {
		mPath = file_path;
		mWriter = null;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static LogWriter open(String file_path) throws IOException {
		if (mLogWriter == null) {
			mLogWriter = new LogWriter(file_path);
		}
		
		mWriter = new BufferedWriter(new FileWriter(new File(mPath), true));
		df = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]: ");
		
		return mLogWriter;
	}
	
	public void close() throws IOException {
		mWriter.close();
	}
	
	public void print(String log) throws IOException {
		mWriter.write(df.format(new Date()));
		mWriter.write(log);
		mWriter.write("\n");
		mWriter.flush();
	}
	
	public void print(Class<?> cls, String log) throws IOException { //加入类名
		mWriter.write(df.format(new Date()));
		mWriter.write(cls.getSimpleName() + " ");
		mWriter.write(log);
		mWriter.write("\n");
		mWriter.flush();
	}
	
}

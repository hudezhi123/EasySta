package easyway.Mobile.Net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import easyway.Mobile.util.*;
import easyway.Mobile.util.FileUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * 附件下载
 */
public class DownloadHelper {
	public Handler handler;

	private URL url = null;
	public int FileSize = 0;
	public int DownloadedSize = 0;
	public boolean StopDownload = false;

	/**
	 * 根据URL下载文件,前提是这个文件当中的内容是文本,函数的返回值就是文本当中的内容 1.创建一个URL对象
	 * 2.通过URL对象,创建一个HttpURLConnection对象 3.得到InputStream 4.从InputStream当中读取数据
	 * 
	 * @param urlStr
	 * @return
	 */
	public String downloadTxt(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return -1:文件下载出错 0:文件下载成功 1:文件已经存在
	 */
	public int DownFile(String urlStr, String path, String fileName) {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils(path);
			if (fileUtils.isFileExist(path + fileName)) {
				return 1;
			} else {
				fileUtils.createSDDir(path);
				GetHttpUrl(urlStr, path, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 
	 * @param urlStr
	 * @param path
	 * @return -1:文件下载出错 0:文件下载成功 1:文件已经存在
	 */
	public int DownFile(String urlStr, String path) {
		String fileName = GetRemoteFileName(urlStr);
		if (fileName.equals(""))
			return -1;
		return DownFile(urlStr, path, fileName);
	}

	public void GetHttpUrl(String url, String path, String fileName)
			throws Exception {
		URL myFileUrl = null;
		InputStream in = null;
//		String pat = URLDecoder.decode(url);
//		if(pat.contains("站内规章")){
//			String front = pat.substring(0, 43);
//			String modiyf = pat.substring(43, 47);
//			String last = pat.substring(pat.length()-19,pat.length());
//			String temp = URLEncoder.encode(modiyf);
//			pat = front + temp + last;
//		}
		myFileUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
		conn.setConnectTimeout(10000);
		conn.setDoInput(true);
		conn.connect();
		in = conn.getInputStream();

		this.FileSize = conn.getContentLength();
		if (this.FileSize <= 0) {
			throw new Exception("无法获知文件大小");
		}

		Message message = new Message();
		message.what = 0;
		message.obj = FileSize;
		handler.sendMessage(message);
		LogUtil.i("Downloadhelper --->" +0);

		if (in == null) {
			throw new RuntimeException("下载失败");
		}
		int resCode = conn.getResponseCode();
		if (resCode != 200) {
			System.gc();
		}
		myFileUrl = null;
		System.gc();

		FileOutputStream fos = new FileOutputStream(path + fileName);
		byte buf[] = new byte[1024];
		this.DownloadedSize = 0;

		do {
			// 循环读取
			int numread = in.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			if (StopDownload) {
				File file = new File(path + fileName);
				file.delete();
				fos.close();
				Log.d("DownloadHelper", "Break");
				break;
			}
			DownloadedSize += numread;
			if (handler != null) {
				message = new Message();
				message.what = 1;
				message.obj = DownloadedSize;
				handler.sendMessage(message);
			}

		} while (!StopDownload);
		fos.close();
		fos = null;
		CloseStream(in, null);
		System.gc();
		/*
		 * byte[] bytes = InputStreamToByte(in); CloseStream(in, null); return
		 * bytes;
		 */
	}

	private void CloseStream(InputStream in, OutputStream out) {
		try {
			if (null != in) {
				in.close();
				in = null;
			}
			if (null != out) {
				out.close();
				out = null;
			}
		} catch (Exception e) {

		}
	}

	public byte[] InputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int count = -1;
		while ((count = is.read(data, 0, 4096)) != -1) {
			bytestream.write(data, 0, count);
		}
		data = null;
		return bytestream.toByteArray();
	}

	public static String GetRemoteFileName(String url) {
		if (url == null)
			return "";
		if (url.equals(""))
			return "";
		String[] filePathList = url.split("/");
		if (filePathList.length == 0)
			return "";
		return filePathList[filePathList.length - 1];
	}

}

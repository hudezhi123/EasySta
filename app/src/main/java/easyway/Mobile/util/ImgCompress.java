package easyway.Mobile.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class ImgCompress {

	public static String Compress(String filePath) {
		return Compress(filePath, 90);
	}
	
	public static String Compress(String filePath, int quality) {
		if (filePath == null)
			return null;

		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		if (f.isDirectory()) {
			return null;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		int w = options.outWidth;
		int h = options.outHeight;

		float hh = 768f;
		float ww = 1024f;
		int be = 1;
		if (w > h && w > ww) {			// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (options.outWidth / ww);
		} else if (w < h && h > hh) {	// 如果高度高的话根据宽度固定大小缩放
			be = (int) (options.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;		// 设置缩放比例
		
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(filePath, options);


		File file = new File(filePath);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file.getPath();
	}
	
	
	public static String SaveImage(Context context, Bitmap bitmap) {
		Uri fileUri = CommonUtils.getOutputMediaFileUri(context, CommonUtils.MEDIA_TYPE_IMAGE);
		
		if (fileUri == null)
			return null;
		
		File file = new File(fileUri.getPath());
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!bitmap.isRecycled()){
			bitmap.recycle();
		}
		
		return file.getPath();
	}
	
//	public static String transImage(String fromFile, String toFile, int width, int height, int quality) {
//		try {
//			if (fromFile == null || toFile == null)
//				return null;
//			
//			if (!(width > 0 && height > 0))
//				return null;
//			
//			if (quality < 0 || quality > 100) 
//				return null;
//			
//			Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
//			if (bitmap == null)
//				return null;
//			
//			int bitmapWidth = bitmap.getWidth();
//			int bitmapHeight = bitmap.getHeight();
//			// 缩放图片的尺寸
//			float scaleWidth = (float) width / bitmapWidth;
//			float scaleHeight = (float) height / bitmapHeight; 
//			float scale = (scaleWidth > scaleHeight ? scaleHeight : scaleWidth);
//			Matrix matrix = new Matrix();
//			matrix.postScale(scale, scale);
//			// 产生缩放后的Bitmap对象
//			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
//			// save file
//			File myCaptureFile = new File(toFile);
//			FileOutputStream out = new FileOutputStream(myCaptureFile);
//			if(resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)){
//				out.flush();
//				out.close();
//			}
//			if(!bitmap.isRecycled()){
//				bitmap.recycle();//记得释放资源，否则会内存溢出
//			}
//			if(!resizeBitmap.isRecycled()){
//				resizeBitmap.recycle();
//			}
//
//			return toFile;
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//		
//		return null;
//	}
	
	public static String Compress(String filePath, String toFile, int width, int height, int quality) {
		if (filePath == null || toFile == null)
			return null;

		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		if (f.isDirectory()) {
			return null;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		int w = options.outWidth;
		int h = options.outHeight;

		float hh = 768f;
		float ww = 1024f;
		int be = 1;
		if (w > h && w > ww) {			// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (options.outWidth / ww);
		} else if (w < h && h > hh) {	// 如果高度高的话根据宽度固定大小缩放
			be = (int) (options.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;		// 设置缩放比例
		
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(filePath, options);


		File file = new File(toFile);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(!bitmap.isRecycled()){
			bitmap.recycle();
		}

		return file.getPath();
	}
}

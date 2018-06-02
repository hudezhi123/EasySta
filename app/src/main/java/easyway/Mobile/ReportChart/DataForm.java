package easyway.Mobile.ReportChart;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Color;

public class DataForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -198439457275512296L;
	public String Name;		// 标题
	public int Type = 0;
	public String Unit;			// 单位
	public ArrayList<DataSerise> Serises;
	
	public static final int TYPE_PIE = 1;		// 饼图
	public static final int TYPE_LINE = 2;		// 线形图
	public static final int TYPE_BAR = 3;		// 柱状图
	
	public static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
		Color.MAGENTA, Color.CYAN, Color.YELLOW };
}

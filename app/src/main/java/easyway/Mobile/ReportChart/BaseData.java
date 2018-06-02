package easyway.Mobile.ReportChart;

import java.io.Serializable;

public class BaseData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1965452975514893683L;
	public String Content;		// 内容
	public double XValue;
	public double YValue;				// 值
	
	public BaseData(String content, double xvalue, double yvalue) {
		Content = content;
		XValue = xvalue;
		YValue = yvalue;
	}
}

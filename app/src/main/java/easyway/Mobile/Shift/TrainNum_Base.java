package easyway.Mobile.Shift;

import java.io.Serializable;

public class TrainNum_Base implements Serializable{
	
	//火车号
	public String TrainNum;
	//声音路径
	public String VoicePath;
	//交班信息
	public String Text;
	
	public TrainNum_Base(String trainNum, String voicePath, String text) {
		super();
		TrainNum = trainNum;
		VoicePath = voicePath;
		Text = text;
	}

	public TrainNum_Base() {
		super();
	}

	@Override
	public String toString() {
		return "TrainNum_Base [TrainNum=" + TrainNum + ", VoicePath="
				+ VoicePath + ", Text=" + Text + "]";
	}

	
	
}

package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.List;

public class ResultForListData<T> implements Serializable {

	private boolean MsgType;
	private String Msg;
	private List<T> Data;
	private String SessionId;
	private int Total;
	private int Code;

	public boolean isMsgType() {
		return MsgType;
	}

	public void setMsgType(boolean msgType) {
		MsgType = msgType;
	}

	public String getMsg() {
		return Msg;
	}

	public void setMsg(String msg) {
		Msg = msg;
	}

	public List<T> getData() {
		return Data;
	}

	public void setData(List<T> data) {
		Data = data;
	}

	public String getSessionId() {
		return SessionId;
	}

	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}

	public int getTotal() {
		return Total;
	}

	public void setTotal(int total) {
		Total = total;
	}

	public int getCode() {
		return Code;
	}

	public void setCode(int code) {
		Code = code;
	}

	@Override
	public String toString() {
		return "ResultForListData [MsgType=" + MsgType + ", Msg=" + Msg
				+ ", Data=" + Data + ", SessionId=" + SessionId + ", Total="
				+ Total + ", Code=" + Code + "]";
	}
	
}

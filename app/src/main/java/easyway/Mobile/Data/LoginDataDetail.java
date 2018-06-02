package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.List;

public class LoginDataDetail implements Serializable {
	private boolean MsgType;

	private String Msg;

	private List<OStation> Data;

	private String SessionId;

	private int Total;

	private int Code;

	private String Version;

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

	public List<OStation> getData() {
		return Data;
	}

	public void setData(List<OStation> data) {
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

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	@Override
	public String toString() {
		return "LoginDataDetail [MsgType=" + MsgType + ", Msg=" + Msg
				+ ", Data=" + Data + ", SessionId=" + SessionId + ", Total="
				+ Total + ", Code=" + Code + ", Version=" + Version + "]";
	}

}

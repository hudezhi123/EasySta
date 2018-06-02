package easyway.Mobile.Data;

import java.io.Serializable;
import java.util.List;

public class LoginResult implements Serializable {

	private boolean MsgType;

	private String Msg;

	private List<LoginData> Data;

	private String SessionId;

	private int Total;

	private int Code;

	private String Version;

	public void setMsgType(boolean MsgType) {
		this.MsgType = MsgType;
	}

	public boolean getMsgType() {
		return this.MsgType;
	}

	public void setMsg(String Msg) {
		this.Msg = Msg;
	}

	public String getMsg() {
		return this.Msg;
	}

	public void setData(List<LoginData> Data) {
		this.Data = Data;
	}

	public List<LoginData> getData() {
		return this.Data;
	}

	public void setSessionId(String SessionId) {
		this.SessionId = SessionId;
	}

	public String getSessionId() {
		return this.SessionId;
	}

	public void setTotal(int Total) {
		this.Total = Total;
	}

	public int getTotal() {
		return this.Total;
	}

	public void setCode(int Code) {
		this.Code = Code;
	}

	public int getCode() {
		return this.Code;
	}

	public void setVersion(String Version) {
		this.Version = Version;
	}

	public String getVersion() {
		return this.Version;
	}

	@Override
	public String toString() {
		return "LoginResult [MsgType=" + MsgType + ", Msg=" + Msg + ", Data="
				+ Data + ", SessionId=" + SessionId + ", Total=" + Total
				+ ", Code=" + Code + ", Version=" + Version + "]";
	}

}

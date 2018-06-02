package easyway.Mobile.TrainAD;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boy on 2017/9/4.
 */

public class CheckPortResult {

    /**
     * MsgType : true
     * Msg :
     * Data : [{"TwId":1542,"Workspace":"A1检票口","StationCode":"SYB"},{"TwId":1543,"Workspace":"A2、A3检票口","StationCode":"SYB"},{"TwId":1544,"Workspace":"A4、A5检票口","StationCode":"SYB"},{"TwId":1545,"Workspace":"A6、A7检票口","StationCode":"SYB"},{"TwId":1546,"Workspace":"A8、A9检票口","StationCode":"SYB"},{"TwId":1547,"Workspace":"A10、A11检票口","StationCode":"SYB"},{"TwId":1548,"Workspace":"A12、A13检票口","StationCode":"SYB"},{"TwId":1549,"Workspace":"A14检票口","StationCode":"SYB"},{"TwId":1572,"Workspace":"B2、B3检票口","StationCode":"SYB"},{"TwId":1573,"Workspace":"B4、B5检票口","StationCode":"SYB"},{"TwId":1574,"Workspace":"B8、B9检票口","StationCode":"SYB"},{"TwId":1575,"Workspace":"B10、B11检票口","StationCode":"SYB"},{"TwId":1576,"Workspace":"B12、B13检票口","StationCode":"SYB"},{"TwId":1582,"Workspace":"B6、B7检票口","StationCode":"SYB"},{"TwId":1703,"Workspace":"2FA18、A19进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":794,"MapY":350},{"TwId":1704,"Workspace":"2FA30、A31进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1705,"Workspace":"2FA2、A3进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":975,"MapY":1381},{"TwId":1706,"Workspace":"2FA12、A13进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":589,"MapY":350},{"TwId":1707,"Workspace":"2FA4、A5进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":974,"MapY":1226},{"TwId":1708,"Workspace":"2FA14、A15进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":663,"MapY":350},{"TwId":1709,"Workspace":"2FA20、A21进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":851,"MapY":350},{"TwId":1710,"Workspace":"1F南A1进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1711,"Workspace":"2FA6、A7进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":972,"MapY":985},{"TwId":1712,"Workspace":"2F西B34进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1713,"Workspace":"2FA28、A29进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1714,"Workspace":"2FA16、A17进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":720,"MapY":350},{"TwId":1715,"Workspace":"2FA26、A27进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1716,"Workspace":"2FA22、A23进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":930,"MapY":350},{"TwId":1717,"Workspace":"1F北A34进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1718,"Workspace":"2FA10、A11进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":532,"MapY":350},{"TwId":1719,"Workspace":"2F西B1进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":509,"MapY":1770},{"TwId":1720,"Workspace":"2FA24、A25进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1721,"Workspace":"2FA8、A9进站口","TCate":"inticket","StationCode":"XAB","MapId":13,"MapX":972,"MapY":785},{"TwId":1722,"Workspace":"2FA32、A33进站口","TCate":"inticket","StationCode":"XAB"},{"TwId":1923,"Workspace":"1-27检票口","TCate":"inticket","StationCode":"XAB"}]
     * SessionId :
     * Total : 35
     * Code : 1000
     * Version :
     */

    private boolean MsgType;
    private String Msg;
    private String SessionId;
    private int Total;
    private int Code;
    private String Version;
    private List<DataBean> Data;

    public boolean isMsgType() {
        return MsgType;
    }

    public void setMsgType(boolean MsgType) {
        this.MsgType = MsgType;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }

    public String getSessionId() {
        return SessionId;
    }

    public void setSessionId(String SessionId) {
        this.SessionId = SessionId;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public List<DataBean> getData() {
        return Data;
    }

    public void setData(List<DataBean> Data) {
        this.Data = Data;
    }

    public static class DataBean {
        /**
         * TwId : 1542
         * Workspace : A1检票口
         * StationCode : SYB
         * TCate : inticket
         * MapId : 13
         * MapX : 794
         * MapY : 350
         */

        private int TwId;
        private String Workspace;
        private String StationCode;
        private String TCate;
        private int MapId;
        private int MapX;
        private int MapY;

        public int getTwId() {
            return TwId;
        }

        public void setTwId(int TwId) {
            this.TwId = TwId;
        }

        public String getWorkspace() {
            return Workspace;
        }

        public void setWorkspace(String Workspace) {
            this.Workspace = Workspace;
        }

        public String getStationCode() {
            return StationCode;
        }

        public void setStationCode(String StationCode) {
            this.StationCode = StationCode;
        }

        public String getTCate() {
            return TCate;
        }

        public void setTCate(String TCate) {
            this.TCate = TCate;
        }

        public int getMapId() {
            return MapId;
        }

        public void setMapId(int MapId) {
            this.MapId = MapId;
        }

        public int getMapX() {
            return MapX;
        }

        public void setMapX(int MapX) {
            this.MapX = MapX;
        }

        public int getMapY() {
            return MapY;
        }

        public void setMapY(int MapY) {
            this.MapY = MapY;
        }
    }

    public static List<DataBean> parseToList(String jsonResult) {
        CheckPortResult result = new Gson().fromJson(jsonResult, CheckPortResult.class);
        List<DataBean> list = new ArrayList<>();
        if (result != null && result.getData() != null && result.getData().size() > 0) {
            list = result.getData();
        }
        return list;
    }
}

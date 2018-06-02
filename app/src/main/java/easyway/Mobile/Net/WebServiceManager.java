package easyway.Mobile.Net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import easyway.Mobile.R;
import easyway.Mobile.util.CommonFunc;
import easyway.Mobile.util.LogUtil;

import android.content.Context;
import android.util.Log;

public class WebServiceManager {
    private final int CONNECT_TIMEOUT = 30 * 1000; // 连接超时时间
    private final int READ_TIMEOUT = 30 * 1000; // 读取超时时间
    private Context context;
    private String methodName;
    private byte[] outBytes;

    public WebServiceManager(Context context, String methodName,
                             HashMap<String, String> paramValues) {
        this.context = context;
        this.methodName = methodName;
        this.outBytes = GetXmlParams(methodName, paramValues);
    }

    /**
     * 这个构造方法新加的int参数没用。随便传都行。主要是第二个参数只能是基本数据类型的对象包装类。扩展一下请求参数的数据类型。
     *
     * @param context
     * @param methodName
     * @param paramValues 第二个Objcet 使用范例 Objcet obj = 123 & Objcet obj = boolean; 基本数据类型直接用Object引用就可以了。因为java 有自动装箱的操作。
     * @param i
     */
    public WebServiceManager(Context context, String methodName,
                             HashMap<String, Object> paramValues, int i) {
        this.context = context;
        this.methodName = methodName;
        this.outBytes = GetXmlParamsForBasicDataType(methodName, paramValues);
    }

    /**
     * @param methodName
     * @param paramValue
     */
    public WebServiceManager(String methodName, HashMap<String, String> paramValue) {
        this.methodName = methodName;
        this.outBytes = GetXmlParams(methodName, paramValue);
    }

    public byte[] GetOutBytes() {
        return this.outBytes;
    }

    public String OpenConnect(String methodPath) {
        String result = "";
        if (CommonFunc.getLocalIpAddress().endsWith("0.0.0.0")) {
            return CreatErrorMsg(context, Constant.EXP_NET_NO_CONNECT);
        }

        LogUtil.w(methodName + " input -->" + new String(outBytes));
        try {
            String serverName = CommonFunc.GetServer(context);
            URL url = new URL(serverName + methodPath);

            LogUtil.e("URL -------->" + url.toString());
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(true);
            connection.setDefaultUseCaches(true);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", "http://tempuri.org/"
                    + methodName);
            connection.setRequestMethod("POST");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(outBytes);
            outputStream.flush();
            outputStream.close();

            int resCode = connection.getResponseCode();

            if (resCode != 200) {
                LogUtil.i(methodName + " resCode-->" + resCode);
                return CreatErrorMsg(context, Constant.EXP_NET_SERVICE_ER);
            }

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String lines;
            while ((lines = reader.readLine()) != null) {
                result += lines;
            }

            if (result.equals("")) {
                LogUtil.i(methodName + " result-->" + result);
                return "";
            }

            result = GetXmlJson(result);
            connection.disconnect();
            connection = null;
        } catch (ConnectException e) {
            e.printStackTrace();
            if (context != null) {
                result = CreatErrorMsg(context, Constant.EXP_NET_CONNECT_ERROR);
            }
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            if (context != null) {
                result = CreatErrorMsg(context, Constant.EXP_NET_CONNECT_TIMEOUT);
            }
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            if (context != null) {
                result = CreatErrorMsg(context, Constant.EXP_NET_READ_TIME_OUT);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = CreatErrorMsg(context, Constant.EXP_NET_CONNECT_ERROR);
        } catch (ProtocolException e) {
            e.printStackTrace();
            if (context != null) {
                result = CreatErrorMsg(context, Constant.EXP_NET_CONNECT_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "";
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.i(methodName + " result-->" + result);
        return result;
    }

    // 拼装soap请求
    private byte[] GetXmlParams(String methodName,
                                HashMap<String, String> parmValues) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        sb.append("<soap:Body>");
        sb.append("<" + methodName + "  xmlns=\"http://tempuri.org/\">");
        for (Map.Entry<String, String> item : parmValues.entrySet()) {
            String key = item.getKey();
            String value = item.getValue();
            if (value == null) {
                value = "";
            } else {
                value = value.trim();
            }
            sb.append("<" + key + ">");
            sb.append(value);
            sb.append("</" + key + ">");
        }
        sb.append("</" + methodName + ">");
        sb.append("</soap:Body>");
        sb.append("</soap:Envelope>");
        try {
            Log.e("请求数据", sb.toString());
            return sb.toString().getBytes("utf-8");
        } catch (Exception ex) {
            return null;
        }

    }

    /**
     * 第二个参数只能是基本数据类型的对象包装类。
     *
     * @param methodName
     * @param parmValues 第二个Objcet 使用范例 Objcet obj = 123 & Objcet obj = boolean; 基本数据类型直接用Object引用就可以了。因为java 有自动装箱的操作。
     * @return
     */
    private byte[] GetXmlParamsForBasicDataType(String methodName,
                                                HashMap<String, Object> parmValues) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        sb.append("<soap:Body>");
        sb.append("<" + methodName + "  xmlns=\"http://tempuri.org/\">");
        for (Map.Entry<String, Object> item : parmValues.entrySet()) {
            String key = item.getKey();
            Object value = item.getValue();
            sb.append("<" + key + ">");
            String FormName = value.getClass().getSimpleName();
            if (FormName.equals("String")) {
                String temp = (String) value;
                sb.append(temp);
            } else if (FormName.equals("Byte")) {
                byte temp = (Byte) value;
                sb.append(temp);
            } else if (FormName.equals("Character")) {
                char temp = (Character) value;
                sb.append(temp);
            } else if (FormName.equals("Integer")) {
                int temp = (Integer) value;
                sb.append(temp);
            } else if (FormName.equals("Long")) {
                long temp = (Long) value;
                sb.append(temp);
            } else if (FormName.equals("Float")) {
                float temp = (Float) value;
                sb.append(temp);
            } else if (FormName.equals("Double")) {
                double temp = (Double) value;
                sb.append(temp);
            }
            sb.append("</" + key + ">");
        }
        sb.append("</" + methodName + ">");
        sb.append("</soap:Body>");
        sb.append("</soap:Envelope>");
        try {
            Log.e("请求数据", sb.toString());
            return sb.toString().getBytes("utf-8");
        } catch (Exception ex) {
            return null;
        }

    }

    private String GetXmlJson(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xml));
            Document dom = builder.parse(inputSource);
            Element root = dom.getDocumentElement();
            String json = root.getTextContent();
            return json;
        } catch (Exception ex) {
            return "";
        }
    }

    // 组装异常返回
    public static String CreatErrorMsg(Context ctx, int code) {
        String msg = null;
        switch (code) {
            case Constant.EXP_NET_NO_CONNECT:
                msg = ctx.getString(R.string.exp_notconnect);
                break;
            case Constant.EXP_NET_CONNECT_TIMEOUT:
                msg = ctx.getString(R.string.exp_connecttimeout);
                break;
            case Constant.EXP_NET_CONNECT_ERROR:
                msg = ctx.getString(R.string.exp_connect);
                break;
            case Constant.EXP_NET_READ_TIME_OUT:
                msg = ctx.getString(R.string.exp_readtimeout);
                break;
            case Constant.EXP_NET_SERVICE_ER:
                msg = ctx.getString(R.string.exp_interface);
                break;
            default:
                break;
        }

        JSONObject jsonP = new JSONObject();
        try {
            jsonP.put("code", code);
            jsonP.put("errMsg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonP.toString();
    }
}

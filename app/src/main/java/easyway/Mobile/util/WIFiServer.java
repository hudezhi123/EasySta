package easyway.Mobile.util;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.IBinder;

public class WIFiServer extends Service{
	
	WifiManageClass wifi;
	String password;
	static String filter = "";
	
	final int wifiOpen = 1;
	final int wifiCLose = 2;
	final int wifiChange = 3;
	
	private String SSID = "Xian RailWay Test WiFi";
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case wifiOpen:
				init();
				break;
			case wifiCLose:
				boolean open = wifi.checkState();
				
				if(!open){
					handler.sendEmptyMessage(wifiOpen);
				}else{
					String s = null;
						s = wifi.getConnectInfo();
						String temp = s.substring(1, s.length()-1);
						boolean state = SSID.equals(temp);
						if(!state){
							handler.sendEmptyMessage(wifiChange);
						}
				}
				handler.sendEmptyMessageDelayed(wifiCLose, 1000);
				break;
			case wifiChange:
				wifi.startScan();
				List<WifiConfiguration> result = wifi.getWifiConfigList();
				if(result != null){
					for(WifiConfiguration evenOne : result){
						String s = evenOne.SSID;
						String temp = s.substring(1, s.length()-1);
						if(temp.equals(SSID)){
							int id = evenOne.networkId;
							wifi.Connect(id);
						}
					}
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		wifi = new WifiManageClass(getApplicationContext());
		password = "1234567890";
		super.onCreate();
	}
	
	private void init(){
			wifi.openWifi();
			wifi.startScan();
			List<ScanResult> result = wifi.getWifiList();
			int size = result.size();
			for(int i = 0; i < size; i++){
				ScanResult even  = result.get(i);
				String s = even.SSID;
				if(s.equals(SSID.trim())){
					//如果这个wifi之前以前配置过，那么就自动连接。
					WifiConfiguration config = wifi.isExist(s);
					if(config != null){
						int id = config.networkId;
						wifi.Connect(id);
					}else{
//						WifiConfiguration CreateConfig = wifi.CreateWifiInfo(s, password, 3);
//						//如果没添加，就添加，并且连接
//						wifi.addNetWordLink(CreateConfig);
					}
				}
			}
	}
	
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		handler.sendEmptyMessageDelayed(wifiCLose, 1000);
		
	}
	
}

package com.robot.et.core.software.system.network;

import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.core.software.system.network.util.NetWorkConnect;
import com.robot.et.core.software.system.network.util.NetWorkConnect.WifiCipherType;
import com.robot.et.util.Utilities;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class NetWorkConnectService extends Service {

	private WifiManager mWiFiManager;
	private NetWorkConnect netWorkConnect;
	private String netWorkName;
	private String password;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mWiFiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		netWorkConnect = new NetWorkConnect(mWiFiManager);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		mFilter.addAction(BroadcastAction.ACTION_NET_CONNECT_STATE);
		mFilter.addAction(BroadcastAction.ACTION_SCAN_QR_CODE);
		registerReceiver(mReceiver, mFilter);

		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_NET_CONNECT_STATE);
		sendBroadcast(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)|| intent.getAction().equals(BroadcastAction.ACTION_NET_CONNECT_STATE)) {
				boolean isNetConnected = Utilities.isNetworkConnected(context);
				if (isNetConnected) {
					Log.i("NetWork", "网络已连接");
					intent.setAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_CONNECT);
					sendBroadcast(intent);
				} else {
					Log.i("NetWork", "网络断开");
					intent.setAction(BroadcastAction.ACTION_VOICE_TO_TEXT_SPEAK);
					intent.putExtra(DataConfig.TYPE_KEY,DataConfig.TYPE_NETWORK_BREAKOFF);
					intent.putExtra("result", DataConfig.NETWORK_BREAK_OFF);
					sendBroadcast(intent);
				}
			} else if (intent.getAction().equals(BroadcastAction.ACTION_SCAN_QR_CODE)) {
				String result = intent.getStringExtra("result");
				Log.i("NetWork", "接收到wifi二维码数据：" + result);
				splitResultAndConnect(result);
			}
		}
	};

	private void splitResultAndConnect(String result) { 
		if (result.contains("MUZI")) {
			//result的结果为：company:MUZHI;S:xiaohuangren;P:test1230;
			String[] arrar=result.split(";");
			netWorkName=arrar[1].split(":")[1];
			Log.i("zxing", "网络名称："+netWorkName);
			password=arrar[2].split(":")[1];
			Log.i("zxing", "密码："+password);
			//该部分只是我个人的观点，我觉得这地方会有问题，wifi的加密类型没有处理。
			netWorkConnect.Connect(netWorkName, password, WifiCipherType.WIFICIPHER_WPA);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

}

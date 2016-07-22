package com.robot.et.core.software.agora;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.core.software.agora.util.AgoraControl;
import com.robot.et.debug.Logger;
import com.robot.et.entity.JpushInfo;
import com.xsj.crasheye.Crasheye;

public class AgoraService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("AgoraService  onCreate()");
		Crasheye.initWithNativeHandle(this, DataConfig.AGORA_CRASHEYE_KEY);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_CONNECT_AGORA);
		filter.addAction(BroadcastAction.ACTION_JOIN_AGORA_ROOM);
		filter.addAction(BroadcastAction.ACTION_CLOSE_AGORA_ACTIVITY);
		registerReceiver(receiver, filter);
		
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BroadcastAction.ACTION_CONNECT_AGORA)){//连接进入agora界面
				Log.i("agoravideo", "type===" + intent.getIntExtra("type", 0));
				AgoraControl.openChannelActivity(intent.getIntExtra("type", 0));
				intent.setAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_OPEN);
				sendBroadcast(intent);
				
			}else if(intent.getAction().equals(BroadcastAction.ACTION_JOIN_AGORA_ROOM)){//极光推送过来进入agora
				Log.i("agoravideo", "AgoraService    极光推送过来进入agora");
				JpushInfo info = intent.getParcelableExtra("JpushInfo");

				if(info != null){
					Log.i("agoravideo", "AgoraService   joinAgoraRoom");
					AgoraControl.joinAgoraRoom(info);
				}
				
			}else if(intent.getAction().equals(BroadcastAction.ACTION_CLOSE_AGORA_ACTIVITY)){//关闭agora界面
				AgoraControl.closeChannel();
				
			}
		}
	};
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}

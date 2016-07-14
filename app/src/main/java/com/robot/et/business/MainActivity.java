package com.robot.et.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.robot.et.R;
import com.robot.et.business.base.BaseActivity;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.core.hardware.emotion.EmotionService;
import com.robot.et.core.hardware.motor.ControlMoveService;
import com.robot.et.core.hardware.serialport.SerialPortService;
import com.robot.et.core.hardware.wakeup.WakeUpServices;
import com.robot.et.core.software.agora.AgoraService;
import com.robot.et.core.software.agora.util.AgoraUtils;
import com.robot.et.core.software.baidumap.LocationService;
import com.robot.et.core.software.iflytek.IflySpeakService;
import com.robot.et.core.software.iflytek.IflyTextUnderstanderService;
import com.robot.et.core.software.iflytek.IflyVoiceToTextService;
import com.robot.et.core.software.netty.NettyService;
import com.robot.et.core.software.ros.RosMoveActivity;
import com.robot.et.core.software.system.AlarmClockService;
import com.robot.et.core.software.system.network.NetWorkConnectService;
import com.robot.et.core.software.system.network.NetWorkTrafficService;
import com.robot.et.core.software.zxing.ScanCodeActivity;
import com.robot.et.util.BluetoothKeyManager;
import com.robot.et.util.ScriptManager;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

//import com.robot.et.core.software.turing.TuRingService;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("lifecycle", "onCreate");
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//注册监测网络连接成功的广播
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_CONNECT);
		mFilter.addAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_OPEN);
		mFilter.addAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_DISCONNECT);
		registerReceiver(mReceiver, mFilter);
		
		// 监听网络连接状态
		startService(new Intent(this, NetWorkConnectService.class));
		//科大讯飞语音合成（嘴巴<在线或者离线语音合成>）
		startService(new Intent(this, IflySpeakService.class));
		//闹铃提醒
		startService(new Intent(this, AlarmClockService.class));
		// 控制Robot服务
		startService(new Intent(this, ControlMoveService.class));
		// 初始化唤醒服务
		startService(new Intent(this, WakeUpServices.class));
		// 表情控制服务
		startService(new Intent(this, EmotionService.class));

		findViewById(R.id.img_welcome).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				ScriptManager.playScript("模拟剧本二");
//				Intent intent=new Intent(MainActivity.this, RosMoveActivity.class);
//				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(BluetoothKeyManager.responseKey(keyCode)){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//接收到网络连接成功的广播，开启下列服务
			if (intent.getAction().equals(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_CONNECT)) {
				//网络连接之后，如果扫描二维码的界面没有关闭的话，关掉
				if(ScanCodeActivity.instance != null){
					ScanCodeActivity.instance.finish();
					ScanCodeActivity.instance = null;
				}
				startActivity(intent.setClass(context,RosMoveActivity.class));
				// 极光推送
//				startService(intent.setClass(context, JpushService.class));
				//图灵
//				startService(intent.setClass(context, TuRingService.class));
				// 百度定位
				startService(intent.setClass(context, LocationService.class));
				// Netty服务
				startService(intent.setClass(context, NettyService.class));
				// 初始化串口服务
				startService(intent.setClass(context, SerialPortService.class));
				// agora服务
				startService(intent.setClass(context, AgoraService.class));
				//科大讯飞文本理解（文本理解）
				startService(intent.setClass(context, IflyTextUnderstanderService.class));
				//语音听写（耳朵<声音转化成文字>）
				startService(intent.setClass(context, IflyVoiceToTextService.class));
			}else if (intent.getAction().equals(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_OPEN)) {
				//网络流量监控
				startService(intent.setClass(context, NetWorkTrafficService.class));
			}else if (intent.getAction().equals(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_DISCONNECT)) {
				//如果视频通话界面还在的话，关闭界面
				AgoraUtils.closeChannel();
				// 极光推送
//				stopService(intent.setClass(context, JpushService.class));
				//图灵
//				stopService(intent.setClass(context, TuRingService.class));
				// 百度定位
				stopService(intent.setClass(context, LocationService.class));
				// Netty服务
				stopService(intent.setClass(context, NettyService.class));
				// 初始化串口服务
				stopService(intent.setClass(context, SerialPortService.class));
				// agora服务
				stopService(intent.setClass(context, AgoraService.class));
				// 科大讯飞文本理解（文本理解）
				stopService(intent.setClass(context, IflyTextUnderstanderService.class));
				// 语音听写（耳朵<声音转化成文字>）
				stopService(intent.setClass(context, IflyVoiceToTextService.class));
				// 网络流量监控
				stopService(intent.setClass(context, NetWorkTrafficService.class));
			}
		}
	};
	
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.i("lifecycle", "onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("lifecycle", "onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i("lifecycle", "onPause");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i("lifecycle", "onStop");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i("lifecycle", "onRestart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("lifecycle", "onDestroy");
		unregisterReceiver(mReceiver);
		stopService(new Intent(this, NetWorkConnectService.class));
		stopService(new Intent(this, IflySpeakService.class));
		stopService(new Intent(this, LocationService.class));
		stopService(new Intent(this, NettyService.class));
		stopService(new Intent(this, ControlMoveService.class));
		stopService(new Intent(this, SerialPortService.class));
		stopService(new Intent(this, IflyVoiceToTextService.class));
		stopService(new Intent(this, AlarmClockService.class));
		stopService(new Intent(this, IflyTextUnderstanderService.class));
		stopService(new Intent(this, EmotionService.class));
		stopService(new Intent(this, WakeUpServices.class));
//		stopService(new Intent(this, JpushService.class));
		stopService(new Intent(this, AgoraService.class));
//		stopService(new Intent(this, TuRingService.class));
		stopService(new Intent(this, NetWorkTrafficService.class));

		//语种切换还原
		DataConfig.isLanguageSwitch = false;
		
		// 清空部分保存
		SharedPreferencesUtils sharedUtils = SharedPreferencesUtils.getInstance();
		sharedUtils.removeValue(SharedPreferencesKeys.CITY_KEY);
		sharedUtils.removeValue(SharedPreferencesKeys.AREA_KEY);
		sharedUtils.removeValue(SharedPreferencesKeys.AGORA_ROOM_NUM);
		sharedUtils.commitValue();
	}
}

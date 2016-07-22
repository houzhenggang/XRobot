package com.robot.et.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.robot.et.R;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.core.hardware.emotion.EmotionService;
import com.robot.et.core.hardware.motor.ControlMoveService;
import com.robot.et.core.hardware.serialport.SerialPortService;
import com.robot.et.core.hardware.wakeup.WakeUpServices;
import com.robot.et.core.software.agora.AgoraService;
import com.robot.et.core.software.agora.util.AgoraControl;
import com.robot.et.core.software.baidumap.LocationService;
import com.robot.et.core.software.iflytek.IflySpeakService;
import com.robot.et.core.software.iflytek.IflyTextUnderstanderService;
import com.robot.et.core.software.iflytek.IflyVoiceToTextService;
import com.robot.et.core.software.netty.NettyService;
import com.robot.et.core.software.ros.CompressedMapTransport;
import com.robot.et.core.software.ros.MoveControler;
import com.robot.et.core.software.system.AlarmClockService;
import com.robot.et.core.software.system.network.NetWorkConnectService;
import com.robot.et.core.software.system.network.NetWorkTrafficService;
import com.robot.et.core.software.zxing.ScanCodeActivity;
import com.robot.et.util.BluetoothKeyManager;
import com.robot.et.util.ScriptManager;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.net.URI;

public class MainActivity extends RosActivity {

	private MoveControler mover;//ROS运动控制
	private CompressedMapTransport mapTransport; //ROS地图
	private NodeConfiguration nodeConfiguration;//ROS节点


	public MainActivity(){
		super("XRobot","Xrobot",URI.create("http://192.168.3.1:11311"));//本体的ROS IP和端口
//		super("XRobot","Xrobot",URI.create("http://192.168.2.164:11311"));
	}

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
		mFilter.addAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_NETTY);
		mFilter.addAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_VOICE);
		mFilter.addAction(BroadcastAction.ACTION_WAKE_UP_AND_MOVE);
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

	}

	@Override
	protected void init(NodeMainExecutor nodeMainExecutor) {
		mover = new MoveControler();
		mover.isPublishVelocity(false);
		mapTransport=new CompressedMapTransport();
		nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
		nodeConfiguration.setMasterUri(getMasterUri());
		nodeMainExecutor.execute(mover, nodeConfiguration);
		nodeMainExecutor.execute(mapTransport,nodeConfiguration);
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
				AgoraControl.closeChannel();
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
			}else if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_NETTY)) {
				String direction=intent.getStringExtra("direction");
				Log.i("ROS_MOVE","手机控制时，得到的direction参数："+direction);
				if (null==direction|| TextUtils.equals("", direction)) {
					return;
				}
				doMoveAction(direction);
				if(DataConfig.isPlayScript){
					ScriptManager.setNewScriptInfos(ScriptManager.getScriptActionInfos(),true,2000);
				}
			}else if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_VOICE)){
				//此部分代码暂时这样修改，待完善。（时间太赶）2016-07-16
				String direction=intent.getStringExtra("direction");
				Log.i("ROS_MOVE","语音控制时，得到的direction参数："+direction);
				String digit=intent.getStringExtra("digit");
				Log.i("ROS_MOVE","语音控制时，得到的digit参数："+digit);
				if (null==direction|| TextUtils.equals("", direction)) {
					return;
				}
//				if (null == digit || TextUtils.equals("",digit)) {
//					return;
//				}
				if (TextUtils.equals("1",direction)||TextUtils.equals("2",direction)){
					doMoveAction(direction);
					try {
						Thread.sleep(1500);
					}catch (InterruptedException e){
						e.printStackTrace();
					}finally {
						doMoveAction("5");
					}
				}else if (TextUtils.equals("3",direction)){
					doTrunAction(mover.getCurrentDegree(),270);
				}else if (TextUtils.equals("4",direction)){
					doTrunAction(mover.getCurrentDegree(),90);
				}
			} else if (intent.getAction().equals(BroadcastAction.ACTION_WAKE_UP_AND_MOVE)){
				Log.i("ROS_WAKE_UP","语音唤醒时，当前机器人的角度："+mover.getCurrentDegree());
				int data=intent.getIntExtra("degree",0);//获取的Brocast传递的角度
				Log.i("ROS_WAKE_UP_DEGREE","语音唤醒时，获取的角度："+data);
				if (data == 0 || data == 360){
					//原地不动
					return;
				}
				doTrunAction(mover.getCurrentDegree(),Double.valueOf(data));
			}
		}
	};

	private void doMoveAction(String message) {
		mover.isPublishVelocity(true);
		if (TextUtils.equals("1", message)) {
			Log.i("ROS_MOVE", "机器人移动方向:向前");
			mover.execMoveForword();
		} else if (TextUtils.equals("2", message)) {
			Log.i("ROS_MOVE", "机器人移动方向:向后");
			mover.execMoveBackForward();
		} else if (TextUtils.equals("3", message)) {
			Log.i("ROS_MOVE", "机器人移动方向:向左");
			mover.execTurnLeft();
		} else if (TextUtils.equals("4", message)) {
			Log.i("ROS_MOVE", "机器人移动方向:向右");
			mover.execTurnRight();
		} else if (TextUtils.equals("5", message)) {
			Log.i("ROS_MOVE", "机器人移动方向:停止");
			mover.execStop();
		}
	}
	public void doTrunAction(double currentDegree,double degree){
		mover.isPublishVelocity(true);
		double temp;
		if (currentDegree+degree<=180){
			temp=currentDegree+degree;
		}else {
			temp=currentDegree+degree-360;
		}
		if ((degree > 0 && degree < 180)){
			mover.execTurnRight();
			mover.setDegree(temp);
		}else{
			mover.execTurnLeft();
			mover.setDegree(temp);
		}
	}
	
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
		stopService(new Intent(this, AgoraService.class));
		stopService(new Intent(this, NetWorkTrafficService.class));

		//语种切换还原
		DataConfig.isLanguageSwitch = false;
		BroadcastShare.controlMouthLED(ScriptConfig.LED_OFF);
		BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
		
		// 清空部分保存
		SharedPreferencesUtils sharedUtils = SharedPreferencesUtils.getInstance();
		sharedUtils.removeValue(SharedPreferencesKeys.CITY_KEY);
		sharedUtils.removeValue(SharedPreferencesKeys.AREA_KEY);
		sharedUtils.removeValue(SharedPreferencesKeys.AGORA_ROOM_NUM);
		sharedUtils.commitValue();
	}
}

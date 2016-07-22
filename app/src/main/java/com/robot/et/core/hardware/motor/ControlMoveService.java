package com.robot.et.core.hardware.motor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.DataConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.entity.RobotAction;
import com.robot.et.util.ScriptManager;

public class ControlMoveService extends Service {
	private int i;
	private int controlNumAways;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		IntentFilter filter = new IntentFilter();
//		filter.addAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE);
//		filter.addAction(BroadcastAction.ACTION_WAKE_UP_AND_MOVE);
		filter.addAction(BroadcastAction.ACTION_CONTROL_AROUND_TOYCAR);
		filter.addAction(BroadcastAction.ACTION_CONTROL_WAVING);
		filter.addAction(BroadcastAction.ACTION_CONTROL_MOUTH_LED);
		filter.addAction(BroadcastAction.ACTION_CONTROL_TOYCAR_AWAYS);
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

//		private String direction;
//		private String tempDigit;

		@Override
		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_NETTY)) {
//				direction = intent.getStringExtra("direction");// 获取方向
//				if (null==direction||TextUtils.equals("", direction)) {
//					return;
//				}
//				tempDigit=intent.getStringExtra("digit");
//				if (null==tempDigit||TextUtils.equals("", tempDigit)) {
//					return;
//				}
//				int digit = Integer.valueOf(tempDigit);// 获取数字数据
//				Log.i("Move", "direction:" + direction + ",data:" + digit);
//				int j = digit / 10;
//				int circle = digit % 10 == 0 ? j : j + 1;
//				Log.i("circle", "circle:"+circle);
//				if (TextUtils.equals("1", direction) || TextUtils.equals("2", direction)) {
//					for (int i = 0; i < circle; i++) {
//						Log.i("circle", "circle:"+i);
//						doAction(direction);
//					}
//				} else if (TextUtils.equals("3", direction) || TextUtils.equals("4", direction)) {
//					for (int i = 0; i < circle; i++) {
//						Log.i("circle", "circle:"+i);
//						doAction(direction);
//					}
//				} else {
//					doAction(direction);
//				}
//			} else if (intent.getAction().equals(BroadcastAction.ACTION_WAKE_UP_AND_MOVE)) {
//				doAction("5");//直接先停掉以前的运动
//				int degree = intent.getIntExtra("degree", 0);//获取旋转角度
//				Log.i("Move", "旋转角度：" + degree);
//				if (degree == 0 || degree == 360) {
//					// 原地不动
//				} else if ((degree > 0 && degree < 180)) {
//					direction = "4";
//				} else {
//					direction = "3";
//					degree = 360 - degree;
//				}
//				int j = degree / 10;
//				int circle = degree % 10 == 0 ? j : j + 1;
//				if (TextUtils.equals("3", direction) || TextUtils.equals("4", direction)) {
//					for (int i = 0; i < circle; i++) {
//						doAction(direction);
//					}
//				}
//			}else
			if(intent.getAction().equals(BroadcastAction.ACTION_CONTROL_AROUND_TOYCAR)){//控制周围小车
				Log.i("Move", "控制周围小车");
				String direction = intent.getStringExtra("direction");
				int directionType = 0;
				if(!TextUtils.isEmpty(direction)){
					if(TextUtils.isDigitsOnly(direction)){
						directionType = Integer.parseInt(direction);
					}
				}
				int toyCarNum = intent.getIntExtra("toyCarNum",0);
				Log.i("Move", "控制周围小车direction==="+ direction);
				Log.i("Move", "toyCarNum==="+ toyCarNum);
				contrlToyCarMove(directionType,toyCarNum);

				if(DataConfig.isControlToyCar){
					if(directionType == 1 || directionType == 2){
						controlNumAways = 10;
					}else if(directionType == 3 || directionType == 4){
						controlNumAways = 100;
					}

					if(directionType != 5){
						SystemClock.sleep(200);
						intent.setAction(BroadcastAction.ACTION_CONTROL_TOYCAR_AWAYS);
						intent.putExtra("directionType",directionType);
						intent.putExtra("toyCarNum",toyCarNum);
						sendBroadcast(intent);
					}else{
						Log.i("Move", "directionType 停止==="+ directionType);
						DataConfig.controlNum = controlNumAways;
						contrlToyCarMove(5,toyCarNum);
					}
				}

				if(DataConfig.isPlayScript){
					int delayTime = 20;
					i++;
					if(i == 2){
						i = 0;
						delayTime = 200;
					}

					ScriptManager.setNewScriptInfos(ScriptManager.getScriptActionInfos(),true,delayTime);
				}

			}else if(intent.getAction().equals(BroadcastAction.ACTION_CONTROL_WAVING)){//举手摆手
				Log.i("Move", "举手摆手");
				String handDirection = intent.getStringExtra("handDirection");
				String handCategory = intent.getStringExtra("handCategory");
				String num = intent.getStringExtra("num");
				Log.i("Move", "handCategory===" + handCategory);
				if(!TextUtils.isEmpty(handDirection) && !TextUtils.isEmpty(handCategory)){
					handAction(handDirection,handCategory);
				}
				if(DataConfig.isPlayScript){
					ScriptManager.setNewScriptInfos(ScriptManager.getScriptActionInfos(),true,1500);
				}

			}else if(intent.getAction().equals(BroadcastAction.ACTION_CONTROL_MOUTH_LED)){//嘴的LED灯
				Log.i("Move", "嘴的LED灯");
				String LEDState = intent.getStringExtra("LEDState");
				if(!TextUtils.isEmpty(LEDState)){
					controlMouthLED(LEDState);
				}
			}else if(intent.getAction().equals(BroadcastAction.ACTION_CONTROL_TOYCAR_AWAYS)){//语音不停的控制小车
				Log.i("Move", "语音不停的控制小车");
				int directionType = intent.getIntExtra("directionType",0);
				int toyCarNum = intent.getIntExtra("toyCarNum",0);
				DataConfig.controlNum ++ ;
				if(DataConfig.controlNum < controlNumAways){
					BroadcastShare.controlToyCarMove(String.valueOf(directionType), toyCarNum);
				}

			}

		}
	};

//	private void doAction(String message) {
//		byte[] content = null;
//
//		RobotAction action = new RobotAction();
//		if (TextUtils.equals("1", message)) {
//			Log.i("Move", "机器人移动方向:向前");
//			action.setCategory("move");
//			action.setAction("forward");
//			action.setDistance(10);
//			action.setTime(150);
//			String s = JSON.toJSONString(action);
//			content = s.getBytes();
//		} else if (TextUtils.equals("2", message)) {
//			Log.i("Move", "机器人移动方向:向后");
//			action.setCategory("move");
//			action.setAction("backward");
//			action.setDistance(10);
//			action.setTime(150);
//			String s = JSON.toJSONString(action);
//			content = s.getBytes();
//		} else if (TextUtils.equals("3", message)) {
//			Log.i("Move", "机器人移动方向:向左");
//			action.setCategory("move");
//			action.setAction("turnLeft");
//			action.setAngle(10);
//			action.setRadius(0);
//			action.setTime(150);
//			String s = JSON.toJSONString(action);
//			content = s.getBytes();
//		} else if (TextUtils.equals("4", message)) {
//			Log.i("Move", "机器人移动方向:向右");
//			action.setCategory("move");
//			action.setAction("turnRight");
//			action.setAngle(10);
//			action.setRadius(0);
//			action.setTime(150);
//			String s = JSON.toJSONString(action);
//			content = s.getBytes();
//		} else if (TextUtils.equals("5", message)) {
//			Log.i("Move", "机器人移动方向:停止");
//			action.setCategory("move");
//			action.setAction("stop");
//			String s = JSON.toJSONString(action);
//			content = s.getBytes();
//		}
//		byte[] end = new byte[] { 0x0a };//结束符
//		byte[] realcontent = byteMerger(content, end);
//		Intent intent = new Intent();
//		intent.setAction(BroadcastAction.ACTION_MOVE_TO_SERIALPORT);
//		intent.putExtra("actioncontent", realcontent);
//		sendBroadcast(intent);
//	}

	private void contrlToyCarMove(int directionType,int toyCarNum){
		if(directionType != 0){
			Log.i("Move", "控制机器人周围玩具toyCarNum===" + toyCarNum);
			RobotAction action = new RobotAction();
			action.setCategory("go");
			switch (directionType){
				case 1:
					Log.i("Move", "玩具控制 向前");
					action.setAction("forward");
					break;
				case 2:
					Log.i("Move", "玩具控制 向后");
					action.setAction("backward");
					break;
				case 3:
					Log.i("Move", "玩具控制 向左");
					action.setAction("turnLeft");
					break;
				case 4:
					Log.i("Move", "玩具控制 向右");
					action.setAction("turnRight");
					break;
				case 5:
					Log.i("Move", "玩具控制 停止");
					action.setAction("stop");
					break;
				default:
					break;
			}
			action.setCarNum(toyCarNum);
			String json = JSON.toJSONString(action);
			sendMoveAction(json);
		}
	}

	//控制摆臂
	private void handAction(String handDirection,String handCategory){
		RobotAction action = new RobotAction();
		action.setCategory("Hand");
		if(TextUtils.equals(handDirection, ScriptConfig.HAND_UP)){
			action.setAction("up");
		}else if(TextUtils.equals(handDirection,ScriptConfig.HAND_DOWN)){
			action.setAction("down");
		}else if(TextUtils.equals(handDirection,ScriptConfig.HAND_WAVING)){
			action.setAction("waving");
		}else if(TextUtils.equals(handDirection,ScriptConfig.HAND_STOP)){
			action.setAction("stop");
		}

		if(TextUtils.equals(handCategory,ScriptConfig.HAND_LEFT)){
			action.setSide("Left");
		}else if(TextUtils.equals(handCategory,ScriptConfig.HAND_RIGHT)){
			action.setSide("Right");
		}else if(TextUtils.equals(handCategory,ScriptConfig.HAND_TWO)){
			action.setSide("LandR");
		}
		String json = JSON.toJSONString(action);
		sendMoveAction(json);
	}

	//控制嘴的LED
	private void controlMouthLED(String LEDState){
		RobotAction action = new RobotAction();
		action.setCategory("LED");
		if(TextUtils.equals(LEDState, ScriptConfig.LED_ON)){
			action.setAction("ON");
		}else if(TextUtils.equals(LEDState,ScriptConfig.LED_OFF)){
			action.setAction("OFF");
		}else if(TextUtils.equals(LEDState,ScriptConfig.LED_BLINK)){
			action.setAction("blink");
		}
		String json = JSON.toJSONString(action);
		sendMoveAction(json);
	}

	private void sendMoveAction(String result){
		Log.i("Move", "json===" + result);
		if(!TextUtils.isEmpty(result)){
			byte[] content = result.getBytes();
			byte[] end = new byte[] { 0x0a };//结束符
			byte[] realcontent = byteMerger(content, end);
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_MOVE_TO_SERIALPORT);
			intent.putExtra("actioncontent", realcontent);
			sendBroadcast(intent);
		}
	}
	
	public byte[] byteMerger(byte[] first, byte[] second) {
		byte[] content = new byte[first.length + second.length];
		System.arraycopy(first, 0, content, 0, first.length);
		System.arraycopy(second, 0, content, first.length, second.length);
		return content;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}

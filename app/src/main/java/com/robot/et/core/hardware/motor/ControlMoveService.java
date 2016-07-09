package com.robot.et.core.hardware.motor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.robot.et.config.BroadcastAction;
import com.robot.et.entity.RobotAction;

public class ControlMoveService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE);
		filter.addAction(BroadcastAction.ACTION_WAKE_UP_AND_MOVE);
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		private String direction;
		private String tempDigit;

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE)) {
				direction = intent.getStringExtra("direction");// 获取方向
				if (null==direction||TextUtils.equals("", direction)) {
					return;
				}
				tempDigit=intent.getStringExtra("digit");
				if (null==tempDigit||TextUtils.equals("", tempDigit)) {
					return;
				}
				int digit = Integer.valueOf(tempDigit);// 获取数字数据
				Log.i("Move", "direction:" + direction + ",data:" + digit);
				int j = digit / 10;
				int circle = digit % 10 == 0 ? j : j + 1;
				Log.i("circle", "circle:"+circle);
				if (TextUtils.equals("1", direction) || TextUtils.equals("2", direction)) {
					for (int i = 0; i < circle; i++) {
						Log.i("circle", "circle:"+i);
						doAction(direction);
					}
				} else if (TextUtils.equals("3", direction) || TextUtils.equals("4", direction)) {
					for (int i = 0; i < circle; i++) {
						Log.i("circle", "circle:"+i);
						doAction(direction);
					}
				} else {
					doAction(direction);
				}   
			} else if (intent.getAction().equals(BroadcastAction.ACTION_WAKE_UP_AND_MOVE)) {
				doAction("5");//直接先停掉以前的运动
				int degree = intent.getIntExtra("degree", 0);//获取旋转角度
				Log.i("Move", "旋转角度：" + degree);
				if (degree == 0 || degree == 360) {
					// 原地不动
				} else if ((degree > 0 && degree < 180)) {
					direction = "4";
				} else {
					direction = "3";
					degree = 360 - degree;
				}
				int j = degree / 10;
				int circle = degree % 10 == 0 ? j : j + 1;
				if (TextUtils.equals("3", direction) || TextUtils.equals("4", direction)) {
					for (int i = 0; i < circle; i++) {
						doAction(direction);
					}
				}
			}
		}
	};

	private void doAction(String message) {
		byte[] content = null;

		RobotAction action = new RobotAction();
		if (TextUtils.equals("1", message)) {
			Log.i("Move", "机器人移动方向:向前");
			action.setCategory("move");
			action.setAction("forward");
			action.setDistance(10);
			action.setTime(150);
			String s = JSON.toJSONString(action);
			content = s.getBytes();
		} else if (TextUtils.equals("2", message)) {
			Log.i("Move", "机器人移动方向:向后");
			action.setCategory("move");
			action.setAction("backward");
			action.setDistance(10);
			action.setTime(150);
			String s = JSON.toJSONString(action);
			content = s.getBytes();
		} else if (TextUtils.equals("3", message)) {
			Log.i("Move", "机器人移动方向:向左");
			action.setCategory("move");
			action.setAction("turnLeft");
			action.setAngle(10);
			action.setRadius(0);
			action.setTime(150);
			String s = JSON.toJSONString(action);
			content = s.getBytes();
		} else if (TextUtils.equals("4", message)) {
			Log.i("Move", "机器人移动方向:向右");
			action.setCategory("move");
			action.setAction("turnRight");
			action.setAngle(10);
			action.setRadius(0);
			action.setTime(150);
			String s = JSON.toJSONString(action);
			content = s.getBytes();
		} else if (TextUtils.equals("5", message)) {
			Log.i("Move", "机器人移动方向:停止");
			action.setCategory("move");
			action.setAction("stop");
			String s = JSON.toJSONString(action);
			content = s.getBytes();
		}
		byte[] end = new byte[] { 0x0a };//结束符
		byte[] realcontent = byteMerger(content, end);
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_MOVE_TO_SERIALPORT);
		intent.putExtra("actioncontent", realcontent);
		sendBroadcast(intent);
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

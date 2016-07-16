package com.robot.et.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.debug.Logger;

public class BroadcastShare {

	//问科大讯飞
	public static void askIfly(String content) {
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_IFLY_TEXT_UNDERSTANDER);
		intent.putExtra("result", content);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
	}

	// 仅仅停止掉说话
	public static void stopSpeakOnly() {
		// 如果还在说话，停止掉
		if (DataConfig.isSpeaking) {
			Logger.i("停止掉说话");
			DataConfig.isSpeaking = false;
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_STOP_SPEAK_ONLY);
			Context context = CustomApplication.getInstance().getApplicationContext();
			context.sendBroadcast(intent);
		}
	}
	
	//仅仅停止掉音乐
	public static void stopMusicOnly(){
		if(DataConfig.isJpushStop){
			DataConfig.isJpushStop = false;
			stopSpeakOnly();
		}
		// 如果音乐还在播放，停止掉
		if (DataConfig.isPlayMusic) {
			Logger.i("停止掉音乐");
			DataConfig.isPlayMusic = false;
			DataConfig.isJpushStop = false;
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_STOP_MUSIC_ONLY);
			Context context = CustomApplication.getInstance().getApplicationContext();
			context.sendBroadcast(intent);
		}
	}
	
	//仅仅停止掉听外面的声音
	public static void stopListenerOnly(){
		// 如果还在听外面的声音，停止掉
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_STOP_LISTENER);
		Context context = CustomApplication.getInstance().getApplicationContext();
		context.sendBroadcast(intent);
	}

	// 把文字转化为语言
	public static void textToSpeak(int type, String content) {
		Context context = CustomApplication.getInstance().getApplicationContext();
		if (!TextUtils.isEmpty(content)) {
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_VOICE_TO_TEXT_SPEAK);
			intent.putExtra("result", content);
			intent.putExtra(DataConfig.TYPE_KEY, type);
			context.sendBroadcast(intent);
			Logger.i("对话发出广播");
		}
	}

	// 继续监听
	public static void resumeChat() {
		Context context = CustomApplication.getInstance().getApplicationContext();
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
		context.sendBroadcast(intent);
		Logger.i("继续监听发出广播");
	}

	// 释放录音设备
	public static void releaseRecord() {
		Context context = CustomApplication.getInstance().getApplicationContext();
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_PHONE_COMEIN);
		context.sendBroadcast(intent);
	}
	
	//连接agora
	public static void connectAgora(int type){
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_CONNECT_AGORA);
		intent.putExtra("type", type);
		Context context = CustomApplication.getInstance().getApplicationContext();
		context.sendBroadcast(intent);
	}
	
    //关闭agora界面
	public static void closeAgoraActivity() {
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_CLOSE_AGORA_ACTIVITY);
		Context context = CustomApplication.getInstance().getApplicationContext();
		context.sendBroadcast(intent);
	}

	//没有返回内容的时候，说的话
	public static void noResultToSpeak(){
		String tempContent = DataManager.getDataNoAnswer();
		textToSpeak(DataConfig.TYPE_VOICE_CHAT, tempContent);
	}

	//控制机器人走的广播带距离
	public static void controlMove(String direction,String distance){
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_DISTANCE);
		intent.putExtra("direction", direction);
		if(TextUtils.isEmpty(distance)){
			distance = "1";
		}
		intent.putExtra("digit", distance);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
	}

	//控制机器人走的广播不带距离
	public static void controlMove(String direction){
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE);
		intent.putExtra("direction", direction);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
	}

	//控制机器人周围小车走的广播
	public static void controlToyCarMove(String direction,int toyCarNum){
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_CONTROL_AROUND_TOYCAR);
		intent.putExtra("direction", direction);
		Log.i("netty", "执行控制周围玩具的动作命令toyCarNum==" + toyCarNum);
		intent.putExtra("toyCarNum", toyCarNum);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
	}

	//控制小车转圈
	public static void controlTurnAround(int turnDirection,String turnNum){
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_TURN);
		intent.putExtra("turnDirection", turnDirection);
		intent.putExtra("turnNum", turnNum);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
	}

	//机器人表情
	public static void controlRobotEmotion(int emotionKey){
		Log.i("netty", "controlRobotEmotion  emotionKey===" + emotionKey);
		if(emotionKey != 0){
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_EMOTION);
			intent.putExtra("emotion", emotionKey);
			CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
		}
	}

	//摆手
	public static void controlWaving(String handDirection,String handCategory,String num){
		if(!TextUtils.isEmpty(handDirection) && !TextUtils.isEmpty(handCategory)){
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_CONTROL_WAVING);
			intent.putExtra("handDirection", handDirection);
			intent.putExtra("handCategory", handCategory);
			intent.putExtra("num", num);
			CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
		}
	}

	//嘴巴的LED灯
	public static void controlMouthLED(String LEDState){
		if(!TextUtils.isEmpty(LEDState)){
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_CONTROL_MOUTH_LED);
			intent.putExtra("LEDState", LEDState);
			CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
		}
	}

	//跟随
	public static void controlFollow(String robotNum,int toyCarNum){
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_FOLLOW);
		intent.putExtra("robotNum", robotNum);
		intent.putExtra("toyCarNum", toyCarNum);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
	}

}

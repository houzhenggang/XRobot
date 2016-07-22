package com.robot.et.common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.debug.Logger;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

public class BroadcastShare {

	private static Intent intent;
	private static Context context;

	static {
		if(context == null){
			context = CustomApplication.getInstance().getApplicationContext();
			intent = new Intent();
		}
	}

	//问科大讯飞
	public static void askIfly(String content) {
		intent.setAction(BroadcastAction.ACTION_IFLY_TEXT_UNDERSTANDER);
		intent.putExtra("result", content);
		context.sendBroadcast(intent);
	}

	// 仅仅停止掉说话
	public static void stopSpeakOnly() {
		// 如果还在说话，停止掉
		if (DataConfig.isSpeaking) {
			Logger.i("停止掉说话");
			intent.setAction(BroadcastAction.ACTION_STOP_SPEAK_ONLY);
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
			intent.setAction(BroadcastAction.ACTION_STOP_MUSIC_ONLY);
			context.sendBroadcast(intent);
		}
	}
	
	//仅仅停止掉听外面的声音
	public static void stopListenerOnly(){
		// 如果还在听外面的声音，停止掉
		intent.setAction(BroadcastAction.ACTION_STOP_LISTENER);
		context.sendBroadcast(intent);
	}

	// 把文字转化为语言
	public static void textToSpeak(int type, String content) {
		if (!TextUtils.isEmpty(content)) {
			intent.setAction(BroadcastAction.ACTION_VOICE_TO_TEXT_SPEAK);
			intent.putExtra("result", content);
			intent.putExtra(DataConfig.TYPE_KEY, type);
			context.sendBroadcast(intent);
			Logger.i("对话发出广播");
		}
	}

	// 继续监听
	public static void resumeChat() {
		intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
		context.sendBroadcast(intent);
		Logger.i("继续监听发出广播");
	}

	// 释放录音设备
	public static void releaseRecord() {
		intent.setAction(BroadcastAction.ACTION_PHONE_COMEIN);
		context.sendBroadcast(intent);
	}
	
	//连接agora
	public static void connectAgora(int type){
		intent.setAction(BroadcastAction.ACTION_CONNECT_AGORA);
		intent.putExtra("type", type);
		context.sendBroadcast(intent);
	}
	
    //关闭agora界面
	public static void closeAgoraActivity() {
		intent.setAction(BroadcastAction.ACTION_CLOSE_AGORA_ACTIVITY);
		context.sendBroadcast(intent);
	}

	//控制机器人走的广播带距离
	public static void controlMove(String direction,String distance){
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_VOICE);
		intent.putExtra("direction", direction);
//		if(TextUtils.isEmpty(distance)){
//			distance = "1";
//		}
//		intent.putExtra("digit", distance);
		context.sendBroadcast(intent);
	}

	//控制机器人走的广播不带距离
	public static void controlMove(String direction){
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE_WITH_NETTY);
		intent.putExtra("direction", direction);
		context.sendBroadcast(intent);
	}

	//控制机器人周围小车走的广播
	public static void controlToyCarMove(String direction,int toyCarNum){
		intent.setAction(BroadcastAction.ACTION_CONTROL_AROUND_TOYCAR);
		intent.putExtra("direction", direction);
		Log.i("netty", "执行控制周围玩具的动作命令toyCarNum==" + toyCarNum);
		intent.putExtra("toyCarNum", toyCarNum);
		context.sendBroadcast(intent);
	}

	//控制小车转圈
	public static void controlTurnAround(int turnDirection,String turnNum){
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_TURN);
		intent.putExtra("turnDirection", turnDirection);
		intent.putExtra("turnNum", turnNum);
		context.sendBroadcast(intent);
	}

	//机器人表情
	public static void controlRobotEmotion(int emotionKey){
		Log.i("netty", "controlRobotEmotion  emotionKey===" + emotionKey);
		if(emotionKey != 0){
			intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_EMOTION);
			intent.putExtra("emotion", emotionKey);
			context.sendBroadcast(intent);
		}
	}

	//摆手
	public static void controlWaving(String handDirection,String handCategory,String num){
		if(!TextUtils.isEmpty(handDirection)){
			intent.setAction(BroadcastAction.ACTION_CONTROL_WAVING);
			intent.putExtra("handDirection", handDirection);
			if(TextUtils.isEmpty(handCategory)){
				handCategory = ScriptConfig.HAND_TWO;
			}
			intent.putExtra("handCategory", handCategory);
			intent.putExtra("num", num);
			context.sendBroadcast(intent);
		}
	}

	//嘴巴的LED灯
	public static void controlMouthLED(String LEDState){
		if(!TextUtils.isEmpty(LEDState)){
			intent.setAction(BroadcastAction.ACTION_CONTROL_MOUTH_LED);
			intent.putExtra("LEDState", LEDState);
			context.sendBroadcast(intent);
		}
	}

	//跟随
	public static void controlFollow(String robotNum,int toyCarNum){
		intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_FOLLOW);
		intent.putExtra("robotNum", robotNum);
		intent.putExtra("toyCarNum", toyCarNum);
		context.sendBroadcast(intent);
	}

	//重新连接netty
	public static void connectNettyArgin(){
		intent.setAction(BroadcastAction.ACTION_OPEN_NETTY);
		intent.putExtra("robotNum", SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, ""));
		context.sendBroadcast(intent);
	}

}

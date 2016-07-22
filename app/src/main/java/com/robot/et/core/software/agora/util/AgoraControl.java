package com.robot.et.core.software.agora.util;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.core.software.agora.ChannelActivity;
import com.robot.et.entity.JpushInfo;
import com.robot.et.util.BroadcastShare;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

public class AgoraControl {

	//进入声网
	public static void openChannelActivity (int type) {
		Context context = CustomApplication.getInstance().getApplicationContext();
		Intent intent = new Intent(context, ChannelActivity.class);
		intent.putExtra(DataConfig.AGORA_EXTRA_CALLING_TYPE, type);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(DataConfig.AGORA_EXTRA_VENDOR_KEY, DataConfig.AGORA_KEY);
		intent.putExtra(DataConfig.AGORA_EXTRA_CHANNEL_ID,SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.AGORA_ROOM_NUM, ""));
		context.startActivity(intent);
	}
	
	//关闭当前声网
	public static void closeChannel(){
		if(ChannelActivity.instance != null){
			ChannelActivity.instance.finish();
			ChannelActivity.instance = null;
		}
	}
	
	//加入agora房间
	public static void joinAgoraRoom(JpushInfo info){
		int extra = info.getExtra();
		String roomNum = info.getRoomNum();
		int agoraType = SharedPreferencesUtils.getInstance().getInt(SharedPreferencesKeys.AGORA_CALL_PATTERN, 0);
		Log.i("agoravideo", "AgoraControl extra===" + extra);
		Log.i("agoravideo", "AgoraControl agoraType===" + agoraType);
		BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");

		switch (extra) {
		case DataConfig.JPUSH_CALL_VIDEO:// agora视频
			if(agoraType == DataConfig.AGORA_CALL_NORMAL_PATTERN){//正常模式
				joinRoomerBegin(roomNum);
				SystemClock.sleep(500);
				DataConfig.isAgoraVideo = true;
				BroadcastShare.textToSpeak(DataConfig.JPUSH_CALL_VIDEO, info.getContent());
				Log.i("agoravideo", "AgoraControl 发出提示广播请求");
			}
			
			break;
		case DataConfig.JPUSH_CALL_VOICE:// agora语音
			if(agoraType == DataConfig.AGORA_CALL_NORMAL_PATTERN){//正常模式
				joinRoomerBegin(roomNum);
				SystemClock.sleep(500);
				DataConfig.isAgoraVoice = true;
				BroadcastShare.textToSpeak(DataConfig.JPUSH_CALL_VOICE, info.getContent());
			}
			
			break;
		case DataConfig.JPUSH_CALL_LOOK:// agora查看
			if(agoraType == DataConfig.AGORA_CALL_NORMAL_PATTERN){//正常模式
				joinRoomerBegin(roomNum);
				openChannelActivity(DataConfig.JPUSH_CALL_LOOK);
			}
			
			break;
		case DataConfig.JPUSH_CALL_CLOSE:// 关闭agora声网
			Log.i("agoravideo", "关闭agora声网");
			if(ChannelActivity.instance != null){
				Intent intent = new Intent();
				intent.setAction(BroadcastAction.ACTION_CLOSE_AGORA);
				CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
			}
			
			break;

		default:
			break;
		}
	}
	
	private static void joinRoomerBegin(String roomNum){
		BroadcastShare.stopSpeakOnly();
		BroadcastShare.stopListenerOnly();
		BroadcastShare.stopMusicOnly();
		closeChannel();
		SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
		share.putString(SharedPreferencesKeys.AGORA_ROOM_NUM, roomNum);
		share.putInt(SharedPreferencesKeys.AGORA_CALL_TYPE, DataConfig.PHONE_CALL_BY_MEN);
		share.commitValue();
	}
	
}

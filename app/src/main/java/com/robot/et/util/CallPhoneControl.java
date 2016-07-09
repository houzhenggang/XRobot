package com.robot.et.util;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.DataConfig;
import com.robot.et.core.software.okhttp.util.HttpUtils;

public class CallPhoneControl {
	
	//语音控制打电话
	public static void callBySpeak(String userName, String result) {
		if (!TextUtils.isEmpty(result)) {
			String roomNum = GsonParse.getRoomNum(result);
			Log.i("json", "getRoomNum()  roomNum==" + roomNum);
			if (!TextUtils.isEmpty(roomNum)) {
				SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
				share.putString(SharedPreferencesKeys.AGORA_ROOM_NUM, roomNum);
				share.putInt(SharedPreferencesKeys.AGORA_CALL_TYPE,DataConfig.PHONE_CALL_TO_MEN);
				share.commitValue();
				String content = "";
				if (TextUtils.isDigitsOnly(userName)) {// 是电话号码
					content = DataManager.getContentSrc();
				} else {// 是用户名
					content = userName;
				}
				Log.i("json", "getRoomNum()  正在给xxx打电话==" + content);
				BroadcastShare.releaseRecord();
				BroadcastShare.closeAgoraActivity();
				SystemClock.sleep(500);
				DataConfig.isAgoraVideo = true;
				BroadcastShare.textToSpeak(DataConfig.JPUSH_CALL_VIDEO,"正在给" + content + "打电话，" + "要耐心等待哦");
			} else {
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT,DataConfig.CALL_NO_PHONENUM);
			}
		} else {
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT,DataConfig.CALL_NO_PHONENUM);
		}
	}
	
	//打电话之前先获取对方的房间号
	public static void getRoomNum(String userName){
		HttpUtils.getInstance().getRoomNum(userName);
	}
	
	// 修改机器人免打扰的状态值  (修改是否可以接听外来的电话)
	public static void changeRobotCallStatus(String status, String content) {
		HttpUtils.getInstance().changeRobotCallStatus(status, content);
	}
	
}

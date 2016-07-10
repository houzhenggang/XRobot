package com.robot.et.util;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.DataConfig;
import com.robot.et.config.UrlConfig;
import com.robot.et.core.software.okhttp.HttpEngine;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

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
	public static void getRoomNum(final String userName){
		HttpEngine.Param[] params = new HttpEngine.Param[]{
				new HttpEngine.Param("username", userName),
				new HttpEngine.Param("robotNumber", SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new HttpEngine.Param("requestType", String.valueOf(DataConfig.JPUSH_CALL_VIDEO))
		};
		HttpEngine httpEngine = HttpEngine.getInstance();
		Request request = httpEngine.createRequest(UrlConfig.GET_AGORA_ROOMNUM, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Request response, IOException arg1) {
			}
			@Override
			public void onResponse(Response response) throws IOException {
				String result = response.body().string();
				Log.i("json", "getRoomNum()  result==" + result);
				if(!TextUtils.isEmpty(result)){
					callBySpeak(userName, result);
				}
			}

		});
	}
	
	// 修改机器人免打扰的状态值  (修改是否可以接听外来的电话)
	public static void changeRobotCallStatus(final String status, final String content) {
		HttpEngine.Param[] params = new HttpEngine.Param[]{
				new HttpEngine.Param("robotNumber", SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new HttpEngine.Param("robotStatus", status)
		};
		HttpEngine httpEngine = HttpEngine.getInstance();
		Request request = httpEngine.createRequest(UrlConfig.ROBOT_DISTURB_URL, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Request arg0, IOException arg1) {
			}
			@Override
			public void onResponse(Response response) throws IOException {
				String result = response.body().string();
				Log.i("json", "result====" + result);
				if(GsonParse.isChangeStatusSuccess(result)){
					SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
					if(TextUtils.equals(status, DataConfig.ROBOT_STATUS_NORMAL)){//正常模式
						share.putInt(SharedPreferencesKeys.AGORA_CALL_PATTERN, DataConfig.AGORA_CALL_NORMAL_PATTERN);
					}else if(TextUtils.equals(status, DataConfig.ROBOT_STATUS_DISYURB_NOT)){//免打扰模式
						share.putInt(SharedPreferencesKeys.AGORA_CALL_PATTERN, DataConfig.AGORA_CALL_DISTURB_NOT_PATTERN);
					}
					share.commitValue();
					BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);
				}
			}

		});
	}

	//通知app关闭agora视频
	public static void notifyAppCloseAgora(String status){

		HttpEngine.Param[] params = new HttpEngine.Param[]{
				new HttpEngine.Param("username", SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, "")),
				new HttpEngine.Param("robotNumber", SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new HttpEngine.Param("requestType", status)
		};
		HttpEngine httpEngine = HttpEngine.getInstance();
		Request request = httpEngine.createRequest(UrlConfig.GET_AGORA_ROOMNUM, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Request arg0, IOException arg1) {
			}
			@Override
			public void onResponse(Response response) throws IOException {
				String result = response.body().string();
				Log.i("json", "result====" + result);
				if(GsonParse.isChangeStatusSuccess(result)){
					Log.i("json", "网速不好关闭agora成功");
				}
			}

		});
	}
	
}

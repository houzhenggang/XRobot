package com.robot.et.core.software.okhttp.util;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.UrlConfig;
import com.robot.et.core.software.okhttp.HttpEngine;
import com.robot.et.core.software.okhttp.HttpEngine.Param;
import com.robot.et.entity.RobotInfo;
import com.robot.et.util.BroadcastShare;
import com.robot.et.util.CallPhoneControl;
import com.robot.et.util.GsonParse;
import com.robot.et.util.GsonParse.RobotInfoCallBack;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class HttpUtils {
	private static HttpUtils instance = null;
	private HttpEngine httpEngine;
	private SharedPreferencesUtils share;
	
	private HttpUtils(){
		httpEngine = HttpEngine.getInstance();
		httpEngine.initContext(CustomApplication.getInstance().getApplicationContext());
		share = SharedPreferencesUtils.getInstance();
	}
	
	public synchronized static HttpUtils getInstance(){
		if(instance == null){
			instance = new HttpUtils();
		}
		return instance;
	}

	//初始化获取机器人的信息
	public void getRobotInfo(String url,final String deviceId) {
		Param[] params = new Param[]{
				new Param("deviceId", deviceId),
		};
		Request request = httpEngine.createRequest(url,params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Request request, IOException arg1) {
				Log.i("json", "getRobotInfo()  onFailure");
			}

			@Override
			public void onResponse(Response request) throws IOException {
				String json = request.body().string();
				Log.i("json", "getRobotInfo()  json==" + json);
				if (!TextUtils.isEmpty(json)) {
					GsonParse.getRobotInfo(json, new RobotInfoCallBack() {

						@Override
						public void getRobotInfo(String message, RobotInfo info) {
							if (info != null) {
								String robotNum = info.getRobotNum();
								if (!TextUtils.isEmpty(robotNum)) {
									share.putString(SharedPreferencesKeys.ROBOT_NUM,robotNum);
									share.commitValue();
									Intent intent = new Intent();
									intent.setAction(BroadcastAction.ACTION_OPEN_NETTY);
									intent.putExtra("robotNum", robotNum);
									CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
								}
							}else{
								HttpUtils.getInstance().getRobotInfo(UrlConfig.GET_ROBOT_INFO_START,deviceId);
							}
						}
					});
				}
			}

		});
	}
	
	//获取通话的房间号
	public void getRoomNum(final String userName){
		Param[] params = new Param[]{
				new Param("username", userName),
				new Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new Param("requestType", String.valueOf(DataConfig.JPUSH_CALL_VIDEO))
		};
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
					CallPhoneControl.callBySpeak(userName, result);
				}
			}
			
		});
	}
	
	//修改机器人免打扰的状态值
	public void changeRobotCallStatus(final String status,final String content){
		Param[] params = new Param[]{
				new Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new Param("robotStatus", status)
		};
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
	
	//关闭agora
	public void closeAgora(String status){
		Param[] params = new Param[]{
				new Param("username", share.getString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, "")),
				new Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new Param("requestType", status)
		};
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
	
	//向APP发送媒体状态
	public void pushMediaState(String meidaType,String mediaState,String playName){
		Param[] params = new Param[]{
				new Param("mobile", share.getString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, "")),
				new Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new Param("mediaType", meidaType),
				new Param("mediaState", mediaState),
				new Param("playName", playName)
		};
		Request request = httpEngine.createRequest(UrlConfig.PUSH_MEDIASTATE_TO_APP, params);
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
					Log.i("json", "向APP发送媒体状态成功");
				}
				Intent intent = new Intent();
				intent.setAction(BroadcastAction.ACTION_OPEN_NETTY);
				intent.putExtra("robotNum", share.getString(SharedPreferencesKeys.ROBOT_NUM, ""));
				CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
			}
			
		});
	}
	
}

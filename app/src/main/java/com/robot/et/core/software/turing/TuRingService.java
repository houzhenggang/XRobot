package com.robot.et.core.software.turing;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.debug.Logger;
import com.robot.et.util.BroadcastShare;
import com.robot.et.util.DataManager;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;

public class TuRingService extends Service {

	private TuringApiManager mTuringApiManager;
	private int type;
	private String city,area;
	private boolean isError;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("TuRingService  onCreate()");
		// turingSDK初始化
		SDKInitBuilder builder = new SDKInitBuilder(this).setSecret(DataConfig.TURING_SECRET)
				.setTuringKey(DataConfig.TURING_APPID).setUniqueId(DataConfig.TURING_UNIQUEID);
		
		SDKInit.init(builder,new InitListener() {
			@Override
			public void onFail(String error) {
				Logger.i("图灵error==="+error);
				isError = true;
				//异常处理 异常后重新去初始化
				startService(new Intent(TuRingService.this, TuRingService.class));
			}
			@Override
			public void onComplete() {
				// 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
				mTuringApiManager = new TuringApiManager(TuRingService.this);
				mTuringApiManager.setHttpListener(myHttpConnectionListener);
			}
		});
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_TURING_RECEIVER);
		registerReceiver(receiver, filter);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BroadcastAction.ACTION_TURING_RECEIVER)){
				String result = intent.getStringExtra("result");
				if(!TextUtils.isEmpty(result)){
					type = intent.getIntExtra(DataConfig.TYPE_KEY, 0);
					if(type == DataConfig.TYPE_WELCOME_CONTENT){//天气
						city = intent.getStringExtra("city");
						area = intent.getStringExtra("area");
					}
					//发出请求  以防mTuringApiManager为null
					if(mTuringApiManager != null){
						mTuringApiManager.requestTuringAPI(result);
					}
				}else{
					BroadcastShare.noResultToSpeak();
				}
			}
		}
	};
	
	HttpConnectionListener myHttpConnectionListener = new HttpConnectionListener() {

		@Override
		public void onSuccess(RequestResult result) {
			if (result != null) {
				try {
					JSONObject result_obj = new JSONObject(result.getContent().toString());
					if (result_obj.has("text")) {
						String content = (String) result_obj.get("text");
						Log.i("voiceresult", "图灵content===="+ content);
						if(type == DataConfig.TYPE_WELCOME_CONTENT){//天气
							BroadcastShare.textToSpeak(DataConfig.TYPE_TURING_WEATHER, DataManager.getWeatherContent(city,area, content));
							
						}else if(type == DataConfig.TYPE_VOICE_CHAT){//对话
							if(content.contains(":") && content.contains("周") && content.contains("风") && content.contains(";")){//从科大讯飞没有获取到天气问图灵
								Log.i("voiceresult", "从科大讯飞没有获取到天气问图灵");
								content = DataManager.getWeatherContent("","", content);
							}

							BroadcastShare.textToSpeak(DataConfig.TYPE_RESUME_CHAT, content);
							
						}
					} else{
						BroadcastShare.noResultToSpeak();
					}
				} catch (JSONException e) {
					Logger.i("图灵JSONException====" + e.getMessage());
					BroadcastShare.noResultToSpeak();
				}
			}
		}

		@Override
		public void onError(ErrorMessage errorMessage) {
			Logger.i("图灵errorMessage.getMessage()====" + errorMessage.getMessage());
			BroadcastShare.noResultToSpeak();
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		if(isError){
			isError = false;
			stopService(new Intent(TuRingService.this, TuRingService.class));
		}
	}

}

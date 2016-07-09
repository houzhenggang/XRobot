package com.robot.et.core.software.iflytek;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.core.software.iflytek.util.IflyUtils;
import com.robot.et.core.software.zxing.ScanCodeActivity;
import com.robot.et.debug.Logger;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.BroadcastShare;
import com.robot.et.util.CharactorTool;
import com.robot.et.util.DataManager;
import com.robot.et.util.PlayerControl;
import com.robot.et.util.ScriptManager;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

public class IflySpeakService extends Service {
	// 语音合成对象
	private SpeechSynthesizer mTts;
	private int currentType;
	private String city,area;
	private String alarmContent;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("IflySpeakService  onCreate()");
		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_VOICE_TO_TEXT_SPEAK);
		filter.addAction(BroadcastAction.ACTION_PHONE_HANGUP);
		filter.addAction(BroadcastAction.ACTION_STOP_SPEAK_ONLY);
		filter.addAction(BroadcastAction.ACTION_GET_LOCATION);
		registerReceiver(receiver, filter);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.ACTION_VOICE_TO_TEXT_SPEAK)) {// 对话发来的广播
				String voiceText = intent.getStringExtra("result");
				int currentType = intent.getIntExtra(DataConfig.TYPE_KEY, 0);
				Logger.i("voiceText====" + voiceText);
				if (!TextUtils.isEmpty(voiceText)) {
					if(currentType == DataConfig.TYPE_REMIND_TIPS){//提醒
						Logger.i("DataConfig.isAlarmTips====" + DataConfig.isAlarmTips);
						if(DataConfig.isAlarmTips){//可以闹铃提醒
							//如果还在监听停止掉
							BroadcastShare.stopListenerOnly();
							
							// 如果正在说话，停止掉
							if (DataConfig.isSpeaking) {
								DataConfig.isSpeaking = false;
								if(mTts.isSpeaking()){
									mTts.stopSpeaking();
								}
							}
							// 如果正在播放音乐，停止掉
							BroadcastShare.stopMusicOnly();
							
						}else{//正在语音或视频不提醒
							Logger.i("正在语音或视频不提醒");
							alarmContent = voiceText;
							return;
						}
					}
				
					boolean isConnectNetWork = false;
					if(currentType == DataConfig.TYPE_NETWORK_BREAKOFF){//网络异常
						isConnectNetWork = false;
					}else {//网络正常
						isConnectNetWork = true;
					}
					speak(currentType,voiceText,isConnectNetWork);

				}else{
					Logger.i("内容为空，继续监听");
					intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
					sendBroadcast(intent);
				}

			} else if (intent.getAction().equals(BroadcastAction.ACTION_PHONE_HANGUP)) {// 查看的时候电话挂断的广播
				Logger.i("电话挂断重新进入沉睡状态");
				// do noThing
			} else if (intent.getAction().equals(BroadcastAction.ACTION_STOP_SPEAK_ONLY)) {// 只把正在说话的动作停止掉
				//停止说话
				Logger.i("把正在说话的动作停止掉");
				if(mTts.isSpeaking()){
					mTts.stopSpeaking();
				}
			} else if (intent.getAction().equals(BroadcastAction.ACTION_GET_LOCATION)) {// 百度地图定位到位置
				SharedPreferencesUtils sharedUtils = SharedPreferencesUtils.getInstance();
				city = sharedUtils.getString(SharedPreferencesKeys.CITY_KEY, "");
				area = sharedUtils.getString(SharedPreferencesKeys.AREA_KEY, "");
				
				speak(DataConfig.TYPE_WELCOME_CONTENT, DataManager.getWelcomeContent(),true);
				
			}
		}
	};

	private void speak(int speakType,String content,boolean isTypeCloud) {
		Logger.i("接受   speak()");
		currentType = speakType;
		Logger.i("speak()  currentType===== "+currentType);
		String speakMen = "";
		if(isTypeCloud){
			if(DataConfig.isLanguageSwitch){
				content = CharactorTool.getFullSpell(content);
				speakMen = DataConfig.VOICER_TIPS_DEFAULT_ENGLISH;
			}else{
				speakMen = DataConfig.VOICER_TIPS_DEFAULT;
			}
		}else {
			speakMen = DataConfig.VOICER_TIPS_LOCAL;
		}
		IflyUtils.setTextToVoiceParam(mTts,isTypeCloud,speakMen,"50","50","50");

		int code = mTts.startSpeaking(content, mTtsListener);
		// * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
		// * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
		// String path =
		// Environment.getExternalStorageDirectory()+"/tts.pcm";
		// int code = mTts.synthesizeToUri(text, path, mTtsListener);

		if (code != ErrorCode.SUCCESS) {
			if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
				// 未安装则跳转到提示安装页面
			} else {
				Logger.i("语音合成失败,错误码: " + code);
			}
		}
	}
	
	// 初始化监听
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				// 初始化失败,错误码
			} else {
				// 初始化成功，之后可以调用startSpeaking方法
				// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
				// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}
		}
	};
	
	// 合成回调监听
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		// 开始播放
		@Override
		public void onSpeakBegin() {
			Logger.i("语音合成 开始播放");
			DataConfig.isSpeaking = true;
		}
		// 暂停播放
		@Override
		public void onSpeakPaused() {
		}
		// 继续播放
		@Override
		public void onSpeakResumed() {
		}
		// 合成进度
		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
		}
		// 播放进度
		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
		}
		@Override
		public void onCompleted(SpeechError error) {
			//说话结束
			DataConfig.isSpeaking = false;
			if (error == null) {
				Logger.i("播放完成 ");
				Logger.i("currentType===== "+currentType);
				Intent intent = new Intent();
				switch (currentType) {
				case DataConfig.TYPE_VOICE_CHAT:// 对话
					if(DataConfig.isBluetoothBox){
						return;
					}
					intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
					sendBroadcast(intent);
					break;
				case DataConfig.TYPE_REMIND_TIPS:// 提醒
					Logger.i("iflyspeakservice   提醒的话语说完了");
					Logger.i("DataConfig.isAgoraLook====" + DataConfig.isAgoraLook);
					if(DataConfig.isAgoraLook){
						//正在agora查看模式，不做任何操作
						AlarmRemindManager.doMoreAlarm();
					}else{
						AlarmRemindManager.doMoreAlarm();
						//继续监听
						intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
						sendBroadcast(intent);
					}
					break;
				case DataConfig.TYPE_MUSIC_PLAY_START:// 音乐开始播放前的提示
					PlayerControl.startPlayMusic(DataManager.getContentSrc());
					ScriptManager.playScript(PlayerControl.getCurrentPlayName());
					break;
				case DataConfig.TYPE_WELCOME_CONTENT:// 第一次欢迎语说完去查天气
					if (DataConfig.isUseIfly) {
						// 查科大讯飞
						BroadcastShare.getWeatherByIfly("今天" + city + area + "的天气");
					} else {
						// 查图灵
						intent.setAction(BroadcastAction.ACTION_TURING_RECEIVER);
						intent.putExtra("result", "今天" + city + area + "的天气");
						intent.putExtra("city", city);
						intent.putExtra("area", area);
						intent.putExtra(DataConfig.TYPE_KEY,DataConfig.TYPE_WELCOME_CONTENT);
						sendBroadcast(intent);
					}
					break;
				case DataConfig.TYPE_TURING_WEATHER:// 查图灵天气完去登录
					Logger.i("开始听外面的声音");
					intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
					sendBroadcast(intent);
					break;
				case DataConfig.TYPE_RESUME_CHAT:// 去图灵查内容之后
					if(DataConfig.isBluetoothBox){
						return;
					}

					if (DataConfig.isPlayMusic) {

					} else {
						intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
						sendBroadcast(intent);
					}
					break;
				case DataConfig.TYPE_NETWORK_CONNECT:// 网络连接
					Logger.i("iflyspeakservice   网络连接");
					DataConfig.isConnectNetWork = true;
					break;
				case DataConfig.TYPE_NETWORK_BREAKOFF:// 网络异常
					Logger.i("iflyspeakservice   网络异常");
					DataConfig.isConnectNetWork = false;
					DataConfig.isPlayScript = false;
					intent.setAction(BroadcastAction.ACTION_MONITOR_WATCH_NETWORK_DISCONNECT);
					sendBroadcast(intent);
					intent.setClass(IflySpeakService.this, ScanCodeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				case DataConfig.TYPE_CALL_PHONE:// 打电话前的提示
					intent.setAction(BroadcastAction.ACTION_CALL_PHONE);
					sendBroadcast(intent);
					break;
				case DataConfig.TYPE_PHONE_ERROR:// 呼叫电话失败的提示
					intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
					sendBroadcast(intent);
					break;
				case DataConfig.TYPE_AGORA_HANGUP:// agora视频或者语音挂断
					Log.i("agoravideo", "agora视频语音挂断");
					if(!TextUtils.isEmpty(alarmContent)){//没有提示的闹铃继续提示完
						BroadcastShare.textToSpeak(DataConfig.TYPE_DO_NOTHING, alarmContent);
						alarmContent = "";
					}
					break;
				case DataConfig.TYPE_DO_NOTHING:// 不处理任何的动作
					Logger.i("iflyspeakservice  不处理任何的动作");
					// do noThing
					break;
				case DataConfig.TYPE_SCRIPT:// 剧本的表演
					Logger.i("iflyspeakservice  剧本的表演");
					ScriptManager.setNewScriptInfos(ScriptManager.getScriptActionInfos(),true);

					break;
				case DataConfig.JPUSH_CALL_VIDEO:// agora视频
					BroadcastShare.connectAgora(DataConfig.JPUSH_CALL_VIDEO);
					break;
				case DataConfig.JPUSH_CALL_VOICE:// agora语音
					BroadcastShare.connectAgora(DataConfig.JPUSH_CALL_VOICE);
					break;

				default:
					break;
				}
				
			} else if (error != null) {
				Logger.i("onCompleted  error=" + error.getPlainDescription(true));
				//语音合成时，科大讯飞网络异常的话，保证视频音频可以用
				switch (currentType){
					case DataConfig.JPUSH_CALL_VIDEO:// agora视频
						BroadcastShare.connectAgora(DataConfig.JPUSH_CALL_VIDEO);
						break;
					case DataConfig.JPUSH_CALL_VOICE:// agora语音
						BroadcastShare.connectAgora(DataConfig.JPUSH_CALL_VOICE);
						break;
					default:
						Intent intent = new Intent();
						intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
						sendBroadcast(intent);
						break;
				}
			}
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mTts.isSpeaking()){
			mTts.stopSpeaking();
		}
		// 退出时释放连接
		mTts.destroy();
		unregisterReceiver(receiver);
	}

}

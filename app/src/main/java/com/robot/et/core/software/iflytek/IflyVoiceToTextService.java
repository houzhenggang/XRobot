package com.robot.et.core.software.iflytek;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.robot.et.R;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.core.software.iflytek.util.IflyUtils;
import com.robot.et.core.software.system.MusicPlayerService;
import com.robot.et.debug.Logger;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.BroadcastShare;
import com.robot.et.util.DataManager;
import com.robot.et.util.GsonParse;
import com.robot.et.util.PlayerControl;
import com.robot.et.util.ScriptManager;
import com.robot.et.util.Utilities;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class IflyVoiceToTextService extends Service {
	// 语音听写对象
	private SpeechRecognizer mIat;
	// 用HashMap存储听写结果
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	private int ret = 0; // 函数调用返回值

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("IflyVoiceToTextService   onCreate()");
		// 初始化SpeechRecognizer对象
		mIat = SpeechRecognizer.createRecognizer(this, mTtsInitListener);
		
		uploadUserThesaurus();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
		filter.addAction(BroadcastAction.ACTION_PHONE_COMEIN);
		filter.addAction(BroadcastAction.ACTION_WAKE_UP_AND_MOVE);
		filter.addAction(BroadcastAction.ACTION_MUSIC_PLAY_END);
		filter.addAction(BroadcastAction.ACTION_MUSIC_PLAY);
		filter.addAction(BroadcastAction.ACTION_STOP_LISTENER);
		registerReceiver(receiver, filter);
		
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.ACTION_RESUME_MONITOR_CHAT)) {// 对话监听
				Log.i("voice", "开始监听" );
				listenBegin();
			} else if (intent.getAction().equals(BroadcastAction.ACTION_PHONE_COMEIN)) {// 电话或者视频进来停掉监听
				Logger.i("电话进来");
				closeAnyDec();
				DataManager.initBooleanValue();
			} else if (intent.getAction().equals(BroadcastAction.ACTION_WAKE_UP_AND_MOVE)) {// 唤醒或者中断的处理
				Log.i("voiceresult", "唤醒或者中断的处理" );
				DataManager.initBooleanValue();

				if(DataConfig.isAgoraVideo){
					BroadcastShare.connectAgora(DataConfig.JPUSH_CALL_VIDEO);
					return;
				}
				
				if(DataConfig.isAgoraVoice){
					BroadcastShare.connectAgora(DataConfig.JPUSH_CALL_VOICE);
					return;
				}
				
				if(DataConfig.isPlayMusic){//如果是极光推送播放的音乐通知APP媒体状态改变
					if(DataConfig.isJpushPlayMusic){
						PlayerControl.pushMediaState("", "close", "");
					}
				}
				
				closeAnyDec();
				String[] wakeUpSpeakContent= getResources().getStringArray(R.array.wake_up_speak_content);
				int i=new Random().nextInt(wakeUpSpeakContent.length);
				String content=wakeUpSpeakContent[i];
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);
			} else if (intent.getAction().equals(BroadcastAction.ACTION_MUSIC_PLAY_END)) {// 音乐播放完成
				Logger.i("音乐播放完成");
				BroadcastShare.controlMouthLED(ScriptConfig.LED_OFF);

				if(DataConfig.isScriptPlayMusic){//播放的剧本里的音乐
					DataConfig.isScriptPlayMusic = false;
					return;
				}

				if(DataConfig.isJpushPlayMusic){//极光推送播完音乐自动播放下一首
					intent.setAction(BroadcastAction.ACTION_PLAY_LOWER);
					sendBroadcast(intent);
					return;
				}
				DataConfig.isStartTime = false;
				listenBegin();
			} else if (intent.getAction().equals(BroadcastAction.ACTION_MUSIC_PLAY)) {// 开始播放音乐
				Logger.i("播放音乐");
				if(DataConfig.isAppPushRemind){//APP推送的提醒
					DataConfig.isScriptPlayMusic = false;
				}
				intent.setClass(IflyVoiceToTextService.this,MusicPlayerService.class);
				intent.putExtra("url", intent.getStringExtra("url"));
				startService(intent);
			} else if (intent.getAction().equals(BroadcastAction.ACTION_STOP_LISTENER)) {// 停止监听对话
				Logger.i("停止监听对话");
				if(mIat.isListening()){
					mIat.cancel();
				}
			}
		}
	};
	
	//关闭掉说话，唱歌，听等操作
	private void closeAnyDec(){
		if(mIat.isListening()){
			mIat.cancel();
		}
		BroadcastShare.stopSpeakOnly();
		BroadcastShare.stopMusicOnly();
	}
	
	//开启听
	private void listenBegin(){
		if(DataConfig.isAppPushRemind){
			AlarmRemindManager.noResponseAppRemind();
		}
	
		listen(true,DataConfig.VOICER_TIPS_DEFAULT);
	}
	
	private void listen(boolean isTypeCloud,String language) {
		mIatResults.clear();
		// 设置参数
		IflyUtils.setVoiceToTextParam(mIat,isTypeCloud,language);
		// 不显示听写对话框
		ret = mIat.startListening(mRecognizerListener);

		if (ret != ErrorCode.SUCCESS) {
			Log.i("voice", "听写失败,错误码：" + ret);
		} else {
			Log.i("voice", "开始听写");
		}
	}
	
	 //听写监听器
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			// 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
			Log.i("voice", "开始说话 " );
			BroadcastShare.controlMouthLED(ScriptConfig.LED_ON);
		}

		@Override
		public void onError(SpeechError error) {
			Log.i("voice", "onError " );
			// 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
			// 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
			if(mIat.isListening()){
				mIat.cancel();
			}
			listenBegin();
		}

		@Override
		public void onEndOfSpeech() {
			// 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
			Log.i("voice", "结束说话 " );
			BroadcastShare.controlMouthLED(ScriptConfig.LED_OFF);
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			Log.i("voice", "onResult " );

			// 有人说话 
			String result = GsonParse.printResult(results,mIatResults);
			Log.i("voiceresult", "问题原版result====" + result);
			Toast.makeText(IflyVoiceToTextService.this, "问题原版result====" + result, Toast.LENGTH_SHORT).show();
			if(isLast){
				Log.i("voiceresult", "onResult  isLast" );
				if(mIat.isListening()){
					mIat.cancel();
				}
				
				if(!TextUtils.isEmpty(result)){
					//如果只有一个字的话，直接继续听
					char[] datas = result.toCharArray();
					if(datas.length == 1){
						listenBegin();
						return;
					}
					//APP提醒必须要说的话
					if(DataConfig.isAppPushRemind){
						AlarmRemindManager.handleAppRemind(result);
						return;
					}

					//APP发来的是剧本的问答
					if(DataConfig.isScriptQA){
						ScriptManager.handleAppScriptQA(result);
						return;
					}

					//做自定义的事情
					if(IflyUtils.doCustomAction(result)){
						return;
					}
					
					//控制走
					if(IflyUtils.isCustormAction(result)){
						return;
					}
					
					//命令功能
					if(IflyUtils.doCommandAction(result)){
						return;
					}

					// 问科大讯飞
					BroadcastShare.askIfly(result);

				}else {
					listenBegin();
				}
				
			}
		}

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			Log.i("voice", "当前正在说话，音量大小： volume=="+volume );
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			Log.i("voice", "会话id " );
			// 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
			// 若使用本地能力，会话id为null
			// if (SpeechEvent.EVENT_SESSION_ID == eventType) {
			// String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
			// Log.d(TAG, "session id =" + sid);
			// }
		}
	};

	//初始化监听
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
        		//初始化失败,错误码
				Log.i("voiceresult", "初始化失败" );
        	} else {
				// 初始化成功，之后可以调用startSpeaking方法
        		// 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
        		// 正确的做法是将onCreate中的startSpeaking调用移至这里
			}		
		}
	};
	
	//上传词表监听器
	private LexiconListener mLexiconListener = new LexiconListener() {

		@Override
		public void onLexiconUpdated(String lexiconId, SpeechError error) {
			if (error != null) {
				Log.i("voiceresult", "上传联系人词表error===" + error.toString() );
				uploadUserThesaurus();
			} else {
				Log.i("voiceresult", "上传联系人词表成功" );
			}
		}
	};
	
	//上传词表
	private void uploadUserThesaurus(){
		String contents = Utilities.readFile("userwords","utf-8");
		// 指定引擎类型
		mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 置编码类型
		mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
		ret = mIat.updateLexicon("userword", contents, mLexiconListener);
		if(ret != ErrorCode.SUCCESS){
			Log.i("voiceresult", "上传热词失败,错误码==" + ret );
			uploadUserThesaurus();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mIat.isListening()){
			mIat.cancel();
		}
		mIat.destroy();
		stopService(new Intent(this, MusicPlayerService.class));
		unregisterReceiver(receiver);
	}

}

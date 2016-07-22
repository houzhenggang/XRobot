package com.robot.et.core.software.iflytek;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.robot.et.common.BroadcastAction;
import com.robot.et.common.BroadcastShare;
import com.robot.et.common.DataConfig;
import com.robot.et.debug.Logger;
import com.robot.et.util.DialogueManager;

//科大讯飞文本理解
public class IflyTextUnderstanderService extends Service {

	private TextUnderstander mTextUnderstander;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("IflyTextUnderstanderService onCreate()");
		
		mTextUnderstander = TextUnderstander.createTextUnderstander(this,textUnderstanderListener);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_IFLY_TEXT_UNDERSTANDER);
		registerReceiver(receiver, filter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BroadcastAction.ACTION_IFLY_TEXT_UNDERSTANDER)) {// 文本理解
				String result = intent.getStringExtra("result");
				Log.i("voiceresult", "文本理解result===" + result);
				if(!TextUtils.isEmpty(result)){
					textUnderstander(result);
				}else{
					BroadcastShare.resumeChat();
				}
			}
		}
	};

	//文本理解
	private void textUnderstander(String content) {
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
			Log.i("voiceresult", "文本理取消");
		}
		// 函数调用返回值
		int ret = mTextUnderstander.understandText(content, textListener);
		if (ret != 0) {
			Log.i("voiceresult", "文本理解错误码ret==" + ret);
			BroadcastShare.resumeChat();
		}
	}

	private InitListener textUnderstanderListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Log.i("voiceresult", "文本理解初始化失败,错误码code==" + code);
			}
		}
	};

	private TextUnderstanderListener textListener = new TextUnderstanderListener() {

		@Override
		public void onResult(UnderstanderResult result) {
			Log.i("voiceresult", "文本理解onResult");
			Message message = handler.obtainMessage();
			message.obj = result;
			if(DataConfig.isPlayScript){//在表演剧本
				return;
			}
			handler.sendMessage(message);
		}

		@Override
		public void onError(SpeechError error) {
			// 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
			Log.i("voiceresult", "文本理解onError Code==" + error.getErrorCode());
			BroadcastShare.resumeChat();
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			UnderstanderResult result = (UnderstanderResult) msg.obj;
			Log.i("voiceresult", "文本理解onResult  result===" + result);
			if (null != result) {
				String text = result.getResultString();
				Log.i("voiceresult", "文本理解text===" + text);
				if (!TextUtils.isEmpty(text)) {
					DialogueManager.speakUnderstanderContent(text);
				} else {
					BroadcastShare.resumeChat();
				}
			} else {
				Log.i("voiceresult", "文本理解不正确");
				BroadcastShare.resumeChat();
			}
		};
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
		}
		mTextUnderstander.destroy();
	}

}

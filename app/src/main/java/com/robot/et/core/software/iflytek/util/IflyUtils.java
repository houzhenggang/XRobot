package com.robot.et.core.software.iflytek.util;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.robot.et.R;
import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.core.software.system.MediaManager;
import com.robot.et.enums.MatchSceneEnum;
import com.robot.et.enums.SceneServiceEnum;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.BroadcastShare;
import com.robot.et.util.CallPhoneControl;
import com.robot.et.util.CharactorTool;
import com.robot.et.util.DataManager;
import com.robot.et.util.DateTools;
import com.robot.et.util.EnumManager;
import com.robot.et.util.GsonParse;
import com.robot.et.util.MatchStringUtil;
import com.robot.et.util.PlayerControl;
import com.robot.et.util.RobotLearnManager;

import java.util.Random;

public class IflyUtils {

	/*科大讯飞语音合成参数设置
	 * isTypeCloud 本地还是云端
	 * speakMen 发音人
	 * speed 语速
	 * pitch 语调
	 * volume 音量
	 */
	public static void setTextToVoiceParam(SpeechSynthesizer mTts,boolean isTypeCloud,String speakMen,
			String speed,String pitch,String volume) {
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 根据合成引擎设置相应参数
		if(isTypeCloud){//设置使用云端引擎
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		}else{//设置使用本地引擎
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			//设置发音人资源路径
			mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath(speakMen));
		}
		// 设置合成发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME,speakMen);
		// 设置合成语速
		mTts.setParameter(SpeechConstant.SPEED, speed);
		// 设置合成音调
		mTts.setParameter(SpeechConstant.PITCH, pitch);
		// 设置合成音量
		mTts.setParameter(SpeechConstant.VOLUME, volume);
		// 设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
		// 设置播放合成音频打断音乐播放，默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		String currentTime = DateTools.getCurrenTimeDetail(System.currentTimeMillis());
//		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,	Environment.getExternalStorageDirectory() + "/msc/tts.wav");
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,	Environment.getExternalStorageDirectory() + "/msc/"+ currentTime + ".wav");
	}
	
	/*
	 * 获取发音人资源路径
	 * speakMen 说话的人
	 */
	private static String getResourcePath(String speakMen) {
		Context context = CustomApplication.getInstance().getApplicationContext();
		StringBuffer tempBuffer = new StringBuffer();
		// 合成通用资源
		tempBuffer.append(ResourceUtil.generateResourcePath(context,RESOURCE_TYPE.assets, "tts/common.jet"));
		tempBuffer.append(";");
		// 发音人资源
		tempBuffer.append(ResourceUtil.generateResourcePath(context,RESOURCE_TYPE.assets, "tts/" + speakMen + ".jet"));
		return tempBuffer.toString();
	}
	
	/*科大讯飞语音听写
	 * isTypeCloud  本地还是云端
	 * language 听的语言
	 * thresholdValue  门限值
	 */
	public static void setVoiceToTextParam(SpeechRecognizer mIat,boolean isTypeCloud,String language) {
		// 清空参数
		mIat.setParameter(SpeechConstant.PARAMS, null);
		// 设置听写引擎
		if(isTypeCloud){
			mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		}else{
			mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			//设置本地识别使用语法id(此id在语法文件中定义)、
			mIat.setParameter(SpeechConstant.LOCAL_GRAMMAR, "call");  
		}
		// 设置返回结果格式
		mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
		if (language.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, language);
		}

		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

		// 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
		mIat.setParameter(SpeechConstant.ASR_PTT, "0");
		
		// 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		// 注：AUDIO_FORMAT参数语记需要更新版本才能生效
		 mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
		 String currentTime = DateTools.getCurrenTimeDetail(System.currentTimeMillis());
//		 mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() +"/msc/iat.wav");
		 mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() +"/msc/" + currentTime + ".wav");
	}
	
	// 处理自定义的一些话
	public static boolean doCustomAction(String result){
		MatchSceneEnum sceneEnum = EnumManager.getSceneEnum(result);
		Log.i("voiceresult", "sceneEnum=====" + sceneEnum );
		if(sceneEnum == null){
			return RobotLearnManager.doRobotLearnContent(result);
		}
		
		boolean flag = false;
		switch (sceneEnum) {
		case VOICE_BIGGEST_SCENE:// 声音最大
			flag = true;
			MediaManager.getInstance().setMaxVolume();
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "音量已经最大");
			break;
		case VOICE_LITTEST_SCENE:// 声音最小
			flag = true;
			MediaManager.getInstance().setCurrentVolume(6);
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "音量已经最小");
			break;
		case VOICE_BIGGER_INDIRECT_SCENE:// 间接增加声音
			flag = true;
			MediaManager.getInstance().increaseVolume();
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "音量增加");
			break;
		case VOICE_LITTER_INDIRECT_SCENE://间接降低声音
			flag = true;
			MediaManager.getInstance().reduceVolume();
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "音量减小");
			break;
		case VOICE_BIGGER_SCENE:// 直接增加声音
			flag = true;
			MediaManager.getInstance().increaseVolume();
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "音量增加");
			break;
		case VOICE_LITTER_SCENE://直接降低声音
			flag = true;
			MediaManager.getInstance().reduceVolume();
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "音量减小");
			break;
		case QUESTION_ANSWER_SCENE:// 智能学习回答话语
			flag = true;
			RobotLearnManager.learnChatBySpeak(result);
			break;
		case DISTURB_OPEN_SCENE:// 免打扰开
			flag = true;
			String content = "好的，进入免打扰模式";
			CallPhoneControl.changeRobotCallStatus(DataConfig.ROBOT_STATUS_DISYURB_NOT, content);
			break;
		case DISTURB_CLOSE_SCENE:// 免打扰关
			flag = true;
			String content2 = "好的，免打扰模式已关闭";
			CallPhoneControl.changeRobotCallStatus(DataConfig.ROBOT_STATUS_NORMAL, content2);
			break;
		case SHUT_UP_SCENE:// 闭嘴
			flag = true;
			BroadcastShare.textToSpeak(DataConfig.TYPE_DO_NOTHING, "好的,我去玩去了");
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_WAKE_UP_RESET);
			CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
			break;
		case DO_ACTION_SCENE:// 智能学习做动作
			flag = true;
			RobotLearnManager.learnActionBySpeak(result);
			break;
		case CONTROL_TOYCAR_SCENE:// 控制玩具车
			flag = true;
			DataConfig.isControlToyCar = true;
			int toyCarNum = MatchStringUtil.getToyCarNum(result);
			DataManager.setToyCarNum(toyCarNum);
			Log.i("voiceresult", "toyCarNum=====" + toyCarNum );
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "好的");

			break;
		case LANGUAGE_SWITCH_SCENE:// 语言切换
			flag = true;
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "好的");
			if(!DataConfig.isLanguageSwitch){
				DataConfig.isLanguageSwitch = true;
			}else{
				DataConfig.isLanguageSwitch = false;
			}
			break;

		default:
			break;
		}
		return flag;
	}
	
	//判断是不是自定义的动作
	public static boolean isCustormAction(String result){
		if(!TextUtils.isEmpty(result)){
			Context context = CustomApplication.getInstance().getApplicationContext();
			String[] actionNames = context.getResources().getStringArray(R.array.action_name);
			for(int i = 0;i < actionNames.length;i++){
				if(CharactorTool.getFullSpell(result).contains(CharactorTool.getFullSpell(actionNames[i]))){
					StringBuffer buffer = new StringBuffer();
					for(int j=0;j<result.length();j++){
						char tempChar = result.charAt(j);
						if(Character.isDigit(tempChar)){
							buffer.append(tempChar);
						}
					}
					String tempDistance = buffer.toString();
					String digit = "";
					if(!TextUtils.isEmpty(tempDistance)){
						digit = tempDistance;
					}else{
						if(result.contains("一圈")){//转一圈的时候传360
							digit = "360";
						}else{//默认30厘米
							digit = "90";
						}
					}
					String[] actionValue = context.getResources().getStringArray(R.array.action_value);
					Log.i("move","actionValue[i]===" + actionValue[i]);
					Log.i("move","digit===" + digit);
					
					//随机回答
					String[] answers = context.getResources().getStringArray(R.array.action_custorm_answer);
					int size = answers.length;
					if(answers != null && size > 0){
						Random random = new Random();
						int randNum = random.nextInt(size);
						String answer = answers[randNum];
						BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, answer);
					}

					if(DataConfig.isControlToyCar){
						//控制玩具车走的广播
						BroadcastShare.controlToyCarMove(actionValue[i],DataManager.getToyCarNum());
					}else{
						//控制机器人走的广播
						BroadcastShare.controlMove(actionValue[i],digit);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	//做制定命令的动作与回答
	public static boolean doCommandAction(String result){
		if(!TextUtils.isEmpty(result)){
			Context context = CustomApplication.getInstance().getApplicationContext();
			String[] questions = context.getResources().getStringArray(R.array.command_question);
			if(questions != null && questions.length > 0){
				for(int i=0;i<questions.length;i++){
					if(result.contains(questions[i])){
						String[] answers = context.getResources().getStringArray(R.array.command_answer);
						String[] actions = context.getResources().getStringArray(R.array.command_action);
						String answer = answers[i];
						String action = actions[i];
						//说命令的回答
						if(!TextUtils.isEmpty(answer)){
							BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, answer);
						}
						//执行命令的动作
						if(!TextUtils.isEmpty(action)){
							Log.i("voiceresult", "执行萌的动作action====" + action);
							Intent intent = new Intent();
							intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_EMOTION);
							intent.putExtra("emotion", Integer.parseInt(action));
							context.sendBroadcast(intent);
							
							BroadcastShare.resumeChat();
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//说什么话的处理
	private static void speakContent(String question,String answer){
		if (!TextUtils.isEmpty(answer)) {
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, answer);
		} else {
			//科大讯飞不能理解的话继续监听
			BroadcastShare.resumeChat();
		}
	}
	
	/*
	 * 科大讯飞文本理解返回的结果
	 *  service + question + answer
	 */
	public static void speakUnderstanderContent(String result) {
		if (!TextUtils.isEmpty(result)) {
			String[] results = GsonParse.parseAnswerResult(result);
			if (results != null && results.length > 0) {
				String question = results[1];
				String service = results[0];
				String answer = results[2];
				Log.i("voiceresult","语义理解识别结果answer===" + answer);
				Log.i("voiceresult","语义理解识别结果question===" + question);
				
				SceneServiceEnum serviceEnum = EnumManager.getIflyService(service);
				Log.i("voiceresult", "Iflyutils  serviceEnum===" + serviceEnum);
				
				if(serviceEnum == null){
					speakContent(question, answer);
					return;
				}
				
				switch (serviceEnum) {
				case BAIKE://百科
					speakContent(question, answer);
					break;
				case CALC://计算器
					speakContent(question, answer);
					break;
				case COOKBOOK://菜谱
					speakContent(question, answer);
					break;
				case DATETIME://日期
					speakContent(question, answer);
					break;
				case FAQ://社区问答
					speakContent(question, answer);
					break;
				case FLIGHT://航班查询
					// do nothing
					speakContent(question, answer);
					break;
				case HOTEL://酒店查询
					// do nothing
					speakContent(question, answer);
					break;
				case MAP://地图查询
					// do nothing
					speakContent(question, answer);
					break;
				case MUSIC://音乐
					if (!TextUtils.isEmpty(answer)) {
						PlayerControl.playMusic(DataConfig.MUSIC_SRC_FROM_OTHER,0,answer);
					} else {
						speakContent(question, answer);
					}
					break;
				case RESTAURANT://餐馆
					// do nothing
					speakContent(question, answer);
					break;
				case SCHEDULE://提醒
					// 日期 + 时间 + 做什么事
					if (!TextUtils.isEmpty(answer)) {
						AlarmRemindManager.setIflyRemind(answer);
					} else {
						speakContent(question, answer);
					}
					break;
				case STOCK://股票查询
					// do nothing
					speakContent(question, answer);
					break;
				case TRAIN://火车查询
					// do nothing
					speakContent(question, answer);
					break;
				case TRANSLATION://翻译
					// do nothing
					speakContent(question, answer);
					break;
				case WEATHER://天气查询
					if(DataConfig.isGetCity){
						DataConfig.isGetCity = false;
					}else{
						speakContent(question, answer);
					}
					break;
				case OPENQA://褒贬&问候&情绪
					speakContent(question, answer);
					break;
				case TELEPHONE://打电话
					if (!TextUtils.isEmpty(answer)) {// 有结果
						CallPhoneControl.getRoomNum(answer);
					} else {// 返回结果为空
						speakContent(question, answer);
					}
					break;
				case MESSAGE://发短信
					// do nothing
					speakContent(question, answer);
					break;
				case CHAT://闲聊
					speakContent(question, answer);
					break;
				case PM25://空气质量
					speakContent(question, answer);
					break;

				default:
					break;
				}
				
			} else {
				if(DataConfig.isParseWeatherError){//解析天气异常
					DataConfig.isParseWeatherError = false;
					BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "主人，没有查到呢，换个试试吧。");
				}else{
					BroadcastShare.resumeChat();
				}
			}

		} else {
			// 结果为空继续监听
			BroadcastShare.resumeChat();
		}
	}
	
	//

}

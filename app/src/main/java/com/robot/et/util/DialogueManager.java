package com.robot.et.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.R;
import com.robot.et.app.CustomApplication;
import com.robot.et.config.DataConfig;
import com.robot.et.enums.SceneServiceEnum;

public class DialogueManager {

	//说什么话的处理
	private static void speakContent(String question,String answer){
		if (!TextUtils.isEmpty(answer)) {
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, answer);
		} else {
			//科大讯飞不能理解的话继续监听
			BroadcastShare.resumeChat();
		}
	}

	//自定义回答
	public static boolean isCustormQA(String result){
		if(!TextUtils.isEmpty(result)){
			Context context = CustomApplication.getInstance().getApplicationContext();
			String[] questions = context.getResources().getStringArray(R.array.custorm_question);
			if(questions != null && questions.length > 0){
				for(int i = 0; i < questions.length; i ++){
					String question = questions[i];
					if(result.contains(question) || question.contains(result)){
						String[] answers = context.getResources().getStringArray(R.array.custorm_answer);
						if(answers != null && answers.length > 0){
							BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, answers[i]);
							return true;
						}
					}
				}
			}

		}
		return false;
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
	
}

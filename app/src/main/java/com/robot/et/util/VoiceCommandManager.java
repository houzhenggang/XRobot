package com.robot.et.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.R;
import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.core.software.system.MediaManager;
import com.robot.et.enums.MatchSceneEnum;

import java.util.Random;

public class VoiceCommandManager {

	// 处理自定义的一些话
	public static boolean doCustomAction(String result) {
		MatchSceneEnum sceneEnum = EnumManager.getSceneEnum(result);
		Log.i("voiceresult", "sceneEnum=====" + sceneEnum);
		if (sceneEnum == null) {
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
				Log.i("voiceresult", "toyCarNum=====" + toyCarNum);
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "好的");

				break;
			case LANGUAGE_SWITCH_SCENE:// 语言切换
				flag = true;
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "好的");
				if (!DataConfig.isLanguageSwitch) {
					DataConfig.isLanguageSwitch = true;
				} else {
					DataConfig.isLanguageSwitch = false;
				}
				break;
			case RAISE_HAND_SCENE:// 抬手
				flag = true;
				BroadcastShare.controlWaving(ScriptConfig.HAND_UP, ScriptConfig.HAND_RIGHT, "0");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						BroadcastShare.controlWaving(ScriptConfig.HAND_DOWN, ScriptConfig.HAND_RIGHT, "0");
						BroadcastShare.resumeChat();
					}
				},1500);

				break;
			case WAVING_SCENE:// 摆手
				flag = true;
				BroadcastShare.controlWaving(ScriptConfig.HAND_UP, ScriptConfig.HAND_TWO, "0");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						BroadcastShare.controlWaving(ScriptConfig.HAND_DOWN, ScriptConfig.HAND_TWO, "0");
						BroadcastShare.resumeChat();
					}
				},1500);

				break;
			case OPEN_HOUSEHOLD_SCENE:// 打开家电
				flag = true;
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "好的");
				AlarmRemindManager.pushMsgToApp("开", DataConfig.TO_APP_BLUETOOTH_CONTROLLER);

				break;
			case CLOSE_HOUSEHOLD_SCENE:// 关闭家电
				flag = true;
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "好的");
				AlarmRemindManager.pushMsgToApp("关", DataConfig.TO_APP_BLUETOOTH_CONTROLLER);

				break;

			default:
				break;
		}
		return flag;
	}
	
	//判断是不是自定义的动作
	public static boolean isCustormAction(String result){
		if(!TextUtils.isEmpty(result)){
			String moveKey = EnumManager.getControlMove(result);
			Log.i("voiceresult","moveKey===" + moveKey);
			if(!TextUtils.isEmpty(moveKey)){
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
						digit = "1";
					}
				}
				Log.i("voiceresult","digit===" + digit);
				if(DataConfig.isControlToyCar){//控制玩具车走
					DataConfig.controlNum = 0;
					BroadcastShare.controlToyCarMove(moveKey,DataManager.getToyCarNum());
					BroadcastShare.resumeChat();
				}else{
					//随机回答
					String[] answers = CustomApplication.getInstance().getApplicationContext().
							getResources().getStringArray(R.array.action_custorm_answer);
					int size = answers.length;
					if(answers != null && size > 0){
						Random random = new Random();
						int randNum = random.nextInt(size);
						String answer = answers[randNum];
						BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, answer);
					}
					//控制机器人走的广播
					BroadcastShare.controlMove(moveKey,digit);
				}
				return true;
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
							BroadcastShare.controlRobotEmotion(Integer.parseInt(action));
							BroadcastShare.resumeChat();
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
}

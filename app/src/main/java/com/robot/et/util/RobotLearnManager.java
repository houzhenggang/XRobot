package com.robot.et.util;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.R;
import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.EmotionConfig;
import com.robot.et.entity.JpushInfo;
import com.robot.et.entity.LearnAnswerInfo;

public class RobotLearnManager {
	
	// 增加智能学习的问题与答案
	public static void insertLeanInfo(String question,String answer, String action, int learnType) {
		String robotNum = SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "");
		DBUtils.insertLeanInfo(robotNum, question, answer, action,learnType);
	}
	
	//获取智能学习的答案
	public static List<LearnAnswerInfo> getLearnAnswerInfos(int questionId){
		return DBUtils.getLearnAnswerInfos(questionId);
	}
	
	//机器人通过人说固定的话来学习要执行的动作
	public static void learnActionBySpeak(String result){
		String question = MatchStringUtil.getQuestion(result);
		Log.i("voiceresult", "quesstion====" + question);
		String content = "";
		if(!TextUtils.isEmpty(question)){
			boolean isExistence = false;
			Context context = CustomApplication.getInstance().getApplicationContext();
			String[] actionNames = context.getResources().getStringArray(R.array.command_question);
			for(int i = 0;i < actionNames.length;i++){
				if(CharactorTool.getFullSpell(question).contains(CharactorTool.getFullSpell(actionNames[i]))){
					isExistence = true;
					String[] answers = context.getResources().getStringArray(R.array.command_answer);
					String[] actions = context.getResources().getStringArray(R.array.command_action);
					String answer = answers[i];
					String action = actions[i];
					Log.i("voiceresult", "action====" + action);
					insertLeanInfo(question, answer, action,DataConfig.LEARN_BY_ROBOT);
				}
			}
			
			if(!isExistence){
				Log.i("voiceresult", "action====" + EmotionConfig.ROBOT_EMOTION_NORMAL);
				insertLeanInfo(question, "", String.valueOf(EmotionConfig.ROBOT_EMOTION_NORMAL),DataConfig.LEARN_BY_ROBOT);
			}
			
			content = "记住了，如果你说 " + question + "，我应该卖个萌" ;
			
		}else{
			content = "我好像没学会，再教我一次吧 ";
		}
		BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);
	}
	
	// 机器人通过人说固定的话来学习要回答的话语
	public static void learnChatBySpeak(String result) {
		String question = MatchStringUtil.getQuestion(result);
		String answer = MatchStringUtil.getAnswer(result);
		Log.i("voiceresult", "quesstion====" + question);
		Log.i("voiceresult", "answer====" + answer);
		
		String content = "";
		if(!TextUtils.isEmpty(question) && !TextUtils.isEmpty(answer)){
			RobotLearnManager.insertLeanInfo(question, answer,"",DataConfig.LEARN_BY_ROBOT);
			content = "记住了，如果你说 " + question + "，我应该回答" + answer;
		}else{
			content = "我好像没学会，再教我一次吧 ";
		}
		BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);
	}
	
	//机器人做自己学习的内容
	public static boolean doRobotLearnContent(String result){
		int quetionId = DBUtils.getQuesstionId(result);
		Log.i("voiceresult", "学习库里面问题quetionId====" + quetionId);
		if(quetionId != -1){
			List<LearnAnswerInfo> mInfos = getLearnAnswerInfos(quetionId);
			Log.i("voiceresult", "学习库里面答案mInfos.size()====" + mInfos.size());
			int size = mInfos.size();
			if(mInfos != null && size > 0){
				Random random = new Random();
				int randNum = random.nextInt(size);
				LearnAnswerInfo mInfo = mInfos.get(randNum);
				String content = mInfo.getAnswer();
				String action = mInfo.getAction();
				//说学习的内容
				if(!TextUtils.isEmpty(content)){
					BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);
				}
				//执行萌的动作
				if(!TextUtils.isEmpty(action)){
					Log.i("voiceresult", "执行萌的动作action====" + action);
					Intent intent = new Intent();
					intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_EMOTION);
					intent.putExtra("emotion", Integer.parseInt(action));
					CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
					
					BroadcastShare.resumeChat();
				}
				return true;
			}
			return false;
		}
		return false;
	}
	
	//机器人通过APP学习，（true:机器人问答库    false:个人问答库）
	public static void learnChatByApp(boolean isRobotLearn,JpushInfo info){
		String quesstion = info.getQuestion();
		String answer = info.getAnswer();
		Log.i("jpush", "quesstion===" + quesstion);
		Log.i("jpush", "answer===" + answer);
		int learnType = 0;
		if(isRobotLearn){//机器人问答库
			learnType = DataConfig.LEARN_BY_ROBOT;
		}else{//个人问答库
			learnType = DataConfig.LEARN_BY_PERSON;
		}
		insertLeanInfo(quesstion, answer, "",learnType);
	}
	
	//机器人通过APP学习，（机器人学习库，通过说话学习）
	public static void learnChatByApp(String result){
		if(!TextUtils.isEmpty(result)){
			String question = MatchStringUtil.getQuestion(result);
			String answer = MatchStringUtil.getAnswer(result);
			Log.i("jpush", "quesstion===" + question);
			Log.i("jpush", "answer===" + answer);
			if(!TextUtils.isEmpty(question) && !TextUtils.isEmpty(answer)){
				insertLeanInfo(question, answer, "",DataConfig.LEARN_BY_ROBOT);
			}
		}
	}
	
}

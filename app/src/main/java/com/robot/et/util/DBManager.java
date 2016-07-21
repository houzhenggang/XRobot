package com.robot.et.util;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.DataConfig;
import com.robot.et.db.RobotDB;
import com.robot.et.entity.LearnAnswerInfo;
import com.robot.et.entity.LearnQuestionInfo;
import com.robot.et.entity.RemindInfo;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;
import com.robot.et.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

	//删除所有用户的信息
	public static void deleteAllUserInfos(){
		RobotDB mDb = RobotDB.getInstance();
		List<UserInfo> infos = mDb.getAllUserPhoneInfos();
		if (infos != null && infos.size() > 0) {
			mDb.deleteAllUserPhoneInfos();
		}
	}
	
	//新增用户信息
	public static void addUserInfo(List<UserInfo> infos) {
		if(infos != null && infos.size() > 0){
			RobotDB mDb = RobotDB.getInstance();
			for(UserInfo info : infos){
				mDb.addUserPhoneInfo(info);
			}
		}
	}
	
	//获取用户信息
	public static List<UserInfo> getAllUserInfos() {
		RobotDB mDb = RobotDB.getInstance();
		return mDb.getAllUserPhoneInfos();
	}
	
	//增加闹铃
	public static boolean addAlarm(RemindInfo info){
		RobotDB mDb = RobotDB.getInstance();
		RemindInfo mInfo = mDb.getRemindInfo(info);
		if(mInfo != null){
			Log.i("alarm", "闹铃已存在");
			return false;
		}
		mDb.addRemindInfo(info);
		Log.i("alarm", "增加闹铃成功");
		return true;
	}

	// 增加智能学习的问题与答案
	public static void insertLeanInfo(String robotNum, String question,
			String answer, String action, int learnType) {
		RobotDB mDb = RobotDB.getInstance();
		LearnQuestionInfo info = new LearnQuestionInfo();
		info.setRobotNum(robotNum);
		info.setQuestion(question);
		info.setLearnType(learnType);
		LearnAnswerInfo aInfo = new LearnAnswerInfo();
		aInfo.setRobotNum(robotNum);
		aInfo.setLearnType(learnType);
		if (!TextUtils.isEmpty(answer)) {
			aInfo.setAnswer(answer);
		}
		if (!TextUtils.isEmpty(action)) {
			aInfo.setAction(action);
		}
		int questionId = mDb.getQuesstionId(question);
		if(questionId != -1){//已存在
			info.setId(questionId);
			mDb.updateLearnQuestion(info);
			aInfo.setQuestionId(questionId);
			mDb.updateLearnAnswer(aInfo);
		}else{//不存在
			mDb.addLearnQuestion(info);
			aInfo.setQuestionId(mDb.getQuesstionId(question));
			
			mDb.addLearnAnswer(aInfo);
		}
	}
	
	//获取智能学习的所有答案
	public static List<LearnAnswerInfo> getLearnAnswerInfos(int questionId) {
		RobotDB mDb = RobotDB.getInstance();
		List<LearnAnswerInfo> infos = mDb.getLearnAnswers(questionId);
		return infos;
	}
	
	// 获取问题ID
	public static int getQuesstionId(String quesstion) {
		return RobotDB.getInstance().getQuesstionId(quesstion);
	}
	
	// 获取提醒
	public static List<RemindInfo> getRemindTips(long minute) {
		String date = DateTools.getCurrentDate(minute);
		int currentHour = DateTools.getCurrentHour(minute);
		String minuteTwo = DateTools.get2DigitMinute(minute);
		String time = currentHour + ":" + minuteTwo + ":" + "00";
		RobotDB mDao = RobotDB.getInstance();
		List<RemindInfo> infos = null;
		try{
			infos = mDao.getRemindInfos(date,time,DataConfig.REMIND_NO_ID);
		}catch (Exception e){
			Log.i("netty", "dbutils  getRemindTips() Exception===" + e.getMessage());
			infos = new ArrayList<RemindInfo>();
		}
		return infos;
	}

	// 更新已经提醒的条目
	public static void updateRemindInfo(RemindInfo info,long minute,int frequency) {
		String date = DateTools.getCurrentDate(minute);
		RobotDB mDao = RobotDB.getInstance();
		mDao.updateRemindInfo(info,date, frequency);
	}

	// 删除已经提醒的条目
	public static void deleteCurrentRemindTips(long minute) {
		String date = DateTools.getCurrentDate(minute);
		int currentHour = DateTools.getCurrentHour(minute);
		String currentMinute = DateTools.get2DigitMinute(minute);
		String time = currentHour + ":" + currentMinute + ":" + "00";
		RobotDB mDao = RobotDB.getInstance();
		mDao.deleteRemindInfo(date, time, DataConfig.REMIND_NO_ID);
	}

	//根据时间内容删除APP发来的闹铃
	public static void deleteAppAlarmRemind(String originalTime){
		if(!TextUtils.isEmpty(originalTime)){
			RobotDB mDao = RobotDB.getInstance();
			mDao.deleteAppRemindInfo(originalTime);
		}
	}

	//增加剧本
	public static void addScript(ScriptInfo info, List<ScriptActionInfo> infos){
		RobotDB mDao = RobotDB.getInstance();
		String scriptName = info.getScriptContent();
		int scriptId = mDao.getScriptId(scriptName);
		Log.i("netty", "addScript temId===" + scriptId);
		if(scriptId != -1){//已经存在
			Log.i("netty", "addScript 数据内容已存在");
			mDao.deleteScriptAction(scriptId);
		}else{//没有存在
			Log.i("netty", "addScript 无数据内容");
			mDao.addScript(info);
			scriptId = mDao.getScriptId(scriptName);
			Log.i("netty", "addScript scriptId===" + scriptId);
		}

		if(infos != null && infos.size() > 0){
			for(ScriptActionInfo actionInfo : infos){
				actionInfo.setScriptId(scriptId);
				mDao.addScriptAction(actionInfo);
			}
			Log.i("netty", "addScript 加入数据库成功");
		}
	}

	//获取剧本执行的动作
	public static List<ScriptActionInfo> getScriptActions(String scriptContent){
		List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
		if(!TextUtils.isEmpty(scriptContent)){
			RobotDB mDao = RobotDB.getInstance();
			int scriptId = mDao.getScriptId(scriptContent);
			Log.i("netty", "getScriptActions()  scriptId====" + scriptId);
			if(scriptId != -1){
				infos = mDao.getScriptActionInfos(scriptId);
			}
		}
		return infos;
	}

}

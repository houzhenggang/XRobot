package com.robot.et.util;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.DataConfig;
import com.robot.et.config.UrlConfig;
import com.robot.et.core.software.okhttp.HttpEngine;
import com.robot.et.core.software.system.AlarmClockManager;
import com.robot.et.debug.Logger;
import com.robot.et.entity.JpushInfo;
import com.robot.et.entity.RemindInfo;
import com.robot.et.entity.ScriptActionInfo;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmRemindManager {

	// 得到提醒的广播时间 data:2016-05-08 time:09:05:00
	public static Calendar getCalendar(String data, String time) {
		Calendar calendar = Calendar.getInstance();
		String datas[] = getDatas(data);
		String times[] = getTimes(time);

		if (datas != null && datas.length > 0) {
			// 当前年
			calendar.set(Calendar.YEAR, Integer.parseInt(datas[0]));
			// 当前月，从0开始 【0-11】
			calendar.set(Calendar.MONTH, Integer.parseInt(datas[1]) - 1);
			// 当前日
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datas[2]));
		}

		if (times != null && times.length > 0) {
			// 当前小时
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
			// 当前分钟
			calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));
			// 当前秒
			calendar.set(Calendar.SECOND, 0);
		}
		return calendar;
	}

	// 得到提醒的广播日期 data:2016-05-08
	private static String[] getDatas(String data) {
		String[] datas = null;
		if (!TextUtils.isEmpty(data)) {
			datas = data.split("-");
			if (datas != null && datas.length > 0) {
				if (datas[1].startsWith("0")) {// 月
					datas[1] = datas[1].substring(1, datas[1].length());
				}

				if (datas[2].startsWith("0")) {// 日
					datas[2] = datas[2].substring(1, datas[2].length());
				}

			}
		}
		return datas;
	}

	// 得到提醒的广播时间 time:09:05:00
	private static String[] getTimes(String time) {
		String[] times = null;
		if (!TextUtils.isEmpty(time)) {
			times = time.split("\\:");
			if (time != null && times.length > 0) {
				if (times[0].startsWith("0")) {// 时
					times[0] = times[0].substring(1, times[0].length());
				}

				if (times[1].startsWith("0")) {// 分
					times[1] = times[1].substring(1, times[1].length());
				}

			}
		}
		return times;
	}

	// 得到提醒的分钟
	public static String getRemindMinute(long minute) {
		String minuteTwo = "";
		// currentMinute====6 当10分钟之内时，系统默认读的是一位数字
		int currentMinute = DateTools.getCurrentMinute(minute);
		String tempMinute = String.valueOf(currentMinute);
		if (tempMinute.length() == 1) {
			minuteTwo = "0" + tempMinute;
		} else {
			minuteTwo = tempMinute;
		}
		return minuteTwo;
	}
	
	//设置闹铃
	public static void setAlarmClock(String date,String time){
		Calendar calendar = getCalendar(date,time);
		long currentMinute = System.currentTimeMillis();
		String action = DateTools.getCurrentDate(currentMinute)+ DataConfig.ACTION_REMIND_SIGN+DateTools.getCurrentTime(currentMinute);
		AlarmClockManager.getInstance().setOneAlarm(action, calendar);
	}

	//设置闹铃时间的格式   yyyy-MM-dd HH:mm:ss--->>转 yyyy-MM-dd HH:mm:00
	public static String setAlarmTimeFormat(String time){
		String result = "";
		if(!TextUtils.isEmpty(time)){
			result = time.substring(0,time.length() - 2);
			result = result + "00";
			return result;
		}
		return result;
	}
	
	// 设置极光推送发来的闹铃
	public static void setAlarm(JpushInfo info) {
		// yyyy-MM-dd HH:mm:ss
		String alarmTime = info.getAlarmTime();
		String originalTime = alarmTime;
		alarmTime = setAlarmTimeFormat(alarmTime);
		String alarmContent = info.getAlarmContent();
		int remindNum = info.getRemindNum();
		int remindInteval = info.getRemindInteval();
		int frequency = info.getFrequency();
		
		Log.i("alarm", "alarmTime===" + alarmTime);
		Log.i("alarm", "alarmContent===" + alarmContent);
		Log.i("alarm", "remindNum===" + remindNum);
		Log.i("alarm", "remindInteval===" + remindInteval);
		Log.i("alarm", "frequency===" + frequency);
		if (!TextUtils.isEmpty(alarmTime)) {
			if (remindNum > 0) {
				for (int i = 0; i < remindNum; i++) {
					long minute = DateTools.getlongTime(alarmTime);
					minute = minute + i * remindInteval * 60 * 1000;
					String tepmTime = DateTools.getCurrenTimeDetail(minute);
					String[] times = tepmTime.split(" ");
					String date = times[0];
					String time = times[1];
					Log.i("alarm", "date===" + date);
					Log.i("alarm", "time===" + time);
					setAlarmClock(date, time);
					RemindInfo mInfo = new RemindInfo();
					mInfo.setRobotNum(SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, ""));
					mInfo.setDate(date);
					mInfo.setTime(time);
					mInfo.setContent(alarmContent);
					mInfo.setRemindInt(DataConfig.REMIND_NO_ID);
					mInfo.setFrequency(frequency);
					mInfo.setOriginalAlarmTime(originalTime);
					DBUtils.addAlarm(mInfo);
				}
			}
		}
	}

	private void setAlarmDateAndTime(String alarmTime){
		if(!TextUtils.isEmpty(alarmTime)){
		}
	}

	//App推送来的提醒信息
	public static void setAppAlarmRemind(String alarmContent){
		if(!TextUtils.isEmpty(alarmContent)){
			RemindInfo info = GsonParse.parseAppRemind(alarmContent);
			if(info != null){
				String alarmTime = info.getOriginalAlarmTime();
				alarmTime = setAlarmTimeFormat(alarmTime);
				if(TextUtils.isEmpty(alarmTime)){
					return;
				}
				String[] times = alarmTime.split(" ");
				String date = times[0];
				String time = times[1];
				Log.i("alarm", "date===" + date);
				Log.i("alarm", "time===" + time);
				setAlarmClock(date, time);
				info.setDate(date);
				info.setTime(time);
				info.setRemindInt(DataConfig.REMIND_NO_ID);
				DBUtils.addAlarm(info);
				pushMsgToApp("success");
			}
		}
	}
	
	//设置多次提醒
	public static void setMoreAlarm(long minute){
		String tepmTime = DateTools.getCurrenTimeDetail(minute);
		String[] times = tepmTime.split(" ");
		String date = times[0];
		String time = times[1];
		Log.i("alarm", "date===" + date);
		Log.i("alarm", "time===" + time);
		setAlarmClock(date, time);
	}
	
	// 获取提醒的内容
	public static List<RemindInfo> getRemindTips(long minute) {
		return DBUtils.getRemindTips(minute);
	}

	// 更新已经提醒的条目
	public static void updateRemindInfo(RemindInfo info,long minute,int frequency) {
		DBUtils.updateRemindInfo(info,minute,frequency);
	}

	// 删除已经提醒的条目
	public static void deleteCurrentRemindTips(long minute) {
		DBUtils.deleteCurrentRemindTips(minute);
	}

	// 删除app传来的提醒
	public static void deleteAppRemindTips(String originalTime) {
		if(!TextUtils.isEmpty(originalTime)){
			DBUtils.deleteAppAlarmRemind(originalTime);
		}
	}

	// 增加Ifly提醒的操作 格式：日期 + 时间 + 做什么事
	public static boolean addRemindInfo(String result) {
		if (!TextUtils.isEmpty(result)) {
			Logger.i("chat  answer===" + result);
			String dates[] = result.split(DataConfig.SCHEDULE_SPLITE);
			RemindInfo info = new RemindInfo();
			info.setRobotNum(SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, ""));
			info.setDate(dates[0]);
			info.setTime(dates[1]);
			info.setContent(dates[2]);
			info.setRemindInt(DataConfig.REMIND_NO_ID);
			info.setFrequency(1);

			return DBUtils.addAlarm(info);
		}
		return false;
	}
	
	//讯飞提醒提示
	public static void setIflyRemind(String result) {
		boolean flag = addRemindInfo(result);
		String content = "";
		if (flag) {
			content = "主人，您的提醒，我已经记下来了";
			String dates[] = result.split(DataConfig.SCHEDULE_SPLITE);
			setAlarmClock(dates[0], dates[1]);
		} else {
			content = "主人，我是一个聪明的小黄人，不用重复提醒哦";
		}

		BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);
	}

	//多个闹铃提示
	public static void doMoreAlarm(){
		List<String> datas = DataManager.getListData();
		Logger.i("datas.size====" + datas.size());
		if(datas != null && datas.size() > 0){
			int size = datas.size();
			String content = "主人您好，您设置的" + datas.get(size - 1) + "提醒时间到了，不要忘记哦。";
			datas.remove(size - 1);
			DataManager.setListData(datas);
			BroadcastShare.textToSpeak(DataConfig.TYPE_REMIND_TIPS,content);
		}
	}

	//APP发来的提醒需求处理
	public static void handleAppRemind(String result){
		pushMsgToApp(result);
		if(!TextUtils.isEmpty(result)){
			String answer = getRequireAnswer();
			if(!TextUtils.isEmpty(answer)){
				if(result.contains(answer)){//回答正确
					DataConfig.isAppPushRemind = false;
					DataConfig.isStartTime = false;
					BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, "嘿嘿，我可以去玩喽");
				}else{//回答错误
					doAppRemindNoResponse();
				}
			}
		}
	}

	//APP发来的提醒没有按照主人设置要求的话的处理
	public static void doAppRemindNoResponse(){
		int type = getSpareType();
		if(type != 0){
			List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
			ScriptActionInfo info = new ScriptActionInfo();
			info.setActionType(type);
			info.setContent(getSpareContent());
			infos.add(info);
			DataConfig.isPlayScript = false;
			ScriptManager.doScriptAction(infos);
		}else{
			BroadcastShare.resumeChat();
		}
	}

	//APP设置的提醒如果15s没有响应的话就执行要做的事情
	public static void noResponseAppRemind(){
		if(!DataConfig.isStartTime){
			DataConfig.isStartTime = true;
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					if(DataConfig.isStartTime){
						pushMsgToApp("");
						BroadcastShare.stopListenerOnly();
						BroadcastShare.stopSpeakOnly();
						doAppRemindNoResponse();
					}
				}
			}, 15 * 1000);
		}
	}

	//向APP推送消息
	public static void pushMsgToApp(String sendContent){
		final SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
		HttpEngine.Param[] params = new HttpEngine.Param[]{
				new HttpEngine.Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new HttpEngine.Param("mobile", share.getString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, "")),
				new HttpEngine.Param("msgType", "REPLY_REMIND"),
				new HttpEngine.Param("msgContent", sendContent)
		};
		HttpEngine httpEngine = HttpEngine.getInstance();
		Request request = httpEngine.createRequest(UrlConfig.PUSH_MESSAGE_TO_APP, params);
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
					Log.i("json", "向APP推送消息成功");
				}
			}

		});
	}

	//要求回答的内容
	private static String requireAnswer;
	//不回答时要求做的类型
	private static int spareType;
	//不回答时要求做的内容
	private static String spareContent;
	//提醒的人
	private static String remindMen;

	public static String getRequireAnswer() {
		return requireAnswer;
	}

	public static void setRequireAnswer(String requireAnswer) {
		AlarmRemindManager.requireAnswer = requireAnswer;
	}

	public static int getSpareType() {
		return spareType;
	}

	public static void setSpareType(int spareType) {
		AlarmRemindManager.spareType = spareType;
	}

	public static String getSpareContent() {
		return spareContent;
	}

	public static void setSpareContent(String spareContent) {
		AlarmRemindManager.spareContent = spareContent;
	}

	public static String getRemindMen() {
		return remindMen;
	}

	public static void setRemindMen(String remindMen) {
		AlarmRemindManager.remindMen = remindMen;
	}

}

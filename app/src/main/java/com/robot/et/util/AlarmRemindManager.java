package com.robot.et.util;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.DataConfig;
import com.robot.et.core.software.system.AlarmClockManager;
import com.robot.et.debug.Logger;
import com.robot.et.entity.JpushInfo;
import com.robot.et.entity.RemindInfo;

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
	
}

package com.robot.et.core.software.system;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.robot.et.app.CustomApplication;
import com.robot.et.receiver.RemindAlamrReceiver;

//聊天监听器  设置与取消的对象必须要保持一致，否则无法取消
public class AlarmClockManager {
	public static AlarmClockManager instance = null;
	private PendingIntent pendingIntent;
	private AlarmManager am;

	private AlarmClockManager() {
	}
	
	public synchronized static AlarmClockManager getInstance(){
		if(instance == null){
			instance = new AlarmClockManager();
		}
		return instance;
	}
	
	private void getPendIntent(String action){
		Intent intent = new Intent();
		intent.setAction(action);
		intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);   
		Context context = CustomApplication.getInstance().getApplicationContext();
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	//开启监听
	public void startTimer(String action){
		getPendIntent(action);
		am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 2 * 1000, pendingIntent);
	}
	
	//取消监听
	public void cancelTimer(String action) {
		getPendIntent(action);
		am.cancel(pendingIntent);
	}
	
	//设置一次性的闹钟
	public void setOneAlarm(String action,Calendar calendar){
		getOneAlarmPendIntent(action);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}
	
	//取消一次性的闹钟
	public void cancelOneAlarm(String action) {
		getOneAlarmPendIntent(action);
		am.cancel(pendingIntent);
	}
	
	private void getOneAlarmPendIntent(String action){
		Context context = CustomApplication.getInstance().getApplicationContext();
		Intent intent = new Intent(context,RemindAlamrReceiver.class);
		intent.setAction(action);
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}
	
}

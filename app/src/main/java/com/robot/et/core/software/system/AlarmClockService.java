package com.robot.et.core.software.system;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.debug.Logger;
import com.robot.et.entity.RemindInfo;
import com.robot.et.util.AlarmRemindManager;
import com.robot.et.util.DataManager;
import com.robot.et.util.BroadcastShare;

//闹铃提醒
public class AlarmClockService extends Service{
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_ALARM_ARRIVED);
		registerReceiver(receiver, filter);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	//闹铃提醒
	private void remindTips() {
		 long minute = System.currentTimeMillis();
		 List<RemindInfo> infos = AlarmRemindManager.getRemindTips(minute);
		 Logger.i("AlarmRemindService  infos.size()===" + infos.size());
		 if(infos != null && infos.size() > 0){
			 Logger.i("infos.size()==="+ infos.size());
			 List<String> datas = new ArrayList<String>();
			 for(int i=0;i<infos.size();i++){
				 //更新已经提醒过的内容
				 RemindInfo info = infos.get(i);
				 Logger.i("info.getContent()==="+ info.getContent());
				 datas.add(info.getContent());
				 
				 int frequency = info.getFrequency();
				 if(frequency == DataConfig.alarmAllDay){//每天
					 if (!TextUtils.isEmpty(info.getRemindMen())) {
						 //app提醒
						 Log.i("alarm", "app提醒");
						 AlarmRemindManager.deleteAppRemindTips(info.getOriginalAlarmTime());
					 }else{
						 //APP设置的闹铃
						 Log.i("alarm", "APP设置的闹铃");
						 minute += 24 * 60 * 60 * 1000;
						 AlarmRemindManager.updateRemindInfo(info, minute, DataConfig.alarmAllDay);
						 AlarmRemindManager.setMoreAlarm(minute);
					 }

				 }else{//不是每天
					 if(frequency == 1){
						 AlarmRemindManager.deleteCurrentRemindTips(minute);
					 }else{
						 minute += 24 * 60 * 60 * 1000;
						 AlarmRemindManager.updateRemindInfo(info, minute,frequency - 1);
						 AlarmRemindManager.setMoreAlarm(minute);
					 }
				 }
			 }
			 
			int size = datas.size();
			Logger.i("闹铃size==="+ size);
			 if(datas != null && size > 0){
				 if(size > 1){//多个闹铃
					 datas.remove(size - 1);
					 DataManager.setListData(datas);
				 }
			 }

			 RemindInfo info = infos.get(infos.size() - 1);
			 String remindMen = info.getRemindMen();
			 String remindContent = info.getContent();
			 String content = "";
			 if(!TextUtils.isEmpty(remindMen)){
				 //app提醒
				 content = remindMen + "，" + remindContent;
				 DataConfig.isAppPushRemind = true;
				 AlarmRemindManager.setRequireAnswer(info.getRequireAnswer());
				 AlarmRemindManager.setSpareType(info.getSpareType());
				 AlarmRemindManager.setSpareContent(info.getSpareContent());
			 }else{
				 //闹铃
				 content = "主人您好，您设置的" + remindContent + "提醒时间到了，不要忘记哦。";
			 }

			 BroadcastShare.textToSpeak(DataConfig.TYPE_REMIND_TIPS, content);
			 
		 }
	
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BroadcastAction.ACTION_ALARM_ARRIVED)){
				Logger.i("AlarmRemindService  接受到提醒的广播");
				remindTips();
			}
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}

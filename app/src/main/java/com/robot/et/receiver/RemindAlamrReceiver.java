package com.robot.et.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.core.software.system.AlarmClockManager;
import com.robot.et.debug.Logger;
//闹铃接受器
public class RemindAlamrReceiver  extends BroadcastReceiver {  
  
    @Override  
    public void onReceive(Context context, Intent intent) {  
    	String action = intent.getAction();
    	Logger.i("闹铃提醒时间到action====" + action);
    	if(!TextUtils.isEmpty(action)){
    		//只有是特定的广播才接受，做出相应   有提醒时，以时间格式发出的广播，这里只接受闹铃提醒的广播
    		if(action.contains(DataConfig.ACTION_REMIND_SIGN)){
    			Logger.i("闹铃提醒receive接受广播");
    			sendAlarmAction(action);
    		}
    	}
    }  
    
    //发送提醒时间到的广播
    private void sendAlarmAction(String action){
    	Logger.i("闹铃提醒receive发出广播");
		AlarmClockManager.getInstance().cancelOneAlarm(action);
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_ALARM_ARRIVED);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
    }
    
}

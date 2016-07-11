package com.robot.et.util;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;

/**
 * Created by houdeming on 2016/7/8.
 */
public class BluetoothKeyManager {

    public static boolean responseKey(String keyCode){
        Log.i("bluth", "key==" + keyCode);
        if(TextUtils.equals(keyCode,"KEYCODE_MEDIA_PREVIOUS")){//上一首
            Log.i("bluth", "上一首");
            return true;
        }else if(TextUtils.equals(keyCode,"KEYCODE_MEDIA_PLAY")){//播放
            Log.i("bluth", "播放");
//			if(DataConfig.isPlayMusic){
//				BroadcastShare.stopMusicOnly();
//			}else{
//				PlayerControl.playMp3(DataConfig.JPUSH_MUSIC,"MUSIC","爸爸妈妈听我说");
//			}

            BroadcastShare.stopListenerOnly();
            BroadcastShare.stopListenerOnly();

            Intent intent = new Intent();
            intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
            CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);

            return true;
        }else if(TextUtils.equals(keyCode,"KEYCODE_MEDIA_NEXT")){//下一首
            Log.i("bluth", "下一首");
            return true;
        }else if(TextUtils.equals(keyCode,"KEYCODE_MEDIA_PAUSE")){//暂停
            Log.i("bluth", "暂停");
            if(DataConfig.isPlayMusic){
                BroadcastShare.stopMusicOnly();
            }

            BroadcastShare.stopSpeakOnly();
            BroadcastShare.stopListenerOnly();
            Intent intent = new Intent();
            intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
            CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);

            return true;
        }
        return false;
    }

}

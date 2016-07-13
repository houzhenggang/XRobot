package com.robot.et.util;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;

/*
蓝牙按键控制机器人
 */
public class BluetoothKeyManager {

    public static boolean responseKey(int keyCode){
        Log.i("bluth", "keyCode==" + keyCode);
        boolean flag = false;
        switch (keyCode){
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                Log.i("bluth", "播放");
                handleResponse();
                flag = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                Log.i("bluth", "暂停");
                handleResponse();
                flag = true;
                break;
            default:
                break;

        }
        return flag;
    }

    private static void handleResponse(){
        BroadcastShare.stopSpeakOnly();
        BroadcastShare.stopListenerOnly();
        BroadcastShare.stopMusicOnly();
        DataConfig.isBluetoothBox = true;
        Intent intent = new Intent();
        intent.setAction(BroadcastAction.ACTION_RESUME_MONITOR_CHAT);
        CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
    }

}

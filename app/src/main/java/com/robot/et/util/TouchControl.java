package com.robot.et.util;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.enums.EmotionEnum;

/**
 * Created by houdeming on 2016/7/16.
 * app触摸机器人的控制
 */
public class TouchControl {

    private final static int TOUCH_HEAD = 0;//头
    private final static int TOUCH_EYE = 1;//眼睛
    private final static int TOUCH_HAND_LEFT = 2;//左手
    private final static int TOUCH_HAND_RIGHT = 3;//右手
    private final static int TOUCH_BELLY = 4;//肚子
    private final static int TOUCH_HAND_FOOT = 5;//脚

    public static void responseTouch(String touchKey){
        if(!TextUtils.isEmpty(touchKey)){
            if(TextUtils.isDigitsOnly(touchKey)){
                int key = Integer.parseInt(touchKey);
                String content = "";
                switch (key){
                    case TOUCH_HEAD://头
                        Log.i("netty", "头");
                        content = "摸摸我的头，感觉头变小了呢";
                        BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);

                        break;
                    case TOUCH_EYE://眼睛
                        Log.i("netty", "眼睛key===" + EmotionEnum.EMOTION_BLINK_TWO.getEmotionKey());
                        BroadcastShare.controlRobotEmotion(EmotionEnum.EMOTION_BLINK_TWO.getEmotionKey());
                        content = "看我眼睛大，你是不是很羡慕呢，嘿嘿";
                        BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);

                        break;
                    case TOUCH_HAND_LEFT://左手
                        Log.i("netty", "左手");
                        BroadcastShare.controlWaving(ScriptConfig.HAND_UP,ScriptConfig.HAND_LEFT,"0");
                        SystemClock.sleep(2000);
                        BroadcastShare.controlWaving(ScriptConfig.HAND_DOWN,ScriptConfig.HAND_LEFT,"0");
                        BroadcastShare.resumeChat();

                        break;
                    case TOUCH_HAND_RIGHT://右手
                        Log.i("netty", "右手");
                        BroadcastShare.controlWaving(ScriptConfig.HAND_UP,ScriptConfig.HAND_RIGHT,"0");
                        SystemClock.sleep(2000);
                        BroadcastShare.controlWaving(ScriptConfig.HAND_DOWN,ScriptConfig.HAND_RIGHT,"0");
                        BroadcastShare.resumeChat();

                        break;
                    case TOUCH_BELLY://肚子
                        Log.i("netty", "肚子");
                        content = "看我肚子这么大，你有木有羡慕呢，哈哈";
                        BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);

                        break;
                    case TOUCH_HAND_FOOT://脚
                        Log.i("netty", "脚");
                        content = "好久没人给我挠痒痒了呢，嘿嘿";
                        BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT, content);

                        break;
                    default:
                        break;

                }
            }

        }

    }
}

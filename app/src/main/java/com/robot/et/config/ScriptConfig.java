package com.robot.et.config;

import android.text.TextUtils;

import javax.jmdns.impl.DNSRecord;

//剧本的控制
public class ScriptConfig {

    //表情
    public static final int SCRIPT_EXPRESSION = 90001;
    //音乐
    public static final int SCRIPT_MUSIC = 90002;
    //跟随
    public static final int SCRIPT_FOLLOW = 90003;
    //转圈
    public static final int SCRIPT_TURN_AROUND = 90004;
    //问答
    public static final int SCRIPT_QUESTION_ANSWER = 90005;
    //说话
    public static final int SCRIPT_SPEAK = 90006;
    //手臂
    public static final int SCRIPT_HAND = 90007;
    //走
    public static final int SCRIPT_MOVE = 90008;
    //转
    public static final int SCRIPT_TURN = 90009;
    //停止
    public static final int SCRIPT_STOP = 90010;



    //顺时针
    public static final int SCRIPT_TURN_CLOCKWISE = 0;
    //逆时针
    public static final int SCRIPT_TURN_ANTI_CLOCKWISE = 1;
    //手举上
    public static final String HAND_UP = "up";
    //手举下
    public static final String HAND_DOWN = "down";
    //手停止动
    public static final String HAND_STOP = "stop";
    //手臂摆手
    public static final String HAND_WAVING = "waving";
    //左手
    public static final String HAND_LEFT = "Left";
    //右手
    public static final String HAND_RIGHT = "Right";
    //双手
    public static final String HAND_TWO = "LandR";
    //嘴的LED开
    public static final String LED_ON = "on";
    //嘴的LED关
    public static final String LED_OFF = "off";
    //嘴的LED闪烁
    public static final String LED_BLINK = "blink";


    //获取哪一个手
    public static String getHandCategory(String handCategory){
        String category = "";
        if(!TextUtils.isEmpty(handCategory)){
            if(TextUtils.equals(handCategory,"左手")){
                category = HAND_LEFT;
            }else if(TextUtils.equals(handCategory,"右手")){
                category = HAND_RIGHT;
            }else if(TextUtils.equals(handCategory,"双手")){
                category = HAND_TWO;
            }
        }
        return category;
    }

    //获取手的方向
    public static String getHandDirection(String handDirection){
        String direction = "";
        if(!TextUtils.isEmpty(handDirection)){
            if(TextUtils.equals(handDirection,"举手")){
                direction = HAND_UP;
            }else if(TextUtils.equals(handDirection,"放手")){
                direction = HAND_DOWN;
            }else if(TextUtils.equals(handDirection,"摆手")){
                direction = HAND_WAVING;
            }
        }
        return direction;
    }

    //获取转圈的方向
    public static int getTurnDirection(String turnDirection){
        int direction = 0;
        if(!TextUtils.isEmpty(turnDirection)){
            if(TextUtils.equals(turnDirection,"顺时针")){
                direction = SCRIPT_TURN_CLOCKWISE;
            }else if(TextUtils.equals(turnDirection,"逆时针")){
                direction = SCRIPT_TURN_ANTI_CLOCKWISE;
            }
        }
        return direction;
    }


}

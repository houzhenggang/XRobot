package com.robot.et.config;

//剧本的控制
public class ScriptConfig {

    //向前走
    public static final int SCRIPT_DIRECTION_FRONT = 90101;
    //向后走
    public static final int SCRIPT_DIRECTION_BACK = 90102;


    //说
    public static final int SCRIPT_SPEAK = 90200;


    //眨左眼
    public static final int SCRIPT_BLINK_LEFT = 90301;
    //眨右眼
    public static final int SCRIPT_BLINK_RIGHT = 90302;
    //眨眼（两个都眨）
    public static final int SCRIPT_BLINK_TWO = 90303;


    //唱
    public static final int SCRIPT_SING = 90400;


    //转圈
    public static final int SCRIPT_TURN_AROUND = 90500;
    //顺时针
    public static final int SCRIPT_TURN_CLOCKWISE = 90501;
    //逆时针
    public static final int SCRIPT_TURN_ANTI_CLOCKWISE = 90502;


    //摆左手
    public static final int SCRIPT_WAVING_LEFT = 90601;
    //摆右手
    public static final int SCRIPT_WAVING_RIGHT = 90602;
    //两只手都摆
    public static final int SCRIPT_WAVING_TWO = 90603;


    //向左转
    public static final int SCRIPT_TURN_LEFT = 90701;
    //向右转
    public static final int SCRIPT_TURN_RIGHT = 90702;


    //举左手
    public static final int SCRIPT_RAISE_HANDS_LEFT = 90801;
    //举右手
    public static final int SCRIPT_RAISE_HANDS_RIGHT = 90802;
    //手都举起来
    public static final int SCRIPT_RAISE_HANDS_TWO = 90803;
    //手放下来
    public static final int SCRIPT_HANDS_PUT_DOWN = 90804;


    //所有都停止
    public static final int SCRIPT_STOP_ALL = 90900;
    //停止说话
    public static final int SCRIPT_STOP_SPEAK = 90901;
    //停止唱歌
    public static final int SCRIPT_STOP_SING = 90902;
    //停止走
    public static final int SCRIPT_STOP_GO = 90903;
    //停止转圈
    public static final int SCRIPT_STOP_TURN = 90904;
    //停止摆手
    public static final int SCRIPT_STOP_WAVING = 90905;


}

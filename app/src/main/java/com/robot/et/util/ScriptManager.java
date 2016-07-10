package com.robot.et.util;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.ControlMoveConfig;
import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.core.software.agora.ChannelActivity;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houdeming on 2016/7/6.
 */
public class ScriptManager {

    //表演剧本
    public static void playScript(String content){
        if(!TextUtils.isEmpty(content)){
            if(ChannelActivity.instance != null){//正在音视频
                return;
            }
            BroadcastShare.stopSpeakOnly();
            BroadcastShare.stopListenerOnly();
            BroadcastShare.stopMusicOnly();
            SystemClock.sleep(1000);

            List<ScriptActionInfo> infos = DBUtils.getScriptActions(content);
            Log.i("netty", "playScript() infos.size()====" + infos.size());
            if(infos != null && infos.size() > 0){
                DataConfig.isPlayScript = true;
                doScriptAction(infos);
            }
        }
    }

    public static void doScriptAction(List<ScriptActionInfo> infos){
        Log.i("netty", "doScriptAction()   infos.size()====" +infos.size());
        if(infos != null && infos.size() > 0){
            ScriptActionInfo info = infos.get(0);
            Log.i("netty", "doScriptAction() info.getContent()====" + info.getContent());
            switch (info.getActionType()){
                case ScriptConfig.SCRIPT_DIRECTION_FRONT://向前走
                    Log.i("netty", "doScriptAction() 向前走");
                    BroadcastShare.controlMove(String.valueOf(ControlMoveConfig.MOVE_DIRECTION_FRONT),info.getContent());
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_DIRECTION_BACK://向后走
                    Log.i("netty", "doScriptAction() 向后走");
                    BroadcastShare.controlMove(String.valueOf(ControlMoveConfig.MOVE_DIRECTION_BACK),info.getContent());
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_SPEAK://说
                    Log.i("netty", "doScriptAction() 说");
                    BroadcastShare.textToSpeak(DataConfig.TYPE_SCRIPT, info.getContent());

                    break;
                case ScriptConfig.SCRIPT_BLINK_LEFT://眨左眼
                    Log.i("netty", "doScriptAction() 眨左眼");
                    BroadcastShare.controlBlink(ScriptConfig.SCRIPT_BLINK_LEFT);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_BLINK_RIGHT://眨右眼
                    Log.i("netty", "doScriptAction() 眨右眼");
                    BroadcastShare.controlBlink(ScriptConfig.SCRIPT_BLINK_RIGHT);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_BLINK_TWO://眨眼（两个都眨）
                    Log.i("netty", "doScriptAction() 眨眼（两个都眨）");
                    BroadcastShare.controlBlink(ScriptConfig.SCRIPT_BLINK_TWO);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_SING://唱
                    Log.i("netty", "doScriptAction() 唱");
                    PlayerControl.playScriptMusic(DataConfig.JPUSH_MUSIC,info.getContent(),info.getSpareContent());

                    break;
                case ScriptConfig.SCRIPT_TURN_AROUND://转圈
                    Log.i("netty", "doScriptAction() 转圈");
                    BroadcastShare.controlTurnAround(ScriptConfig.SCRIPT_TURN_CLOCKWISE,Integer.parseInt(info.getContent()));
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_WAVING_LEFT://摆左手
                    Log.i("netty", "doScriptAction() 摆左手");
                    BroadcastShare.controlWaving(ScriptConfig.SCRIPT_WAVING_LEFT);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_WAVING_RIGHT://摆右手
                    Log.i("netty", "doScriptAction() 摆右手");
                    BroadcastShare.controlWaving(ScriptConfig.SCRIPT_WAVING_RIGHT);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_WAVING_TWO://两只手都摆
                    Log.i("netty", "doScriptAction() 两只手都摆");
                    BroadcastShare.controlWaving(ScriptConfig.SCRIPT_WAVING_TWO);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_STOP_WAVING://停止摆手
                    Log.i("netty", "doScriptAction() 两只手都摆");
                    BroadcastShare.controlWaving(ScriptConfig.SCRIPT_STOP_WAVING);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_TURN_LEFT://向左转
                    Log.i("netty", "doScriptAction() 向左转");
                    BroadcastShare.controlMove(String.valueOf(ControlMoveConfig.MOVE_DIRECTION_LEFT),info.getContent());
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_TURN_RIGHT://向右转
                    Log.i("netty", "doScriptAction() 向右转");
                    BroadcastShare.controlMove(String.valueOf(ControlMoveConfig.MOVE_DIRECTION_RIGHT),info.getContent());
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_RAISE_HANDS_LEFT://举左手
                    Log.i("netty", "doScriptAction() 举左手");
                    BroadcastShare.controlRalseHands(ScriptConfig.SCRIPT_RAISE_HANDS_LEFT);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_RAISE_HANDS_RIGHT://举右手
                    Log.i("netty", "doScriptAction() 举右手");
                    BroadcastShare.controlRalseHands(ScriptConfig.SCRIPT_RAISE_HANDS_RIGHT);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_RAISE_HANDS_TWO://手都举起来
                    Log.i("netty", "doScriptAction() 手都举起来");
                    BroadcastShare.controlRalseHands(ScriptConfig.SCRIPT_RAISE_HANDS_TWO);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_HANDS_PUT_DOWN://手放下来
                    Log.i("netty", "doScriptAction() 手放下来");
                    BroadcastShare.controlRalseHands(ScriptConfig.SCRIPT_HANDS_PUT_DOWN);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_STOP_ALL://所有都停止
                    Log.i("netty", "doScriptAction() 所有都停止");
                    BroadcastShare.stopSpeakOnly();
                    BroadcastShare.stopMusicOnly();
                    BroadcastShare.controlMove(String.valueOf(ControlMoveConfig.MOVE_STOP),"");
                    BroadcastShare.controlTurnAround(ScriptConfig.SCRIPT_TURN_CLOCKWISE,ScriptConfig.SCRIPT_STOP_TURN);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_STOP_SPEAK://停止说话
                    Log.i("netty", "doScriptAction() 停止说话");
                    BroadcastShare.stopSpeakOnly();
                    setNewScriptInfos(infos,true,0);

                    break;
                case ScriptConfig.SCRIPT_STOP_SING://停止唱歌
                    Log.i("netty", "doScriptAction() 停止唱歌");
                    BroadcastShare.stopMusicOnly();
                    setNewScriptInfos(infos,true,0);

                    break;
                case ScriptConfig.SCRIPT_STOP_GO://停止走
                    Log.i("netty", "doScriptAction() 停止走");
                    BroadcastShare.controlMove(String.valueOf(ControlMoveConfig.MOVE_STOP),"");
                    SystemClock.sleep(2000);
                    setNewScriptInfos(infos,true,2000);

                    break;
                case ScriptConfig.SCRIPT_STOP_TURN://停止转圈
                    Log.i("netty", "doScriptAction() 停止转圈");
                    BroadcastShare.controlTurnAround(ScriptConfig.SCRIPT_TURN_CLOCKWISE,ScriptConfig.SCRIPT_STOP_TURN);
                    setNewScriptInfos(infos,true,2000);

                    break;
                default:
                    break;
            }

        }else{
            Log.i("netty", "doScriptAction()剧本执行完毕");
            DataConfig.isPlayScript = false;
        }
    }

    //更新剧本的内容
    public static void setNewScriptInfos(List<ScriptActionInfo> infos,boolean isResume,long delayTime){
        if(infos != null && infos.size() > 0){
            infos.remove(0);
            setScriptActionInfos(infos);
            //动作的执行全部延迟2000ms
            SystemClock.sleep(delayTime);

            if(isResume){
                doScriptAction(infos);
            }
        }else{
            Log.i("netty", "setNewScriptInfos()剧本执行完毕");
            DataConfig.isPlayScript = false;
        }
    }

    //插入剧本
    public static void addLocalScript(String scriptName){
        String content = Utilities.readFile(scriptName,"utf-8");
        GsonParse.parseScript(content, new GsonParse.ScriptCallBack() {
            @Override
            public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                if(info != null){
                    Log.i("netty", "addScript  size==" + infos.size());
                    if(infos != null && infos.size() > 0){
                        DBUtils.addScript(info,infos);
                    }
                }
            }
        });

    }

    //增加APP发过来的录制动作
    public static void addAppRecordAction(String content){
        if(!TextUtils.isEmpty(content)){
            GsonParse.parseAppRecordAction(content, new GsonParse.ScriptCallBack() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if(info != null){
                        Log.i("netty", "addScript  size==" + infos.size());
                        if(infos != null && infos.size() > 0){
                            DBUtils.addScript(info,infos);
                        }
                    }
                }
            });
        }
    }

    //增加APP发过来的音乐编舞
    public static void addAppRecordMusic(String content){
        if(!TextUtils.isEmpty(content)){
            GsonParse.parseAppRecordMusic(content, new GsonParse.ScriptCallBack() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if(info != null){
                        Log.i("netty", "addScript  size==" + infos.size());
                        if(infos != null && infos.size() > 0){
                            DBUtils.addScript(info,infos);
                        }
                    }
                }
            });
        }
    }

    private static List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();

    public static List<ScriptActionInfo> getScriptActionInfos() {
        return infos;
    }

    public static void setScriptActionInfos(List<ScriptActionInfo> infos) {
        ScriptManager.infos = infos;
    }

}

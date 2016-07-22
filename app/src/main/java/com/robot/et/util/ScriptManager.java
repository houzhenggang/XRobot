package com.robot.et.util;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.common.BroadcastShare;
import com.robot.et.common.DataConfig;
import com.robot.et.common.ScriptConfig;
import com.robot.et.core.software.agora.ChannelActivity;
import com.robot.et.db.RobotDB;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;
import com.robot.et.impl.ScriptImpl;

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
            BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");

            List<ScriptActionInfo> infos = getScriptActions(content);
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
            String content = info.getContent();
            Log.i("netty", "doScriptAction() info.getContent()====" + content);
            switch (info.getActionType()){
                case ScriptConfig.SCRIPT_EXPRESSION://表情
                    Log.i("netty", "doScriptAction() 表情");
                    int emotionKey = EnumManager.getEmotionKey(content);
                    Log.i("netty", "doScriptAction() emotionKey====" + emotionKey);
                    setScriptActionInfos(infos);
                    BroadcastShare.controlRobotEmotion(emotionKey);

                    break;
                case ScriptConfig.SCRIPT_MUSIC://音乐
                    Log.i("netty", "doScriptAction() 音乐");
                    setScriptActionInfos(infos);
                    PlayerControl.playScriptMusic(DataConfig.JPUSH_MUSIC,content,info.getSpareContent());

                    break;
                case ScriptConfig.SCRIPT_FOLLOW://跟随
                    Log.i("netty", "doScriptAction() 跟随");
                    String robotNum = "";
                    int toyCarNum = 0;
                    if (content.contains("机器人")) {
                        robotNum = info.getSpareContent();
                    } else if (content.contains("小车")) {
                        toyCarNum = MatchStringUtil.getToyCarNum(content);
                    }
                    Log.i("netty", "doScriptAction() robotNum====" + robotNum);
                    Log.i("netty", "doScriptAction() toyCarNum====" + toyCarNum);
                    BroadcastShare.controlFollow(robotNum,toyCarNum);
                    setNewScriptInfos(infos,true,getDealyTime(2000));

                    break;
                case ScriptConfig.SCRIPT_TURN_AROUND://转圈
                    Log.i("netty", "doScriptAction() 转圈");
                    int direction = ScriptConfig.getTurnDirection(content);
                    Log.i("netty", "doScriptAction() direction====" + direction);
                    Log.i("netty", "doScriptAction() num====" + info.getSpareContent());
                    BroadcastShare.controlTurnAround(direction,info.getSpareContent());
                    setNewScriptInfos(infos,true,getDealyTime(2000));

                    break;
                case ScriptConfig.SCRIPT_QUESTION_ANSWER://问答
                    Log.i("netty", "doScriptAction() 问答");
                    DataConfig.isScriptQA = true;
                    setScriptActionInfos(infos);
                    Log.i("netty", "doScriptAction() answer===" + info.getSpareContent());
                    String speakContent = content + "，请回答：" + info.getSpareContent();
                    BroadcastShare.textToSpeak(DataConfig.TYPE_SCRIPT, speakContent);

                    break;
                case ScriptConfig.SCRIPT_SPEAK://说话
                    Log.i("netty", "doScriptAction() 说话");
                    DataConfig.isScriptQA = false;
                    setScriptActionInfos(infos);
                    BroadcastShare.textToSpeak(DataConfig.TYPE_SCRIPT, content);

                    break;
                case ScriptConfig.SCRIPT_HAND://手
                    Log.i("netty", "doScriptAction() 手");
                    String handDirection = ScriptConfig.getHandDirection(info.getSpareContent());
                    String handCategory = ScriptConfig.getHandCategory(content);
                    Log.i("netty", "doScriptAction() handDirection===" + handDirection);
                    setScriptActionInfos(infos);
                    BroadcastShare.controlWaving(handDirection,handCategory,"1");

                    break;
                case ScriptConfig.SCRIPT_MOVE://走
                    Log.i("netty", "doScriptAction() 走");
                    String moveDirection = EnumManager.getScriptMoveKey(content);
                    String spareContent = info.getSpareContent();
                    Log.i("netty", "spareContent==" + spareContent);
                    int num = 0;
                    setScriptActionInfos(infos);
                    if(!TextUtils.isEmpty(spareContent)){
                        if (spareContent.contains("小车")) {
                            num = MatchStringUtil.getToyCarNum(spareContent);
                            BroadcastShare.controlToyCarMove(moveDirection,num);
                        }else{
                            BroadcastShare.controlMove(moveDirection);
                        }
                    }

                    break;
                case ScriptConfig.SCRIPT_TURN://左转右转
                    Log.i("netty", "doScriptAction() 左转右转");
                    String turnDirection = EnumManager.getScriptMoveKey(content);
                    setScriptActionInfos(infos);
                    BroadcastShare.controlMove(turnDirection);

                    break;
                case ScriptConfig.SCRIPT_STOP://停止
                    Log.i("netty", "doScriptAction() 停止");
                    BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
                    setNewScriptInfos(infos,true,getDealyTime(2000));

                    break;
                default:
                    break;
            }

        }else{
            Log.i("netty", "doScriptAction()剧本执行完毕");
            playScriptEnd();
        }
    }

    private static long getDealyTime(long custormTime){
        long time = 0;
        if(DataConfig.isPlayMusic){
            time = 4000;
        }else{
            time = custormTime;
        }
        return time;
    }

    //更新剧本的内容
    public static void setNewScriptInfos(List<ScriptActionInfo> infos,boolean isResume,long delayTime){
        if(infos != null && infos.size() > 0){
            infos.remove(0);
            setScriptActionInfos(infos);
            //动作的执行全部延迟2000ms
            SystemClock.sleep(delayTime);

            if(isResume){
                if(DataConfig.isPlayScript){
                    doScriptAction(infos);
                }else{
                    Log.i("netty", "DataConfig.isPlayScript===false 剧本执行完毕");
                }
            }
        }else{
            Log.i("netty", "setNewScriptInfos()剧本执行完毕");
            playScriptEnd();
        }
    }

    //表演剧本之前
    public static void playScriptStart(){
        BroadcastShare.stopSpeakOnly();
        BroadcastShare.stopListenerOnly();
        BroadcastShare.stopMusicOnly();
        BroadcastShare.controlMouthLED(ScriptConfig.LED_OFF);
        BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
    }

    //剧本执行完毕
    public static void playScriptEnd(){
        Log.i("netty", "playScriptEnd()");
        DataConfig.isPlayScript = false;
        DataConfig.isStartTime = false;
        if(!DataConfig.isPlayMusic){
            SystemClock.sleep(2000);
            BroadcastShare.controlMouthLED(ScriptConfig.LED_OFF);
            BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
        }
        if(DataConfig.isAppPushRemind){
            DataConfig.isAppPushRemind = false;
            BroadcastShare.resumeChat();
        }

        //重连netty
        BroadcastShare.connectNettyArgin();

    }

    //插入剧本
    public static void addLocalScript(String scriptName){
        String content = Utilities.readFile(scriptName,"utf-8");
        GsonParse.parseScript(content, new ScriptImpl() {
            @Override
            public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                if(info != null){
                    Log.i("netty", "addScript  size==" + infos.size());
                    if(infos != null && infos.size() > 0){
                        addScript(info,infos);
                    }
                }
            }
        });

    }

    //增加APP发来的图形编辑
    public static void addAppGraphicEdit(String content){
        if(!TextUtils.isEmpty(content)){
            GsonParse.parseScript(content, new ScriptImpl() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if(info != null){
                        Log.i("netty", "addScript  size==" + infos.size());
                        setScriptName(info.getScriptContent());
                        if(infos != null && infos.size() > 0){
                            addScript(info,infos);
                        }
                    }
                }
            });
        }
    }

    //增加APP发过来的录制动作
    public static void addAppRecordAction(String content){
        if(!TextUtils.isEmpty(content)){
            GsonParse.parseAppRecordAction(content, new ScriptImpl() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if(info != null){
                        Log.i("netty", "addScript  size==" + infos.size());
                        if(infos != null && infos.size() > 0){
                            addScript(info,infos);
                        }
                    }
                }
            });
        }
    }

    //增加APP发过来的音乐编舞
    public static void addAppRecordMusic(String content){
        if(!TextUtils.isEmpty(content)){
            GsonParse.parseAppRecordMusic(content, new ScriptImpl() {
                @Override
                public void getScribt(ScriptInfo info, List<ScriptActionInfo> infos) {
                    if(info != null){
                        Log.i("netty", "addScript  size==" + infos.size());
                        if(infos != null && infos.size() > 0){
                            addScript(info,infos);
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

    private static String scriptName;

    public static String getScriptName() {
        return scriptName;
    }

    public static void setScriptName(String scriptName) {
        ScriptManager.scriptName = scriptName;
    }

    //APP发来的提醒需求处理
    public static void handleAppScriptQA(String result){
        if(!TextUtils.isEmpty(result)){
            String answer = getScriptActionInfos().get(0).getSpareContent();
            if(!TextUtils.isEmpty(answer)){
                if(result.contains(answer)){//回答正确
                    DataConfig.isScriptQA = false;
                    setNewScriptInfos(getScriptActionInfos(),true,0);
                }else{//回答错误
                    BroadcastShare.resumeChat();
                }
            }
        }
    }

    //增加剧本
    public static void addScript(ScriptInfo info, List<ScriptActionInfo> infos){
        RobotDB mDao = RobotDB.getInstance();
        String scriptName = info.getScriptContent();
        int scriptId = mDao.getScriptId(scriptName);
        Log.i("netty", "addScript temId===" + scriptId);
        if(scriptId != -1){//已经存在
            Log.i("netty", "addScript 数据内容已存在");
            mDao.deleteScriptAction(scriptId);
        }else{//没有存在
            Log.i("netty", "addScript 无数据内容");
            mDao.addScript(info);
            scriptId = mDao.getScriptId(scriptName);
            Log.i("netty", "addScript scriptId===" + scriptId);
        }

        if(infos != null && infos.size() > 0){
            for(ScriptActionInfo actionInfo : infos){
                actionInfo.setScriptId(scriptId);
                mDao.addScriptAction(actionInfo);
            }
            Log.i("netty", "addScript 加入数据库成功");
        }
    }

    //获取剧本执行的动作
    public static List<ScriptActionInfo> getScriptActions(String scriptContent){
        List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
        if(!TextUtils.isEmpty(scriptContent)){
            RobotDB mDao = RobotDB.getInstance();
            int scriptId = mDao.getScriptId(scriptContent);
            Log.i("netty", "getScriptActions()  scriptId====" + scriptId);
            if(scriptId != -1){
                infos = mDao.getScriptActionInfos(scriptId);
            }
        }
        return infos;
    }

}

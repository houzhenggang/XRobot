package com.robot.et.util;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.core.software.agora.ChannelActivity;
import com.robot.et.entity.JpushInfo;

/**
 * Created by houdeming on 2016/7/5.
 */
public class NettyResultHandle {

    //推送来的结果
    public static void setPushResult(String result){
        if (!TextUtils.isEmpty(result)) {
            JpushInfo info = GsonParse.getJpushInfo(result);
            if(info != null){
                String direction = info.getDirection();
                if(TextUtils.isEmpty(direction)){
                    doPushResult(info);
                    return;
                }

                Log.i("netty", "direction===" + direction);
                if(TextUtils.isDigitsOnly(direction)){
                    BroadcastShare.controlMove(direction);
                }else{
                    if(!TextUtils.isEmpty(direction)){
                        String splite = "__";
                        if(direction.contains(splite)){//1_1(小车编号__方向指令)
                            String[] datas = direction.split(splite);
                            Log.i("netty", "datas[0]===" + datas[0]);
                            Log.i("netty", "datas[1]===" + datas[1]);
                            String carNum = datas[0];
                            int toyCarNum = 0;
                            if(!TextUtils.isEmpty(carNum)){
                                if(TextUtils.isDigitsOnly(carNum)){
                                    toyCarNum = Integer.parseInt(carNum);
                                }
                            }
                            BroadcastShare.controlToyCarMove(datas[1],toyCarNum);
                        }
                    }
                }

            }
        }
    }

    // 处理推送的结果
    public static void doPushResult(JpushInfo info) {
        if (info != null) {
            int extra = info.getExtra();
            String musicContent = info.getMusicContent();
            Log.i("netty", "pushCode===" + extra);
            Log.i("netty", "musicContent===" + musicContent);

            DataConfig.isJpushStop = false;

            switch (extra) {
                case DataConfig.JPUSH_MUSIC:// 音乐
                    Log.i("netty", "音乐");
                    PlayerControl.playMp3(DataConfig.JPUSH_MUSIC,"MUSIC",musicContent);

                    break;
                case DataConfig.JPUSH_STORY:// 故事
                    Log.i("netty", "故事");
                    PlayerControl.playMp3(DataConfig.JPUSH_STORY,"STORY",musicContent);

                    break;
                case DataConfig.JPUSH_SYNCHRONOUS_CLASSROOM:// 同步课堂
                    Log.i("netty", "同步课堂");
                    PlayerControl.playMp3(DataConfig.JPUSH_SYNCHRONOUS_CLASSROOM,"SYNCHRONOUS_CLASSROOM",musicContent);

                    break;
                case DataConfig.JPUSH_THOUSANDS_WHY:// 十万个为什么
                    Log.i("netty", "十万个为什么");
                    PlayerControl.playMp3(DataConfig.JPUSH_THOUSANDS_WHY,"THOUSANDS_WHY",musicContent);

                    break;
                case DataConfig.JPUSH_ENCYCLOPEDIAS:// 百科
                    Log.i("netty", "百科");
                    PlayerControl.playMp3(DataConfig.JPUSH_ENCYCLOPEDIAS,"ENCYCLOPEDIAS",musicContent);

                    break;
                case DataConfig.JPUSH_VOLUME_ADJUST:// 播放器音量控制
                    Log.i("netty", "播放器音量控制");
                    if(ChannelActivity.instance != null){
                        return;
                    }
                    if(!TextUtils.isEmpty(musicContent)){
                        PlayerControl.setMusicVolue(Double.parseDouble(musicContent));
                    }

                    break;
                case DataConfig.JPUSH_UPPER:// 上一首
                    Log.i("netty", "上一首 currentMediaType===" + PlayerControl.getCurrentMediaType());
                    PlayerControl.playMp3(PlayerControl.getCurrentMediaType(),PlayerControl.getCurrentMediaName(),musicContent);

                    break;
                case DataConfig.JPUSH_LOWER:// 下一首
                    Log.i("netty", "下一首 currentMediaType===" + PlayerControl.getCurrentMediaType());
                    PlayerControl.playMp3(PlayerControl.getCurrentMediaType(),PlayerControl.getCurrentMediaName(),musicContent);

                    break;
                case DataConfig.JPUSH_PAUSE:// 音乐暂停
                    Log.i("netty", "音乐暂停");
                    DataConfig.isJpushStop = true;
                    BroadcastShare.stopMusicOnly();
                    BroadcastShare.controlMouthLED(ScriptConfig.LED_OFF);
                    BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");

                    break;
                case DataConfig.JPUSH_GET_MEDIASTATE:// 获取媒体当前状态
                    Log.i("netty", "获取媒体当前状态");
                    if(DataConfig.isPlayMusic){//正在播放
                        PlayerControl.pushMediaState(PlayerControl.getCurrentMediaName(), "open", PlayerControl.getCurrentPlayName());
                    }else{
                        PlayerControl.pushMediaState("", "close", "");
                    }

                    break;
                case DataConfig.JPUSH_ALARM:// 闹铃
                    Log.i("netty", "闹铃");
                    BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
                    AlarmRemindManager.setAlarm(info);

                    break;
                case DataConfig.JPUSH_REMIND:// APP提醒
                    Log.i("netty", "APP提醒");
                    BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
                    AlarmRemindManager.setAppAlarmRemind(musicContent);

                    break;
                case DataConfig.JPUSH_ROBOT_LEARN:// 机器人问答库
                    Log.i("netty", "机器人问答库");
                    RobotLearnManager.learnChatByApp(true, info);

                    break;
                case DataConfig.JPUSH_ROBOT_SPEAK:// 机器人学习库，通过说话学习
                    Log.i("netty", "机器人问答库通过说话学习");
                    RobotLearnManager.learnChatByApp(musicContent);

                    break;
                case DataConfig.JPUSH_PERSON_LEARN:// 个人问答库
                    Log.i("netty", "个人问答库");
                    RobotLearnManager.learnChatByApp(false, info);

                    break;
                case DataConfig.JPUSH_UPDATE_USERPHONE_INFO:// 更新用户联系方式
                    Log.i("netty", "更新用户联系方式");
                    //do  nothing
                    break;
                case DataConfig.JPUSH_PLAY_SCRIPT:// 表演剧本
                    Log.i("netty", "表演剧本");
                    if(ChannelActivity.instance != null){
                        return;
                    }
                    ScriptManager.playScriptStart();
                    ScriptManager.playScript(musicContent);

                    break;
                case DataConfig.JPUSH_PATROL_MOVING_TRACK:// 本体巡逻移动轨迹
                    Log.i("netty", "本体巡逻移动轨迹");

                    break;
                case DataConfig.JPUSH_RECORDING_ACTION:// 录制动作
                    Log.i("netty", "录制动作");
                    ScriptManager.addAppRecordAction(musicContent);

                    break;
                case DataConfig.JPUSH_DELETE_A_MESSAGE:// 删除留言
                    Log.i("netty", "删除留言");
                    AlarmRemindManager.deleteAppRemindTips(musicContent);

                    break;
                case DataConfig.JPUSH_CHOREOGRAPHY_DANCE:// 为某首歌曲编排舞蹈
                    Log.i("netty", "为某首歌曲编排舞蹈");
                    ScriptManager.addAppRecordMusic(musicContent);

                    break;
                case DataConfig.JPUSH_SCENE_INTERACTION:// 场景互动
                    Log.i("netty", "场景互动");
                    if(ChannelActivity.instance != null){
                        return;
                    }
                    ScriptManager.playScriptStart();
                    ScriptManager.playScript(musicContent);

                    break;
                case DataConfig.JPUSH_GRAPHIC_EDITOR:// 图形编辑
                    Log.i("netty", "图形编辑");
                    ScriptManager.addAppGraphicEdit(musicContent);

                    break;
                case DataConfig.JPUSH_FROLIC:// 嬉闹
                    Log.i("netty", "嬉闹");
                    if(ChannelActivity.instance != null){
                        return;
                    }
                    ScriptManager.playScriptStart();
                    TouchControl.responseTouch(musicContent);

                    break;

                default:// agora音视频
                    Log.i("netty", "agora音视频");
                    Intent intent = new Intent();
                    intent.setAction(BroadcastAction.ACTION_JOIN_AGORA_ROOM);
                    intent.putExtra("JpushInfo", info);
                    CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
                    break;
            }

        }
    }
}

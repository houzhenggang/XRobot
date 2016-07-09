package com.robot.et.util;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.entity.JpushInfo;
import com.robot.et.enums.ControlMoveEnum;

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
                ControlMoveEnum moveEnum = EnumManager.getControlMove(direction);
                Log.i("netty", "moveEnum===" + moveEnum);
                if(moveEnum != null){
                    switch (moveEnum){
                        case FORWARD://向前
                            sendDirection(direction);
                            break;
                        case BACKWARD://向后
                            sendDirection(direction);
                            break;
                        case LEFT://向左
                            sendDirection(direction);
                            break;
                        case RIGHT://向右
                            sendDirection(direction);
                            break;
                        case STOP://停止
                            sendDirection(direction);
                            break;
                        default:
                            break;
                    }
                }

            }
        }
    }

    //发送走的指令
    public static void sendDirection(String direction){
        Intent intent = new Intent();
        intent.setAction(BroadcastAction.ACTION_CONTROL_ROBOT_MOVE);
        intent.putExtra("direction", direction);
        if(!TextUtils.equals("5", direction)){
            intent.putExtra("digit", "10");
        }
        CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
    }

    // 处理推送的结果
    public static void doPushResult(JpushInfo info) {
        if (info != null) {
            int extra = info.getExtra();
            String musicContent = info.getMusicContent();
            Log.i("netty", "pushCode===" + extra);
            Log.i("netty", "musicContent===" + musicContent);

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
                    BroadcastShare.stopMusicOnly();

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
                    Log.i("netty", "JPUSH_ALARM");
                    AlarmRemindManager.setAlarm(info);

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
                    ScriptManager.playScript(musicContent);

                    break;
                case DataConfig.JPUSH_PATROL_MOVING_TRACK:// 本体巡逻移动轨迹
                    Log.i("netty", "本体巡逻移动轨迹");

                    break;
                case DataConfig.JPUSH_RECORDING_ACTION:// 录制动作
                    Log.i("netty", "录制动作");

                    break;
                case DataConfig.JPUSH_DELETE_A_MESSAGE:// 删除留言
                    Log.i("netty", "删除留言");

                    break;
                case DataConfig.JPUSH_CHOREOGRAPHY_DANCE:// 为某首歌曲编排舞蹈
                    Log.i("netty", "为某首歌曲编排舞蹈");

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

package com.robot.et.config;

public class BroadcastAction {
	
    /*
     *广播 MONITOR_WATCH 总控制
     *
     */
	//网络连接
	public static String ACTION_MONITOR_WATCH_NETWORK_CONNECT = "action.monitor.watch.network.connect";
	//网络断开
	public static String ACTION_MONITOR_WATCH_NETWORK_DISCONNECT = "action.monitor.watch.network.disconnect";
	//网络流量状态监控
	public static String ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_OPEN = "action.monitor.watch.network.traffic.open";
	//关闭网络流量状态监控
	public static String ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_CLOSE = "action.monitor.watch.network.traffic.close";
	//检测网络变化
	public static String ACTION_MONITOR_WATCH_NETWORK_TRAFFIC_SPEED = "action.monitor.watch.network.traffic.speed";
	
	
	
	//把语音转化为文字读出来的广播
	public static String ACTION_VOICE_TO_TEXT_SPEAK = "com.robot.et.voice.to.text.speak";
	//音乐开始播放的广播
	public static String ACTION_MUSIC_PLAY = "com.robot.et.music.play";
	//音乐播放结束的广播
	public static String ACTION_MUSIC_PLAY_END = "com.robot.et.music.play.end";
	//监听电话挂断的广播
	public static String ACTION_PHONE_HANGUP = "com.robot.et.phone.hangup";
	//监听电话进来的广播
	public static String ACTION_PHONE_COMEIN = "com.robot.et.phone.comein";
	//监听对话的广播
	public static String ACTION_MONITOR_CHAT = "com.robot.et.monitor.chat";
	//音乐开始播放的广播
	public static String ACTION_MUSIC_PLAY_START = "com.robot.et.music.play.start";
	//仅仅停止音乐播放的广播
	public static String ACTION_STOP_MUSIC_ONLY = "com.robot.et.stop.music.only";
	//仅仅停止说话的广播
	public static String ACTION_STOP_SPEAK_ONLY = "com.robot.et.stop.speak.only";
	//继续监听对话的广播
	public static String ACTION_RESUME_MONITOR_CHAT = "com.robot.et.resume.monitor.chat";
	//停止监听对话的广播
	public static String ACTION_STOP_LISTENER = "com.robot.et.stop.listener";
	//控制机器人移动不带距离
	public static String ACTION_CONTROL_ROBOT_MOVE = "action.control.robot.move";
	//控制机器人移动带距离
	public static String ACTION_CONTROL_ROBOT_MOVE_WITH_DISTANCE = "action.control.robot.move.with.distance";
	//控制机器人转向
	public static String ACTION_CONTROL_ROBOT_TURN ="action.control.robot.turn";
	//串口数据接收
	public static String ACTION_MOVE_TO_SERIALPORT ="action.control.robot.serialport";
	//图灵接受需要理解的内容广播
	public static String ACTION_TURING_RECEIVER = "action.turing.receiver";
	//语音控制打电话前的提示的广播
	public static String ACTION_CALL_PHONE = "action.call.phone";
	//动态路线规划
//	public static String ACTION_MOVE_DYNAMIC_ROTUE_PLAN="action.move.dynamic.rotue.plan";
	//极光推送自定义消息的广播
	public static String ACTION_JPUSH = "action.jpush";
	//打开极光推送的广播
	public static String ACTION_OPEN_JPUSH = "action.open.jpush";
	//唤醒或打断并且转向的广播
	public static String ACTION_WAKE_UP_AND_MOVE = "action.wake.up.and.move";
	//控制机器人表情
	public static String ACTION_CONTROL_ROBOT_EMOTION = "action.contorl.robot.emotion";
	//极光推送关闭声网agora
	public static String ACTION_CLOSE_AGORA = "action.close.agora";
	//闹铃时间到的广播
	public static String ACTION_ALARM_ARRIVED = "action.alarm.arrived";
	//查询完百度地图
	public static String ACTION_GET_LOCATION = "action.get.location";
	//检测是否联网
	public static String ACTION_NET_CONNECT_STATE = "com.robot.et.network.connect.state";
	//扫描二维码结果
	public static String ACTION_SCAN_QR_CODE ="com.robot.et.zxing.result";
	//科大讯飞文本理解
	public static String ACTION_IFLY_TEXT_UNDERSTANDER = "com.robot.et.ifly.text.understander";
	//闭嘴的广播
	public static String ACTION_WAKE_UP_RESET = "action.wake.up.reset";
	//连接agora的广播
	public static String ACTION_CONNECT_AGORA = "action.connect.agora";
	//进入agora的广播
	public static String ACTION_JOIN_AGORA_ROOM = "action.join.agora.room";
	//关闭agora界面的广播
	public static String ACTION_CLOSE_AGORA_ACTIVITY = "action.close.agora.activity";
	//自动播放下一首音乐的广播
	public static String ACTION_PLAY_LOWER = "action.play.lower";
	//打开netty的广播
	public static String ACTION_OPEN_NETTY = "action.open.netty";
	//摆手的广播
	public static String ACTION_CONTROL_WAVING = "action.control.waving";
	//控制机器人周围小车的广播
	public static String ACTION_CONTROL_AROUND_TOYCAR = "action.control.around.toycar";
	//控制机器人嘴LED灯的广播
	public static String ACTION_CONTROL_MOUTH_LED = "action.control.mouth.led";
	//跟随的广播
	public static String ACTION_CONTROL_ROBOT_FOLLOW = "action.control.robot.follow";

}

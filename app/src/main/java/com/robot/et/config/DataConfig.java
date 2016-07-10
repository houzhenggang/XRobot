package com.robot.et.config;


public class DataConfig {
	
	//HostName   internet:139.196.185.52  localHost:192.168.1.28
	public static final String HOST = "139.196.185.52";
	//Port
	public static final int PORT = 9999;
	//科大讯飞的appid
	public static final String SPEECH_APPID = "570e1085";

	//图灵的appid
	public static final String TURING_APPID = "8314e713b83b80dbe26264214907bce1";
	//图灵的secret
	public static final String TURING_SECRET = "b4e5061c950ea99a";
	//图灵的UNIQUEID   填写一个任意的标示，没有具体要求，但一定要写
	public static final String TURING_UNIQUEID = "131313131";
	
	//声网arora key值
	public static final String AGORA_KEY = "67ddec80abde4933b1672a186c9bdb3e";
	//声网arora的crasheye key 值
	public static final String AGORA_CRASHEYE_KEY = "06798b00";
	
	//声网arora传递的key值
	public final static String AGORA_EXTRA_CALLING_TYPE = "extra_calling_type";
    public final static String AGORA_EXTRA_VENDOR_KEY = "extra_vendor_key";
    public final static String AGORA_EXTRA_CHANNEL_ID = "extra_channel_id";
	
	// 极光推送的类型值
	// 视频
    public final static int JPUSH_CALL_VIDEO = 40004;
	//语音
	public final static int JPUSH_CALL_VOICE = 40001;
	//查看
	public final static int JPUSH_CALL_LOOK = 40007; 
	//关闭声网
	public final static int JPUSH_CALL_CLOSE = 60000; 
	//agora语音转视频
	public final static int JPUSH_CALL_VOICE_TO_VIDEO = 40014; 
	//agora视频转语音
	public final static int JPUSH_CALL_VIDEO_TO_VOICE = 40041; 
	//更新用户电话联系方式
	public final static int JPUSH_UPDATE_USERPHONE_INFO = 40003; 
	//给本体设置闹钟
	public final static int JPUSH_ALARM = 70001; 
	//机器人问答库
	public final static int JPUSH_ROBOT_LEARN = 70002; 
	//个人问答库
	public final static int JPUSH_PERSON_LEARN = 70003; 
	//音乐
	public final static int JPUSH_MUSIC = 40006; 
	//故事
	public final static int JPUSH_STORY = 40008; 
	//同步课堂
	public final static int JPUSH_SYNCHRONOUS_CLASSROOM = 40009; 
	//十万个为什么
	public final static int JPUSH_THOUSANDS_WHY = 40010; 
	//百科
	public final static int JPUSH_ENCYCLOPEDIAS = 40011; 
	//音量调节
	public final static int JPUSH_VOLUME_ADJUST = 40017; 
	//上一首
	public final static int JPUSH_UPPER = 40018; 
	//下一首
	public final static int JPUSH_LOWER = 40019; 
	//音乐暂停
	public final static int JPUSH_PAUSE = 40020; 
	//获取媒体当前状态
	public final static int JPUSH_GET_MEDIASTATE = 40021; 
	//表演剧本
	public final static int JPUSH_PLAY_SCRIPT = 40002; 
	//机器人看世界
	public final static int JPUSH_ROBOT_LOOK_WORLD = 40012; 
	//机器人说话
	public final static int JPUSH_ROBOT_SPEAK = 40013; 
	//视觉学习
	public final static int JPUSH_VISUAL_LEARNING = 40015; 
	//场景互动
	public final static int JPUSH_SCENE_INTERACTION = 40016; 
	//本体媒体播放通知
	public final static int JPUSH_MEDIA_PLAY_NOTIFY = 40022;
	//本体巡逻移动轨迹
	public final static int JPUSH_PATROL_MOVING_TRACK = 40023;
	//录制动作
	public final static int JPUSH_RECORDING_ACTION = 40024;
	//删除留言
	public final static int JPUSH_DELETE_A_MESSAGE = 40025;
	//为某首歌曲编排舞蹈
	public final static int JPUSH_CHOREOGRAPHY_DANCE = 40026;

	// 声音提示默认发音人 在线合成
	public static final String VOICER_TIPS_DEFAULT = "nannan";
	// 声音提示默认发音人  本地合成
	public static final String VOICER_TIPS_LOCAL = "xiaoyan";
	//英语发音人
	public static final String VOICER_TIPS_DEFAULT_ENGLISH = "catherine";
	
	//是否语种切换
	public static boolean isLanguageSwitch = false;
	
	//提醒广播的标志
	public static String ACTION_REMIND_SIGN = "&ALARM&";
	
	//发广播的类型
	public static final String TYPE_KEY = "type_key";
	public static final int TYPE_WELCOME_CONTENT = 0;
	public static final int TYPE_MUSIC_PLAY_START = 1;
	public static final int TYPE_VOICE_CHAT = 3;
	public static final int TYPE_REMIND_TIPS = 5;
	public static final int TYPE_NETWORK_CONNECT = 6;
	public static final int TYPE_NETWORK_BREAKOFF = 7;
	public static final int TYPE_CALL_PHONE = 9;
	public static final int TYPE_PHONE_ERROR = 10;
	public static final int TYPE_AGORA_HANGUP = 11;
	public static final int TYPE_DO_NOTHING = 13;
	public static final int TYPE_SCRIPT = 14;

	//是否正在播放音乐
	public static boolean isPlayMusic = false;
	
	//是否正在说话
	public static boolean isSpeaking = false;
	
	//已提醒
	public static final int REMIND_HAD_ID = 1;
	//未提醒
	public static final int REMIND_NO_ID = 0;
	
	//网络连接
	public static String NETWORK_CONNECT = "网络已连接";
	//网络断开
	public static String NETWORK_BREAK_OFF = "网络已断开，请检查网络设置";
	
	//音乐文件不存在了
	public static String MUSIC_NOT_EXIT = "抱歉，主人，音乐文件已经不存在了，再换一首别的歌吧";
	
	//打电话类型
	public static String PHONE_CALL_KEY = "phone_call_key";
	//打电话类型：被叫
	public static final int PHONE_CALL_BY_MEN = 0;
	//打电话类型：主动呼叫
	public static final int PHONE_CALL_TO_MEN = 1;
	
	//agora正常模式 默认
	public static final int AGORA_CALL_NORMAL_PATTERN = 0;
	//agora免打扰模式
	public static final int AGORA_CALL_DISTURB_NOT_PATTERN = 1;
	
	//机器人状态
	//正常模式
	public static final String ROBOT_STATUS_NORMAL = "12015";
	//免打扰模式
	public static final String ROBOT_STATUS_DISYURB_NOT = "12014";
	
	//当打电话时，没有那个人的电话的时候，要说的话
	public static String CALL_NO_PHONENUM = "主人，还没有这个人的电话呢，换个试试吧";
	
	//机器人问答
	public static final int LEARN_BY_ROBOT = 0;
	//个人问答
	public static final int LEARN_BY_PERSON = 1;
	
	//多个手机号之间的连接符
	public final static String PHONE_SPLITE = ",";
	
	// 提醒内容中间的分隔符
	public static String SCHEDULE_SPLITE = ",";
	
	//歌曲信息连接符
	public static String MUSIC_SPLITE = "&";
	
	//科大讯飞查询天气没有获取当前城市
	public static boolean isGetCity = false;
	
	//天气解析异常
	public static boolean isParseWeatherError = false;
			
	//当前是agora视频
	public static boolean isAgoraVideo = false;
	
	//当前是agora语音
	public static boolean isAgoraVoice = false;
	
	//当前是否连接网络
	public static boolean isConnectNetWork = false;
	
	//闹铃或提醒是否执行
	public static boolean isAlarmTips = true;
	
	//电话类型，当前正在查看
	public static boolean isAgoraLook = false;
	
	//设置为每天的闹铃
	public static int alarmAllDay = 0;
	
	//mp3文件来自 0:第三方  1:极光推送
	public static final int MUSIC_SRC_FROM_OTHER = 0;
	public static final int MUSIC_SRC_FROM_JPUSH = 1;
	
	//是用极光推送播放音乐还是第三方科大讯飞
	public static boolean isJpushPlayMusic = false;
	//是剧本里播放的音乐
	public static boolean isScriptPlayMusic = false;

	//使用蓝牙音箱
	public static boolean isBluetoothBox = false;

	//在表演剧本
	public static boolean isPlayScript = false;

	//app是否推送了暂停
	public static boolean isJpushStop = false;

}

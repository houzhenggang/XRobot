package com.robot.et.enums;

//控制表情的指令
public enum EmotionEnum {

	EMOTION_NORMAL(6000,"正常"),
	EMOTION_SMILE(6001,"笑"),
	EMOTION_WEEP(6007,"哭"),
	EMOTION_SURPRISED(6000,"惊讶"),
	EMOTION_SLEEP(6008,"睡觉"),
	EMOTION_BLINK_LEFT(6000,"眨左眼"),
	EMOTION_BLINK_RIGHT(6000,"眨右眼"),
	EMOTION_BLINK_TWO(6000,"眨双眼"),
	EMOTION_SWEAT(6000,"流汗"),
	EMOTION_HALO(6000,"晕"),
	EMOTION_EYES_WHITE(6000,"白眼"),
	EMOTION_DOUBT(6000,"疑问"),
	EMOTION_LOOK_LEFT(6005,"向左看"),
	EMOTION_LOOK_RIGHT(6006,"向右看"),
	EMOTION_ANGER(6002,"怒"),
	EMOTION_LOVELY(6004,"可爱");

	private int emotionKey;
	private String emotionName;

	EmotionEnum(int emotionKey, String emotionName) {
		this.emotionKey = emotionKey;
		this.emotionName = emotionName;
	}

	public int getEmotionKey() {
		return emotionKey;
	}

	public String getEmotionName() {
		return emotionName;
	}

}

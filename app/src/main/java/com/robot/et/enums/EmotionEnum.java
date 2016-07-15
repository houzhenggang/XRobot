package com.robot.et.enums;

//控制表情的指令
public enum EmotionEnum {

	EMOTION_SMILE(600001,"笑"),EMOTION_WEEP(600003,"哭"),EMOTION_SURPRISED(600002,"惊讶"),EMOTION_SLEEP(600005,"睡觉"),
	EMOTION_BLINK_LEFT(600001,"眨左眼"),EMOTION_BLINK_RIGHT(600001,"眨右眼"),EMOTION_BLINK_TWO(600001,"眨双眼"),EMOTION_SWEAT(600002,"流汗"),
	EMOTION_HALO(600002,"晕"),EMOTION_EYES_WHITE(600005,"白眼"),EMOTION_DOUBT(600002,"疑问"),EMOTION_ANGER(600002,"怒"),
	EMOTION_LOVELY(600004,"可爱");

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

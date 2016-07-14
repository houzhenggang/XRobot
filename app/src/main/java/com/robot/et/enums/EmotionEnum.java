package com.robot.et.enums;

//控制表情的指令
public enum EmotionEnum {

	EMOTION_SMILE(600001,"微笑"),EMOTION_WEEP(600003,"哭泣"),EMOTION_SURPRISED(600002,"惊讶"),EMOTION_SLEEP(600005,"睡觉"),
	EMOTION_BLINK_LEFT(90101,"眨左眼"),EMOTION_BLINK_RIGHT(90102,"眨右眼"),EMOTION_BLINK_TWO(90103,"眨双眼"),EMOTION_SWEAT(600002,"流汗"),
	EMOTION_ANGER(600002,"愤怒"),EMOTION_LOVELY(600004,"可爱");

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

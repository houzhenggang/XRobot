package com.robot.et.core.hardware.emotion;

public class Emotion {
	
	static {
		System.loadLibrary("Emotion");
	}
	
	public static native int init(); 
	public static native int setEmotion(int id);
    public static native int close(); 
}

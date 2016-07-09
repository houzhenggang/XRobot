package com.robot.et.core.software.system;

import com.robot.et.app.CustomApplication;

import android.content.Context;
import android.media.AudioManager;

public class MediaManager {
	private static MediaManager instance = null;
	private AudioManager mAudioManager;
	
	private MediaManager(){
		Context context = CustomApplication.getInstance().getApplicationContext();
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}
	
	private static synchronized void syncInit(){
		if(instance == null){
			instance = new MediaManager();
		}
	}
	
	public static MediaManager getInstance(){
		if(instance == null){
			syncInit();
		}
		return instance;
	}

	// 增加音量
	public void increaseVolume() {
		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
	}

	// 降低音量
	public void reduceVolume() {
		if (getCurrentVolume() > 0) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER,AudioManager.FX_FOCUS_NAVIGATION_UP);
		}
	}

	// 获取最大音量值
	public int getMaxVolume() {
		return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	// 获取当前音量值
	public int getCurrentVolume() {
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	// 设置当前音量
	public void setCurrentVolume(int volumeValue) {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, 0);
	}

	// 设置最大音量
	public void setMaxVolume() {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,getMaxVolume(), 0);
	}
	
}

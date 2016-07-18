package com.robot.et.util;

import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.app.CustomApplication;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.config.UrlConfig;
import com.robot.et.core.software.agora.ChannelActivity;
import com.robot.et.core.software.okhttp.HttpEngine;
import com.robot.et.core.software.system.MediaManager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

public class PlayerControl {

	// 获取播放音频的路径
	public static String getMusicSrc(int fileType, String musicName) {
		String fileSrc = getTempSrc(fileType);
		fileSrc += File.separator + musicName + ".mp3";
		return fileSrc;
	}
	
	// 获取下一首mp3文件的路径
	public static String getLowerMusicSrc(int fileType,String currentMusicName) {
		String fileSrc = getTempSrc(fileType);
		Log.i("jpush", "playcontrol  fileSrc==="+fileSrc);
		Log.i("jpush", "playcontrol  currentMusicName==="+currentMusicName);
		File file = new File(fileSrc);
		String[] names = file.list();
		if(names != null && names.length > 0){
			for(int i=0;i<names.length;i++){
				String name = names[i];
				Log.i("jpush", "playcontrol  name==="+name);
				if(TextUtils.equals(name, currentMusicName)){
					if(i == names.length - 1){
						return fileSrc += File.separator + names[0];
					}
					return fileSrc += File.separator + names[i + 1];
				}
			}
		}
		
		return null;
	}
	
	private static String getTempSrc(int fileType){
		String fileSrc = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "robot" + File.separator;
		
		switch (fileType) {
		case DataConfig.JPUSH_MUSIC:
			fileSrc += "音乐";
			break;
		case DataConfig.JPUSH_STORY:
			fileSrc += "故事";
			break;
		case DataConfig.JPUSH_SYNCHRONOUS_CLASSROOM:
			fileSrc += "同步课堂";
			break;
		case DataConfig.JPUSH_THOUSANDS_WHY:
			fileSrc += "十万个为什么";
			break;
		case DataConfig.JPUSH_ENCYCLOPEDIAS:
			fileSrc += "百科";
			break;

		default:
			break;
		}

		return fileSrc;
	}

	// 设置音乐音量
	public static void setMusicVolue(double volumeValue) {
		int maxValue = MediaManager.getInstance().getMaxVolume();
		int currentValue = (int) (maxValue * volumeValue);
		Log.i("jpush", "音量maxValue===" + maxValue);
		Log.i("jpush", "音量当前值currentValue===" + currentValue);
		MediaManager.getInstance().setCurrentVolume(currentValue);
	}
	
	// 播放音乐
	public static void playMusic(int srcType,int mediaType,String result) {
		String content = "";
		String playPrompt = "开始播放";
		DataConfig.isScriptPlayMusic = false;
		if(srcType == DataConfig.MUSIC_SRC_FROM_OTHER){//第三方
			DataConfig.isJpushPlayMusic  = false;
			String[] datas = result.split(DataConfig.MUSIC_SPLITE);
			// 歌手+歌名 + 歌曲src
			DataManager.setContentSrc(datas[2]);
			content = "好的，" + playPrompt + datas[0] + "，" + datas[1];
		}else {//极光推送
			DataConfig.isJpushPlayMusic  = true;
			String musicSrc = getMusicSrc(mediaType, result);
			Log.i("jpush", "musicSrc===" + musicSrc);
			DataManager.setContentSrc(musicSrc);
			
			switch (mediaType) {
			case DataConfig.JPUSH_MUSIC:
				content = playPrompt + result;
				break;
			case DataConfig.JPUSH_STORY:
				content = playPrompt + result;
				break;
			case DataConfig.JPUSH_SYNCHRONOUS_CLASSROOM:
				content = playPrompt + result;
				break;
			case DataConfig.JPUSH_THOUSANDS_WHY:
				content = playPrompt + result;
				break;
			case DataConfig.JPUSH_ENCYCLOPEDIAS:
				content = playPrompt + result;
				break;

			default:
				break;
			}
		}
		BroadcastShare.textToSpeak(DataConfig.TYPE_MUSIC_PLAY_START, content);
	}

	//获取音乐的名字不带.mp3
	public static String getMusicName(String content){
		if(!TextUtils.isEmpty(content)){
			int start = content.lastIndexOf("/");
			int end = content.indexOf(".mp3");
			return content.substring(start + 1, end);
		}
		return "";
	}
	
	//播放音乐
	public static void startPlayMusic(String url){
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_MUSIC_PLAY);
		intent.putExtra("url", url);
		CustomApplication.getInstance().getApplicationContext().sendBroadcast(intent);
	}

	//播放剧本音乐
	public static void playScriptMusic(int mediaType,String content,String url){
		String musicSrc = "";
		DataConfig.isJpushPlayMusic  = false;
		DataConfig.isScriptPlayMusic = true;
		if(!TextUtils.isEmpty(content)){
			musicSrc = getMusicSrc(mediaType, content);
		}else{
			musicSrc = url;
		}
		startPlayMusic(musicSrc);
	}

	//向APP发送当前媒体播放的状态
	public static void pushMediaState(String meidaType,String mediaState,String playName){
		final SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
		HttpEngine.Param[] params = new HttpEngine.Param[]{
				new HttpEngine.Param("mobile", share.getString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, "")),
				new HttpEngine.Param("robotNumber", share.getString(SharedPreferencesKeys.ROBOT_NUM, "")),
				new HttpEngine.Param("mediaType", meidaType),
				new HttpEngine.Param("mediaState", mediaState),
				new HttpEngine.Param("playName", playName)
		};
		HttpEngine httpEngine = HttpEngine.getInstance();
		Request request = httpEngine.createRequest(UrlConfig.PUSH_MEDIASTATE_TO_APP, params);
		Call call = httpEngine.createRequestCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onFailure(Request arg0, IOException arg1) {
			}
			@Override
			public void onResponse(Response response) throws IOException {
				String result = response.body().string();
				Log.i("json", "result====" + result);
				if(GsonParse.isChangeStatusSuccess(result)){
					Log.i("json", "向APP发送媒体状态成功");
				}
				BroadcastShare.connectNettyArgin();
			}

		});
	}

	//当前播放的媒体类型
	private static int currentMediaType;
	//当前播放的媒体名字
	private static String currentMediaName;
	//当前播放的歌名
	private static String currentPlayName;

	public static void setCurrentMediaType(int mediaType){
		currentMediaType = mediaType;
	}

	public static int getCurrentMediaType(){
		return  currentMediaType;
	}

	public static void setCurrentPlayName(String playName){
		currentPlayName = playName;
	}

	public static String getCurrentPlayName(){
		return  currentPlayName;
	}

	public static void setCurrentMediaName(String mediaName){
		currentMediaName = mediaName;
	}

	public static String getCurrentMediaName(){
		return currentMediaName;
	}

	//播放.mp3文件
	public static void playMp3(int mediaType,String mediaName,String content){
		setCurrentMediaType(mediaType);
		setCurrentMediaName(mediaName);
		setCurrentPlayName(content);

		if(ChannelActivity.instance != null){//正在音视频
			PlayerControl.pushMediaState("VIDEO", "close", "");
			return;
		}
		pushMediaState(mediaName, "open", content);
		BroadcastShare.stopSpeakOnly();
		BroadcastShare.stopMusicOnly();
		BroadcastShare.stopListenerOnly();
		DataConfig.isPlayScript = false;
		BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
		if(DataConfig.isJpushStop){
			DataConfig.isJpushStop = false;
			return;
		}
		playMusic(DataConfig.MUSIC_SRC_FROM_JPUSH,mediaType, content);
	}

}

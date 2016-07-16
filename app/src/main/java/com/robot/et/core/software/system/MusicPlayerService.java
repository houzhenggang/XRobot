package com.robot.et.core.software.system;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.ScriptConfig;
import com.robot.et.debug.Logger;
import com.robot.et.util.BroadcastShare;
import com.robot.et.util.PlayerControl;
import com.robot.et.util.ScriptManager;

import java.io.IOException;

public class MusicPlayerService extends Service{

	// 媒体播放器对象  
	 private MediaPlayer mediaPlayer;
	 
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer = new MediaPlayer(); 
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_STOP_MUSIC_ONLY);
		filter.addAction(BroadcastAction.ACTION_PLAY_LOWER);
		registerReceiver(receiver, filter);
		
		//设置音乐播放完成时的监听器 
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				Logger.i("音乐播放完成");
				DataConfig.isPlayMusic = false;
				BroadcastShare.controlMouthLED(ScriptConfig.LED_OFF);
				BroadcastShare.controlWaving(ScriptConfig.HAND_STOP,ScriptConfig.HAND_TWO,"0");
				Intent intent = new Intent();
				intent.setAction(BroadcastAction.ACTION_MUSIC_PLAY_END);
				sendBroadcast(intent);
			}
		});
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BroadcastAction.ACTION_STOP_MUSIC_ONLY)){// 仅仅把音乐停止掉
				if(mediaPlayer.isPlaying()){
					mediaPlayer.stop(); // 音乐停止播放  
				}
			}else if(intent.getAction().equals(BroadcastAction.ACTION_PLAY_LOWER)){//是推送来的音乐时，自动播放下一首
				Log.i("netty", "MusicPlayerService currentMediaType===" + PlayerControl.getCurrentMediaType());
				Log.i("netty", "MusicPlayerService currentPlayName===" + PlayerControl.getCurrentPlayName());
				String playSrc = PlayerControl.getLowerMusicSrc(PlayerControl.getCurrentMediaType(), PlayerControl.getCurrentPlayName() + ".mp3");
				Log.i("netty", "MusicPlayerService playSrc==="+playSrc);
				if(!TextUtils.isEmpty(playSrc)){
					DataConfig.isJpushPlayMusic  = true;
					PlayerControl.setCurrentPlayName(PlayerControl.getMusicName(playSrc));
					PlayerControl.pushMediaState(PlayerControl.getCurrentMediaName(), "open", PlayerControl.getCurrentPlayName());
					PlayerControl.startPlayMusic(playSrc);
				}
			}
		}
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String path = intent.getStringExtra("url");
		if(!TextUtils.isEmpty(path)){
			try {
				mediaPlayer.reset();// 把各项参数恢复到初始状态
				mediaPlayer.setDataSource(path);
				// 进行缓冲
				mediaPlayer.prepare();
				mediaPlayer.setOnPreparedListener(new PreparedListener());
			} catch (IllegalStateException e) {
				//报异常提示文件不存在
				String tempContent = DataConfig.MUSIC_NOT_EXIT;
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT,tempContent);
			} catch (IOException e) {
				//报异常提示文件不存在
				String tempContent = DataConfig.MUSIC_NOT_EXIT;
				BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT,tempContent);
			} 
			
		}else{
			//提示文件不存在
			String tempContent = DataConfig.MUSIC_NOT_EXIT;
			BroadcastShare.textToSpeak(DataConfig.TYPE_VOICE_CHAT,tempContent);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	//实现一个OnPrepareLister接口,当音乐准备好的时候开始播放 
	 private final class PreparedListener implements OnPreparedListener {  
	        @Override  
	        public void onPrepared(MediaPlayer mp) {  
	        	Logger.i("音乐开始播放");
				DataConfig.isPlayMusic = true;
				mediaPlayer.start(); // 开始播放
				if(DataConfig.isScriptPlayMusic){
					ScriptManager.setNewScriptInfos(ScriptManager.getScriptActionInfos(),true,0);
				}else{
					ScriptManager.playScript(PlayerControl.getCurrentPlayName());
				}
	        }  
	    }  
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		if (mediaPlayer != null) {  
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();  
			}
            mediaPlayer.release();  
            mediaPlayer = null;  
        }  
	}

}

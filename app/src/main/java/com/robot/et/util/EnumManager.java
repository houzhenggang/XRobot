package com.robot.et.util;

import android.text.TextUtils;

import com.robot.et.enums.ControlMoveEnum;
import com.robot.et.enums.EmotionEnum;
import com.robot.et.enums.MatchSceneEnum;
import com.robot.et.enums.SceneServiceEnum;

import java.util.Random;

public class EnumManager {

	// 获取自定义的场景枚举值
	public static MatchSceneEnum getSceneEnum(String str) {
		for (MatchSceneEnum sceneEnum : MatchSceneEnum.values()) {
			if (sceneEnum.isScene(str)) {
				return sceneEnum;
			}
		}
		return null;
	}
	
	//获取科大讯飞提供的场景service
	public static SceneServiceEnum getIflyService(String str){
		for(SceneServiceEnum serviceEnum : SceneServiceEnum.values()){
			if(TextUtils.equals(str, serviceEnum.getServiceKey())){
				return serviceEnum;
			}
		}
		return null;
	}
	
	//获取控制走的枚举值
	public static String getControlMove(String str){
		for(ControlMoveEnum moveEnum : ControlMoveEnum.values()){
			if(CharactorTool.getFullSpell(str).contains(CharactorTool.getFullSpell(moveEnum.getMoveName()))){
				return moveEnum.getMoveKey();
			}
		}
		return "";
	}

	//获取表情的int型值
	public static int getEmotionKey(String emotionName){
		int key = 0;
		if(!TextUtils.isEmpty(emotionName)){
			if(TextUtils.equals(emotionName,"随意表情")){
				EmotionEnum[] motions = EmotionEnum.values();
				int size = motions.length;
				if(motions != null && size > 0){
					Random random = new Random();
					int randNum = random.nextInt(size);
					EmotionEnum newEnum = motions[randNum];
					key = newEnum.getEmotionKey();
				}
			}else{
				for(EmotionEnum emotionEnum:EmotionEnum.values()){
					if(TextUtils.equals(emotionName,emotionEnum.getEmotionName())){
						key = emotionEnum.getEmotionKey();
					}
				}
			}
		}
		return key;
	}

	//获取剧本控制走的枚举值
	public static String getScriptMoveKey(String str){
		for(ControlMoveEnum moveEnum : ControlMoveEnum.values()){
			if(TextUtils.equals(str, moveEnum.getMoveName())){
				return moveEnum.getMoveKey();
			}
		}
		return null;
	}

	//获取表情的枚举值
	public static EmotionEnum getEmotionEnum(String str){
		if(!TextUtils.isEmpty(str)){
			for(EmotionEnum emotionEnum : EmotionEnum.values()){
				if(CharactorTool.getFullSpell(str).contains(CharactorTool.getFullSpell(emotionEnum.getEmotionName()))){
					return emotionEnum;
				}
			}
		}
		return null;
	}

}

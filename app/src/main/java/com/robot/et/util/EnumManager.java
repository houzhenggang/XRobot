package com.robot.et.util;

import android.text.TextUtils;

import com.robot.et.enums.ControlMoveEnum;
import com.robot.et.enums.MatchSceneEnum;
import com.robot.et.enums.SceneServiceEnum;

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
	public static ControlMoveEnum getControlMove(String str){
		for(ControlMoveEnum moveEnum : ControlMoveEnum.values()){
			if(TextUtils.equals(str, moveEnum.getMoveKey())){
				return moveEnum;
			}
		}
		return null;
	}


}

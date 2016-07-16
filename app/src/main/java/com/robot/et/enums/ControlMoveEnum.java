package com.robot.et.enums;

//控制动的指令
public enum ControlMoveEnum {

	FORWARD("1","前进"),
	FORWARD2("1","向前"),
	FORWARD3("1","过来"),
	BACKWARD("2","后退"),
	BACKWARD2("2","向后"),
	BACKWARD3("2","再退"),
	LEFT("3","左转"),
	LEFT2("3","向左"),
	RIGHT("4","右转"),
	RIGHT2("4","向右"),
	STOP("5","停止");

	private String moveKey;
	private String moveName;

	private ControlMoveEnum(String moveKey, String moveName){
		this.moveKey = moveKey;
		this.moveName = moveName;
	}

	public String getMoveKey() {
		return moveKey;
	}

	public String getMoveName() {
		return moveName;
	}

}

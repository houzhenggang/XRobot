package com.robot.et.enums;

//控制走的指令
public enum ControlMoveEnum {

	FORWARD("1","前进"),BACKWARD("2","后退"),LEFT("3","左转"),RIGHT("4","右转"),
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

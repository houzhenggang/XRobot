package com.robot.et.enums;

//控制走的指令
public enum ControlMoveEnum {

	FORWARD("1","向前"),BACKWARD("2","向后"),LEFT("3","向左"),RIGHT("4","向右"),
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

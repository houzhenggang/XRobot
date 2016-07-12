package com.robot.et.entity;

public class RobotAction {
	private String category; 
	private String action;  
	private int angle; // 转弯角度
	private int radius; // 转弯半径
	private int distance; //移动距离
	private int time; //移动时间
	private String toyCarNum; //机器人周围小车编号

	public RobotAction() {
		super();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getToyCarNum() {
		return toyCarNum;
	}

	public void setToyCarNum(String toyCarNum) {
		this.toyCarNum = toyCarNum;
	}

}

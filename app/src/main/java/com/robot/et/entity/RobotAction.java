package com.robot.et.entity;

public class RobotAction {
	private String category; 
	private String action;  
	private int angle; // 转弯角度
	private int radius; // 转弯半径
	private int distance; //移动距离
	private int time; //移动时间

	public RobotAction() {
		super();
	}

	public RobotAction(String category, String action, int angle, int radius,
			int distance, int time) {
		super();
		this.category = category;
		this.action = action;
		this.angle = angle;
		this.radius = radius;
		this.distance = distance;
		this.time = time;
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

}

package com.robot.et.entity;

/**
 * Created by houdeming on 2016/7/16.
 */
public class ImageInfo {
    private int width;//图片宽
    private int height;//图片高
    private float resolution;//分辨率
    private double robotX;//机器人坐标x
    private double robotY;//机器人坐标Y
    private double mapX;//地图坐标X
    private double mapY;//地图坐标Y

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getResolution() {
        return resolution;
    }

    public void setResolution(float resolution) {
        this.resolution = resolution;
    }

    public double getRobotX() {
        return robotX;
    }

    public void setRobotX(double robotX) {
        this.robotX = robotX;
    }

    public double getRobotY() {
        return robotY;
    }

    public void setRobotY(double robotY) {
        this.robotY = robotY;
    }

    public double getMapX() {
        return mapX;
    }

    public void setMapX(double mapX) {
        this.mapX = mapX;
    }

    public double getMapY() {
        return mapY;
    }

    public void setMapY(double mapY) {
        this.mapY = mapY;
    }

    public ImageInfo() {
        super();
    }
}

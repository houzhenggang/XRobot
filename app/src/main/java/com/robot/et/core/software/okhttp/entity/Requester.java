package com.robot.et.core.software.okhttp.entity;

import java.util.HashMap;
import java.util.Map;

//okhttp请求实体类
public class Requester {
	public String os = "android";
	public String ver = "v1.0";
	public String lg = "zh";
	public int cmd;
	public String uid;
	public String pwd;
	public Map<String, Object> body = new HashMap<String, Object>();
	public Requester() {
		super();
	}

}

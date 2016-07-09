package com.robot.et.entity;

import java.io.Serializable;

/**
 * @ClassName: CommandMsg
 * @Description: TODO(控制指令消息体)
 * @author ZHY
 * @date 2016年7月4日 上午11:22:51
 * @Copyright © 2016上海小黄人互动机器人有限公司
 */
public class CommandMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	private String from;
	private String to;
	private String msg;

	public CommandMsg(String from, String to, String msg) {
		super();
		this.from = from;
		this.to = to;
		this.msg = msg;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}

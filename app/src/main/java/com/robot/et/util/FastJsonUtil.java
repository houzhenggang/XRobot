package com.robot.et.util;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName: FastJsonUtil
 * @Description: TODO(阿里json工具类)
 * @author ZHY
 * @date 2016年7月6日 下午1:41:40
 * @Copyright © 2016上海小黄人互动机器人有限公司
 */
public class FastJsonUtil {

	/**
	 * @method getJsonField(基于fastjson字段key的value提取)
	 * @return Object
	 * @author ZHY
	 * @date 2016年6月1日 下午3:33:39
	 */
	public static String getJsonField(String jsonStr, String key) {
		return JSON.parseObject(jsonStr).getString(key);
	}
}

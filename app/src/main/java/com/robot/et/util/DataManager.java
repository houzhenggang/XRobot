package com.robot.et.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.text.TextUtils;

import com.robot.et.R;
import com.robot.et.app.CustomApplication;
import com.robot.et.debug.Logger;

public class DataManager {

	//得到当前时间的欢迎语
	public static String getWelcomeContent() {
		int currentHour = DateTools.getCurrentHour(System.currentTimeMillis());
		Context context = CustomApplication.getInstance().getApplicationContext();
		String content = "";
		if (0 <= currentHour && currentHour < 12) {// 早上
			content = context.getResources().getString(R.string.voice_tips_morning);
		} else if (12 <= currentHour && currentHour < 13) {// 中午
			content = context.getResources().getString(R.string.voice_tips_noon);
		} else if (13 <= currentHour && currentHour < 18) {// 下午
			content = context.getResources().getString(R.string.voice_tips_afternoon);
		} else if (18 <= currentHour && currentHour < 24) {// 晚上
			content = context.getResources().getString(R.string.voice_tips_night);
		}
		return content;
	}
	
	// 当从科大讯飞与图灵都没有返回来的数据时
	public static String getDataNoAnswer() {
		String result = "";
		Context context = CustomApplication.getInstance().getApplicationContext();
		String[] datas = context.getResources().getStringArray(R.array.voice_custorm_chat);
		if(datas != null && datas.length > 0){
			for (int i = 0; i < datas.length; i++) {
				Random random = new Random();
				int randNum = random.nextInt(datas.length);
				result = datas[randNum];
			}
		}
		return result;
	}
	
	//获取穿衣提醒    weather:天气 temperature:温度的度数
	public static String getDressTips(String weather, String temperature) {
		String tips = "";
		Context context = CustomApplication.getInstance().getApplicationContext();
		// 天气气象提示
		String[] weathers = context.getResources().getStringArray(R.array.weather_tips);
		if (weathers != null && weathers.length > 0) {
			for (int i = 0; i < weathers.length; i++) {
				if (TextUtils.equals(weather, weathers[i])) {
					String[] dresss = context.getResources().getStringArray(R.array.weather_dress_tips);
					tips = "外出指数：" + dresss[i] + "。";
				}
			}
		}
		String dressContent = "";
		int degree = Integer.parseInt(temperature);
		Logger.i("degree==" + degree);
		// 温度区间
		if (degree < -20) {
			dressContent = "推荐穿衣：" + "棉衣、冬大衣、厚呢外套、呢帽、软毛厚帽子、手套、冲锋衣、厚羽绒服、裘皮大衣等厚重保暖衣服等。"  + "温馨提示：" + "如外出请尽量带上帽子和手套、口罩等等，避免冷气直接与身体接触引起感冒和冻疮。";
		} else if (degree >= -20 && degree < -10) {
			dressContent = "推荐穿衣：" + "棉衣、冬大衣、皮夹克、厚呢外套、呢帽、手套、羽绒服、皮袄等。" + "温馨提示：" + "如外出请尽量带上帽子和手套、口罩等等，避免冷气直接与身体接触引起感冒和冻疮。";
		} else if (degree >= -10 && degree < 0) {
			dressContent = "推荐穿衣：" + "圆领毛衣、保暖内衣、针织棉内衣、内穿型薄羽绒衣、高领厚毛衣、羊绒衫等。" + "温馨提示：" + "如外出请尽量带上帽子和手套、口罩等等，避免冷气直接与身体接触引起感冒和冻疮。";
		} else if (degree >= 0 && degree < 5) {
			dressContent = "推荐穿衣：" + "羽绒服、羊绒大衣、皮夹克等。" + "温馨提示：" + "年老体弱者建议穿着厚军大衣、冬大衣或者厚羽绒服等。";
		} else if (degree >= 5 && degree < 10) {
			dressContent = "推荐穿衣：" + "棉服、羽绒服、皮夹克加羊毛衫等。" + "温馨提示：" + "年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。";
		} else if (degree >= 10 && degree < 15) {
			dressContent = "推荐穿衣：" + "皮衣、薄棉外套、风衣、棉衣、厚外套等。" + "温馨提示：" + "保暖内衣很重要，在寒冷的天气里它是你身体的一件“暖水壶”哦！";
		} else if (degree >= 15 && degree < 20) {
			dressContent = "推荐穿衣：" + "薄外套、针织衫、卫衣、牛仔衬衫、风衣、运动外套等。" + "温馨提示：" + "外面最好套上一款外套，内里加上毛衣或者针织衫，幼儿和老人最好套上一条保暖内衣。";
		} else if (degree >= 20 && degree < 25) {
			dressContent = "推荐穿衣：" + "短衬衫、短T恤、卫衣、薄风衣、薄皮衣等。" + "温馨提示：" + "老人与幼儿请注意添加增减衣物。";
		} else if (degree >= 25) {
			dressContent = "推荐穿衣：" + "短裤、短衬衫、短T恤、薄外套、单卫衣等。" + "温馨提示：" + "要注意防晒，尤其在夏天感冒咳嗽很难受的哦！";
		}
		return tips + dressContent;
	}
	
	// 得到天气的区域
	public static String getWeatherArea(String content) {
		String area = "";
		if (!TextUtils.isEmpty(content)) {
			if (content.contains("市") && content.contains("区")) {
				int begin = content.indexOf("市");
				int end = content.indexOf("区");
				area = content.substring(begin + 1, end + 1);
			}
		}
		return area;
	}

	/*
	 * content weather格式 上海:05/16 周一,15-24° 23° 晴 北风微风; 05/17 周二,16-26° 晴 东南风微风;
	 * 05/18 周三,17-26° 多云 东风微风; 05/19 周四,19-26° 多云 东风微风;
	 */
	public static String getWeatherContent(String cityName, String area,String content) {
		String result = "";
		if (!TextUtils.isEmpty(content)) {
			String[] datas = content.split(";");
			result = datas[0];
			String[] tempDatas = result.split(",");
			result = tempDatas[1];
			String cityData = tempDatas[0];
			String[] citys = cityData.split("\\:");
			String city = citys[0];

			String[] weathers = result.split(" ");
			// weathers[0]15-24° weathers[1]23° weathers[2]晴 weathers[3]北风微风
			if (weathers != null && weathers.length > 0) {
				if (!TextUtils.isEmpty(area)) {
					if (!TextUtils.isEmpty(cityName)) {
						result = cityName + area;
					} else {
						result = city + "市" + area;
					}
				} else {
					if (!TextUtils.isEmpty(cityName)) {
						result = cityName;
					} else {
						result = city + "市";
					}
				}
				// result = result + "今天天气：" + weathers[2] + ",气温：" +
				// weathers[1] + ",风力：" + weathers[3] + ","
				// + VoiceCommenUtils.getDressTips(weathers[2],
				// weathers[1].substring(0, weathers[1].length() - 1));
				result = result + "天气：" + weathers[2] + ",气温：" + weathers[0] + ",风力：" + weathers[3];
			}
		}
		return result;
	}

	// 得到天气的温度值 气温范围25℃~19℃ °和℃都是一个字符
	public static String getTemperature(String content) {
		String result = "";
		if (!TextUtils.isEmpty(content)) {
			String[] datas = content.split("\\~");
			if (datas != null && datas.length > 0) {
				String data1 = datas[0].substring(0, datas[0].length() - 1);
				String data2 = datas[1].substring(0, datas[1].length() - 1);
				Logger.i("data1===" + data1);
				Logger.i("data2===" + data2);
				int tempData = (Integer.parseInt(data1) + Integer.parseInt(data2)) / 2;
				result = String.valueOf(tempData);
			}
		}
		Logger.i("result===" + result);
		return result;
	}
	
	private static String mContent;
	public static void setContentSrc(String content){
		mContent = content;
	}
	
	public static String getContentSrc(){
		return mContent;
	}
	
	private static List<String> datas = new ArrayList<String>();
	public static void setListData(List<String> strs){
		datas = strs;
	}
	
	public static List<String> getListData(){
		return datas;
	}
	
}

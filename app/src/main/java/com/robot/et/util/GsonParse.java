package com.robot.et.util;

import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.RecognizerResult;
import com.robot.et.config.DataConfig;
import com.robot.et.entity.JpushInfo;
import com.robot.et.entity.RemindInfo;
import com.robot.et.entity.RobotInfo;
import com.robot.et.entity.ScriptActionInfo;
import com.robot.et.entity.ScriptInfo;
import com.robot.et.enums.SceneServiceEnum;
import com.robot.et.impl.ScriptImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GsonParse {
	private final static String TAG = "jsonparse";

	public static RobotInfo getRobotInfo(String json){
		RobotInfo info = null;
		if(!TextUtils.isEmpty(json)){
			try {
				JSONTokener tokener = new JSONTokener(json);
				JSONObject object = new JSONObject(tokener);
				String message = object.getString("message");
				String resultCode = object.getString("resultCode");
				if(TextUtils.equals(resultCode, "00")){
					JSONObject jObject = object.getJSONObject("robot");
					info = new RobotInfo();
					info.setRobotId(jObject.getInt("id"));
					info.setRobotNum(jObject.getString("robotNumber"));
				}
			} catch (JSONException e) {
				Log.i(TAG, "getRobotInfo JSONException");
				info = null;
			}
		}
		return info;
	}
	
	public static JpushInfo getJpushInfo(String jsonData) {
		JpushInfo info = null;
		if (!TextUtils.isEmpty(jsonData)) {
			try {
				JSONTokener tokener = new JSONTokener(jsonData);
				JSONObject jsonObject = new JSONObject(tokener);
				String msg = jsonObject.getString("msg");
				if(!TextUtils.isEmpty(msg)){
					info = new JpushInfo();
					if(!msg.contains("pushCode")){
						info.setDirection(msg);
						return info;
					}

					JSONTokener tokener2 = new JSONTokener(msg);
					JSONObject json = new JSONObject(tokener2);
					if(json.has("content")){
						String content = json.getString("content");
						if(!TextUtils.isEmpty(content)){
							info.setContent(content);
						}
					}

					if(json.has("roomNumber")){
						String roomNumber = json.getString("roomNumber");
						if(!TextUtils.isEmpty(roomNumber)){
							info.setRoomNum(roomNumber);
						}
					}

					if(json.has("pushCode")){
						String extra = json.getString("pushCode");
						if(!TextUtils.isEmpty(extra)){
							info.setExtra(Integer.parseInt(extra));
						}
					}

					if(json.has("comandContent")){
						String comandContent = json.getString("comandContent");
						if(!TextUtils.isEmpty(comandContent)){
							info.setMusicContent(comandContent);
						}
					}

					if(json.has("mobile")){
						String mobile = json.getString("mobile");
						Log.i(TAG, "mobile===="+mobile);
						if(!TextUtils.isEmpty(mobile)){
							SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
							share.putString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, mobile);
							share.commitValue();
						}
					}

					if(json.has("setAlarmTime")){
						String alarmTime = json.getString("setAlarmTime");
						if(!TextUtils.isEmpty(alarmTime)){
							info.setAlarmTime(alarmTime);
						}
					}

					if(json.has("setAlarmContent")){
						String alarmContent = json.getString("setAlarmContent");
						if(!TextUtils.isEmpty(alarmContent)){
							info.setAlarmContent(alarmContent);
						}
					}

					if(json.has("setRemindNum")){
						info.setRemindNum(json.getInt("setRemindNum"));
					}

					if(json.has("setFreInterval")){
						info.setRemindInteval(json.getInt("setFreInterval"));
					}

					if(json.has("setFrequency")){
						info.setFrequency(json.getInt("setFrequency"));
					}

					if(json.has("question")){
						info.setQuestion(json.getString("question"));
					}

					if(json.has("answer")){
						info.setAnswer(json.getString("answer"));
					}

					if(json.has("user")){
						JSONObject object = json.getJSONObject("user");
						String mobile = object.getString("mobile");
						Log.i(TAG, "mobile===="+mobile);
						SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
						share.putString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, mobile);
						share.commitValue();
					}

				}

			} catch (JSONException e) {
				Log.i(TAG, "getJpushInfo JSONException");
				info = null;
			}
		}
		return info;
	}
	
	public static String getRoomNum(String result){
		String roomNumber = "";
		if(!TextUtils.isEmpty(result)){
			try {
				JSONTokener tokener = new JSONTokener(result);
				JSONObject object = new JSONObject(tokener);
				String resultCode = object.getString("resultCode");
				if(TextUtils.equals(resultCode, "00")){
					JSONObject json = object.getJSONObject("agora");
					roomNumber = json.getString("roomNumber");
					if(object.has("requestUser")){
						JSONObject jObject = object.getJSONObject("requestUser");
						String mobile = jObject.getString("mobile");
						String userName = jObject.getString("username");
						if(!TextUtils.isEmpty(userName)){
							DataManager.setContentSrc(userName);
						}
						Log.i(TAG, "mobile===="+mobile);
						Log.i(TAG, "userName===="+userName);
						SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
						share.putString(SharedPreferencesKeys.AGORA_CALL_PHONENUM, mobile);
						share.commitValue();
						
					}
				}
			} catch (JSONException e) {
				Log.i(TAG, "getRoomNum JSONException");
				roomNumber = "";
			}
		}
		return roomNumber;
	}
	
	//修改机器状态是否成功
	public static boolean isChangeStatusSuccess(String result){
		if(!TextUtils.isEmpty(result)){
			try {
				JSONTokener tokener = new JSONTokener(result);
				JSONObject object = new JSONObject(tokener);
				String resultCode = object.getString("resultCode");
				if(TextUtils.equals(resultCode, "00")){
					return true;
				}
			} catch (JSONException e) {
				Log.i(TAG, "isChangeStatusSuccess JSONException");
				return false;
			}
		}
		return false;
	}
	
	// 科大讯飞语音听写的结果json解析
	public static String printResult(RecognizerResult results,
			HashMap<String, String> mIatResults) {
		String text = parseVoiceToTextResult(results.getResultString());
		String sn = "";
		// 读取json结果中的sn字段
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mIatResults.put(sn, text);
		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		String result = resultBuffer.toString();
		return result;
	}

	// 科大讯飞语音听写json解析
	public static String parseVoiceToTextResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				// 转写结果词，默认使用第一个结果
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
				// 如果需要多候选结果，解析数组其他字段
				// for(int j = 0; j < items.length(); j++)
				// {
				// JSONObject obj = items.getJSONObject(j);
				// ret.append(obj.getString("w"));
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}

	/*
	 * 科大讯飞语义理解的json解析
	 * service + question + answer
	 */
	public static String[] parseAnswerResult(String result) {
		String[] results = null;
		try {
			JSONTokener tokener = new JSONTokener(result);
			JSONObject jObject = new JSONObject(tokener);
			int isSuccessInt = jObject.getInt("rc");
			results = new String[3];
			String question = jObject.getString("text");
			results[1] = question;

			// rc=0 操作成功
			if (isSuccessInt == 0) {
				String service = jObject.getString("service");

				results[0] = service;
				SceneServiceEnum serviceEnum = EnumManager.getIflyService(service);
				Log.i(TAG, "serviceEnum===" + serviceEnum);
				if(serviceEnum != null){
					switch (serviceEnum) {
					case BAIKE://百科
						results[2] = getAnswerData(jObject);
						break;
					case CALC://计算器
						results[2] = getAnswerData(jObject);
						break;
					case COOKBOOK://菜谱
						results[2] = getCookBookData(jObject);
						break;
					case DATETIME://日期
						results[2] = getAnswerData(jObject);
						break;
					case FAQ://社区问答
						results[2] = getAnswerData(jObject);
						break;
					case FLIGHT://航班查询
						// do nothing
						results[2] = "";
						break;
					case HOTEL://酒店查询
						// do nothing
						results[2] = "";
						break;
					case MAP://地图查询
						// do nothing
						results[2] = "";
						break;
					case MUSIC://音乐
						results[2] = getMusicData(jObject);
						break;
					case RESTAURANT://餐馆
						// do nothing
						results[2] = "";
						break;
					case SCHEDULE://提醒
						results[2] = getRemindData(jObject);
						break;
					case STOCK://股票查询
						// do nothing
						results[2] = "";
						break;
					case TRAIN://火车查询
						// do nothing
						results[2] = "";
						break;
					case TRANSLATION://翻译
						// do nothing
						results[2] = "";
						break;
					case WEATHER://天气查询
						results[2] = getWeatherData(jObject);
						break;
					case OPENQA://褒贬&问候&情绪
						results[2] = getAnswerData(jObject);
						break;
					case TELEPHONE://打电话
						results[2] = getPhoneData(jObject);
						break;
					case MESSAGE://发短信
						// do nothing
						results[2] = "";
						break;
					case CHAT://闲聊
						results[2] = getAnswerData(jObject);
						break;
					case PM25://空气质量
						results[2] = getPm25Data(jObject);
						break;
						
					default:
						results[2] = "";
						break;
					}
				}else{
					results[0] = "";
					results[2] = "";
				}
				
			} else {
				// 返回的 rc!=0 无内容
				results[0] = "";
				results[2] = "";
			}

		} catch (JSONException e) {
			Log.i(TAG, "parseIatAnswerResult  JSONException");
			DataConfig.isParseWeatherError = true;
			results = null;
		}
		return results;
	}
	
	
	//百科,计算器,日期,社区问答,褒贬&问候&情绪,闲聊
	private static String getAnswerData(JSONObject jObject){
		String json = "";
		try {
			JSONObject jsonObject = jObject.getJSONObject("answer");
			json = jsonObject.getString("text");
		} catch (JSONException e) {
			Log.i(TAG, "getAnswerData  JSONException");
		}
		return json;
	}
	
	//获取菜谱
	private static String getCookBookData(JSONObject jObject){
		String json = "";
		try {
			JSONObject jsonObject = jObject.getJSONObject("data");
			JSONArray cookArray = jsonObject.getJSONArray("result");
			List<String> cooks = new ArrayList<String>();
			for (int i = 0; i < cookArray.length(); i++) {
				JSONObject object = cookArray.getJSONObject(i);
				String ingredient = object.getString("ingredient");// 主要材料
				String accessory = object.getString("accessory");// 辅助材料
				String content = "";
				if (!TextUtils.isEmpty(ingredient) && TextUtils.isEmpty(accessory)) {
					content = "主料：" + ingredient;
				} else if (TextUtils.isEmpty(ingredient) && !TextUtils.isEmpty(accessory)) {
					content = "辅料：" + accessory;
				} else if (!TextUtils.isEmpty(ingredient) && !TextUtils.isEmpty(accessory)) {
					content = "主料：" + ingredient + "辅料：" + accessory;
				}
				cooks.add(content);
			}

			int size = cooks.size();
			if (cooks != null && size > 0) {
				Random random = new Random();
				int randNum = random.nextInt(size);
				json = cooks.get(randNum);
			}
		} catch (JSONException e) {
			Log.i(TAG, "getCookBookData  JSONException");
		}
		return json;
	}
	
	//获取音乐
	private static String getMusicData(JSONObject jObject){
		String json = "";
		try {
			JSONObject jsonObject = jObject.getJSONObject("data");
			JSONArray musicArray = jsonObject.getJSONArray("result");
			List<String> musics = new ArrayList<String>();
			for (int i = 0; i < musicArray.length(); i++) {
				JSONObject object = musicArray.getJSONObject(i);
				String url = object.getString("downloadUrl");// 音乐地址
				String singer = "";
				if(object.has("singer")){
					singer = object.getString("singer");//歌手
				}
				String musicName = "";
				if(object.has("name")){
					musicName = object.getString("name");//歌曲
				}
				if (!TextUtils.isEmpty(url)) {
					//歌手+歌名 + 歌曲src
					musics.add(singer + DataConfig.MUSIC_SPLITE + musicName + DataConfig.MUSIC_SPLITE + url);
				}
			}

			int size = musics.size();
			if (musics != null && size > 0) {
				Random random = new Random();
				int randNum = random.nextInt(size);
				json = musics.get(randNum);
			}
		} catch (JSONException e) {
			Log.i(TAG, "getMusicData  JSONException");
		}
		return json;
	}
	
	//获取提醒
	private static String getRemindData(JSONObject jObject){
		String json = "";
		try {
			JSONObject jsonObject = jObject.getJSONObject("semantic");
			JSONObject object = jsonObject.getJSONObject("slots");
			String content = object.getString("content");// 做什么事
			JSONObject dataObject = object.getJSONObject("datetime");
			String time = dataObject.getString("time");// 时间
			String date = dataObject.getString("date");// 日期

			// 日期 + 时间 + 做什么事
			json = date + DataConfig.SCHEDULE_SPLITE + time + DataConfig.SCHEDULE_SPLITE + content;

		} catch (JSONException e) {
			Log.i(TAG, "getRemindData  JSONException");
		}
		return json;
	}
	
	//获取天气
	private static String getWeatherData(JSONObject jObject){
		String json = "";
		try {
			JSONObject object1 = jObject.getJSONObject("semantic");
			JSONObject object2 = object1.getJSONObject("slots");
			JSONObject object3 = object2.getJSONObject("datetime");
			String time = "";
			if(object3.has("dateOrig")){
				time = object3.getString("dateOrig");// 日期
			}else{
				time = "今天";
			}
			JSONObject object4 = object2.getJSONObject("location");
			String iflyCity = object4.getString("city");// 城市
			String iflyArea = "";
			if (object4.has("area")) {// 区域
				iflyArea = object4.getString("area");// 区域
			}

			JSONObject jsonObject = jObject.getJSONObject("data");
			JSONArray dataArray = jsonObject.getJSONArray("result");
			List<String> weathers = new ArrayList<String>();
			List<String> weathersUnKnow = new ArrayList<String>();
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject object = dataArray.getJSONObject(i);
				String airQuality = object.getString("airQuality");// 空气质量
				String wind = object.getString("wind");// 风向以及风力
				String weather = object.getString("weather");// 天气现象
				String tempRange = object.getString("tempRange");// 气温范围25℃~19℃
				String content = "";
				SharedPreferencesUtils share = SharedPreferencesUtils.getInstance();
				String currentCity = share.getString(SharedPreferencesKeys.CITY_KEY, "");
				String currentArea = share.getString(SharedPreferencesKeys.AREA_KEY, "");
				if (currentCity.contains(iflyCity)) {// 是当前城市
					if (!TextUtils.isEmpty(iflyArea)) {
						content = time + iflyCity + iflyArea;
					} else {
						content = time + iflyCity + currentArea;
					}
				} else {// 不是当前城市
					if (!TextUtils.isEmpty(iflyArea)) {
						content = time + iflyCity + iflyArea;
					} else {
						if(TextUtils.equals(iflyCity, "CURRENT_CITY")){
							DataConfig.isGetCity = true;
							String weatherContent = time + currentCity + currentArea + "的天气";
							BroadcastShare.askIfly(weatherContent);
							break;
						}else{
							content = time + iflyCity;
						}
						
					}
				}
//				content = content + "天气："+ weather+ ",空气质量："+ airQuality+ ",风力："+ wind+ ",气温："+ tempRange	+ ","+ VoiceControlUtils.getDressTips(weather,
//						ResultHandleUtils.getTemperature(tempRange));
				content = content + "天气："+ weather+ ",空气质量："+ airQuality+ ",风力："+ wind+ ",气温："+ tempRange	+ ",";

				if (!TextUtils.equals(airQuality, "未知")) {
					weathers.add(content);
				} else {
					weathersUnKnow.add(content);
				}
			}

			int size = weathers.size();
			if (weathers != null && size > 0) {
				Random random = new Random();
				int randNum = random.nextInt(size);
				json = weathers.get(randNum);
			} else if (weathersUnKnow != null && weathersUnKnow.size() > 0) {
				int sizes = weathersUnKnow.size();
				for (int i = 0; i < sizes; i++) {
					Random random = new Random();
					int randNum = random.nextInt(sizes);
					json = weathersUnKnow.get(randNum);
				}
			} else {
				json = "";
			}

		} catch (JSONException e) {
			Log.i(TAG, "getWeatherData  JSONException");
		}
		return json;
	}
	
	//获取打电话
	private static String getPhoneData(JSONObject jObject){
		String json = "";
		try {
			JSONObject jsonObject = jObject.getJSONObject("semantic");
			JSONObject object = jsonObject.getJSONObject("slots");
			String name = "";
			if (object.has("name")) {
				name = object.getString("name");// 拨打电话的人名
			} else if (object.has("code")) {
				name = object.getString("code");// 拨打电话的号码
			}
			json = name;
		} catch (JSONException e) {
			Log.i(TAG, "getPhoneData  JSONException");
		}
		return json;
	}
	
	//获取空气质量
	private static String getPm25Data(JSONObject jObject){
		String json = "";
		try {
			JSONObject json1 = jObject.getJSONObject("semantic");
			JSONObject json2 = json1.getJSONObject("slots");
			JSONObject json3 = json2.getJSONObject("location");
			String iflyCity = json3.getString("city");// 城市
			String iflyArea = "";
			if (json3.has("area")) {
				iflyArea = json3.getString("area");// 区域
			}

			JSONObject jsonObject = jObject.getJSONObject("data");
			JSONArray dataArray = jsonObject.getJSONArray("result");
			List<String> weathers = new ArrayList<String>();

			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject object = dataArray.getJSONObject(i);
				String pmValue = object.getString("pm25");// pm值
				String weather = object.getString("quality");// 空气质量
				String aqi = object.getString("aqi");// 空气质量指数

				String content = "";
				SharedPreferencesUtils share  =SharedPreferencesUtils.getInstance();
				
				if (share.getString(SharedPreferencesKeys.CITY_KEY, "").contains(iflyCity)) {// 是当前城市
					if (!TextUtils.isEmpty(iflyArea)) {// 有返回区
						content = iflyCity + iflyArea;
					} else {// 没有返回区
						content = iflyCity + share.getString(SharedPreferencesKeys.AREA_KEY, "");
					}
				} else {// 不是当前城市
					if (!TextUtils.isEmpty(iflyArea)) {// 有返回区
						content = iflyCity + iflyArea;
					} else {// 没有返回区
						content = iflyCity;
					}
				}

				content = content + "pm值：" + pmValue + ",空气质量指数：" + aqi + ",空气质量：" + weather;
				weathers.add(content);
			}

			int size = weathers.size();
			if (weathers != null && size > 0) {
				Random random = new Random();
				int randNum = random.nextInt(size);
				json = weathers.get(randNum);
			} else {
				json = "";
			}
		} catch (JSONException e) {
			Log.i(TAG, "getPm25Data  JSONException");
		}
		return json;
	}

	//获取剧本
	public static void parseScript(String jsonContent,ScriptImpl callBack) {
		if(!TextUtils.isEmpty(jsonContent)){
			try {
				JSONTokener tokener = new JSONTokener(jsonContent);
				JSONObject json = new JSONObject(tokener);
				String scriptContent = json.getString("scriptContent");
				ScriptInfo scriptInfo = new ScriptInfo();
				scriptInfo.setScriptContent(scriptContent);
				List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
				JSONArray array = json.getJSONArray("script");
				infos = getInfos(array);

				callBack.getScribt(scriptInfo,infos);
			} catch (Exception e) {
				Log.i(TAG, "parseScript  JSONException");
				callBack.getScribt(null,null);
			}

		}
	}

	//增加APP发过来的录制动作
	public static void parseAppRecordAction(String jsonContent,ScriptImpl callBack) {
		if(!TextUtils.isEmpty(jsonContent)){
			try {
				JSONTokener tokener = new JSONTokener(jsonContent);
				JSONObject json = new JSONObject(tokener);
				String scriptContent = json.getString("actionName");
				ScriptInfo scriptInfo = new ScriptInfo();
				scriptInfo.setScriptContent(scriptContent);
				List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
				JSONArray array = json.getJSONArray("actions");
				infos = getInfos(array);

				callBack.getScribt(scriptInfo,infos);
			} catch (Exception e) {
				Log.i(TAG, "addAppRecordScript  JSONException");
				callBack.getScribt(null,null);
			}

		}
	}

	//增加APP发过来的音乐编舞
	public static void parseAppRecordMusic(String jsonContent,ScriptImpl callBack) {
		if(!TextUtils.isEmpty(jsonContent)){
			try {
				JSONTokener tokener = new JSONTokener(jsonContent);
				JSONObject json = new JSONObject(tokener);
				String scriptContent = json.getString("songName");
				ScriptInfo scriptInfo = new ScriptInfo();
				scriptInfo.setScriptContent(scriptContent);
				List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
				JSONArray array = json.getJSONArray("editDance");
				infos = getInfos(array);

				callBack.getScribt(scriptInfo,infos);
			} catch (Exception e) {
				Log.i(TAG, "addAppRecordScript  JSONException");
				callBack.getScribt(null,null);
			}

		}
	}

	private static List<ScriptActionInfo> getInfos(JSONArray array) {
		List<ScriptActionInfo> infos = new ArrayList<ScriptActionInfo>();
		try {
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				ScriptActionInfo info = new ScriptActionInfo();
				String actionType = object.getString("actionType");
				if (!TextUtils.isEmpty(actionType)) {
					if (TextUtils.isDigitsOnly(actionType)) {
						info.setActionType(Integer.parseInt(actionType));
					}
				}
				if (object.has("content")) {
					String content = object.getString("content");
					if (!TextUtils.isEmpty(content)) {
						info.setContent(content);
					}
				}
				if (object.has("spareType")) {
					String spareType = object.getString("spareType");
					if(!TextUtils.isEmpty(spareType)){
						if (TextUtils.isDigitsOnly(spareType)) {
							info.setDirection(Integer.parseInt(spareType));
						}
					}
				}
				if (object.has("spareContent")) {
					String spareContent = object.getString("spareContent");
					if (!TextUtils.isEmpty(spareContent)) {
						info.setSpareContent(spareContent);
					}
				}
				if (object.has("spareContent2")) {
					String spareContent2 = object.getString("spareContent2");
					if (!TextUtils.isEmpty(spareContent2)) {
						info.setRemarks(spareContent2);
					}
				}
				infos.add(info);
			}
		} catch (Exception e) {
			Log.i(TAG, "getInfos  JSONException");
		}
		return infos;
	}

	//app发来的提醒
	public static RemindInfo parseAppRemind(String jsonContent) {
		RemindInfo info = new RemindInfo();
		if(!TextUtils.isEmpty(jsonContent)){
			try {
				JSONTokener tokener = new JSONTokener(jsonContent);
				JSONObject json = new JSONObject(tokener);
				info.setOriginalAlarmTime(json.getString("remindTime"));
				info.setContent(json.getString("remindContent"));
				info.setRemindMen(json.getString("remindMen"));
				if(json.has("requireAnswer")){
					String requireAnswer = json.getString("requireAnswer");
					if(!TextUtils.isEmpty(requireAnswer)){
						info.setRequireAnswer(requireAnswer);
					}
				}
				if(json.has("spareContent")){
					String spareContent = json.getString("spareContent");
					if(!TextUtils.isEmpty(spareContent)){
						info.setSpareContent(spareContent);
					}
				}
				if(json.has("spareType")){
					String spareType = json.getString("spareType");
					if(!TextUtils.isEmpty(spareType)){
						info.setSpareType(Integer.parseInt(spareType));
					}
				}
				return info;
			} catch (Exception e) {
				Log.i(TAG, "parseAppRemind  JSONException");
			}
		}
		return info;
	}

}

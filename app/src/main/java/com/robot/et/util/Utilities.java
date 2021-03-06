package com.robot.et.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.robot.et.app.CustomApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;

/**
 * @author Tony 2015-10-08
 */
public class Utilities {

	/**
	 * 公用提示框
	 * 
	 * @param message
	 * @param context
	 */
	public static void showToast(CharSequence message, Context context) {
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context.getApplicationContext(), message,
				duration);
		toast.show();
	}

	@SuppressLint("SimpleDateFormat")
	public static String setThroughTime() {
		Date date = new Date();
		SimpleDateFormat sdformat = new SimpleDateFormat(("yyyy-MM-dd"));// 24小时制
		String LgTime = sdformat.format(date);
		return LgTime;
	}

	public static String inputStream2String(InputStream in) throws IOException {
		BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = inReader.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}

	public static boolean checkSdCardIsExist() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getSdCardPath() {
		// In this method,Lenovo pad return StorageDirectory,not "/mnt/sdcard1"
		String sdPath = null;
		if (checkSdCardIsExist()) {
			sdPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}
		return sdPath.toString();
	}

	/**
	 * 判断是否是Integer类型
	 * 
	 * @author daichangfu
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		if (str != null && !"".equals(str.trim())) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(str);
			Long number = 0l;
			if (isNum.matches()) {
				number = Long.parseLong(str);
			} else {
				return false;
			}
			if (number > 2147483647) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	// 手机号码正则表达式
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(17[6-8])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 密码强度正则表达式
	public static boolean isPasswordStronger(String password) {
		String string = "^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])|(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9])|(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])|(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9])).{6,}|(?:(?=.*[A-Z])(?=.*[a-z])|(?=.*[A-Z])(?=.*[0-9])|(?=.*[A-Z])(?=.*[^A-Za-z0-9])|(?=.*[a-z])(?=.*[0-9])|(?=.*[a-z])(?=.*[^A-Za-z0-9])|(?=.*[0-9])(?=.*[^A-Za-z0-9])|).{8,}";
		Pattern p = Pattern.compile(string);
		Matcher m = p.matcher(password);
		return m.matches();
	}
	//判断是否为今天
	@SuppressLint("SimpleDateFormat")
	public static boolean isToday(Calendar cal) {
		boolean ret = false;
		Calendar tmp = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String s1 = format.format(cal.getTime());
		String s2 = format.format(tmp.getTime());
		if (s1.equals(s2)) {
			ret = true;
		}
		return ret;
	}
	@SuppressLint("SimpleDateFormat")
	public static boolean isToday(String s1) {
		boolean ret = false;
		Calendar tmp = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String s2 = format.format(tmp.getTime());
		if (s1.equals(s2)) {
			ret = true;
		}
		return ret;
	}

	public static String getMD5(String info)
	{
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(info.getBytes("UTF-8"));
			byte[] encryption = md5.digest();

			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < encryption.length; i++)
			{
				if (Integer.toHexString(0xff & encryption[i]).length() == 1)
				{
					strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
				}
				else
				{
					strBuf.append(Integer.toHexString(0xff & encryption[i]));
				}
			}

			return strBuf.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			return "";
		}
		catch (UnsupportedEncodingException e)
		{
			return "";
		}
	}
	
	/**
	 * 读取asset目录下文件。
	 * @return content
	 */
	public static String readFile(String file, String code) {
		int len = 0;
		byte[] buf = null;
		String result = "";
		try {
			InputStream in = CustomApplication.getInstance().getApplicationContext().getAssets().open(file);
			len = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);
			result = new String(buf, code);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 读取asset目录下文件
	 * @return 二进制文件数据
	 */
	public static byte[] readFile(String filename) {
		try {
			InputStream ins = CustomApplication.getInstance().getApplicationContext().getAssets().open(filename);
			byte[] data = new byte[ins.available()];
			ins.read(data);
			ins.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
}

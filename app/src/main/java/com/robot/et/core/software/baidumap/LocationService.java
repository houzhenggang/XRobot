package com.robot.et.core.software.baidumap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.robot.et.config.BroadcastAction;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

//百度地图定位
public class LocationService extends Service {

	private Location mLocation;
	private SharedPreferencesUtils sharedUtils;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mLocation = new Location(this);
		SDKInitializer.initialize(getApplicationContext());  
		mLocation.registerListener(mListener);
		mLocation.setLocationOption(mLocation.getDefaultLocationClientOption());
		sharedUtils = SharedPreferencesUtils.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mLocation.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private BDLocationListener mListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				String city = location.getCity();
				String area = location.getDistrict();
				if(!TextUtils.isEmpty(city) && !TextUtils.isEmpty(area)){
					sharedUtils.putString(SharedPreferencesKeys.CITY_KEY, city);
					sharedUtils.putString(SharedPreferencesKeys.AREA_KEY, area);
					sharedUtils.commitValue();
					//停止定位服务（百度地图在不断的定位）
					mLocation.unregisterListener(mListener);
					mLocation.stop();
					
					Intent intent = new Intent();
					intent.setAction(BroadcastAction.ACTION_GET_LOCATION);
					sendBroadcast(intent);
					
				}else{
					//没有定位成功继续定位
					mLocation.start();
				}
			}else{
				//没有定位成功继续定位
				mLocation.start();
			}
		}

	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销掉监听
		mLocation.unregisterListener(mListener);
		//停止定位服务
		mLocation.stop();
		
	}
}

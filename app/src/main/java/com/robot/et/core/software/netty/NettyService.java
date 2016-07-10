package com.robot.et.core.software.netty;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.robot.et.config.BroadcastAction;
import com.robot.et.config.DataConfig;
import com.robot.et.config.UrlConfig;
import com.robot.et.debug.Logger;
import com.robot.et.entity.CommandMsg;
import com.robot.et.util.DataManager;
import com.robot.et.util.DeviceUuidFactory;
import com.robot.et.util.SharedPreferencesKeys;
import com.robot.et.util.SharedPreferencesUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyService extends Service {
	
	private NioEventLoopGroup group;
	private CommandMsg commandMsg;
	private SocketChannel socketChannel;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i("NettyService  onCreate()");

		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastAction.ACTION_OPEN_NETTY);
		registerReceiver(receiver, filter);

		String robotNum = SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "");
		if (TextUtils.isEmpty(robotNum)) {
			String deviceId = new DeviceUuidFactory(this).getDeviceUuid();
			Log.i("netty", "deviceId===" + deviceId);
			DataManager.getRobotInfo(UrlConfig.GET_ROBOT_INFO_BY_DEVICEID, deviceId);
		} else {
			Intent intent = new Intent();
			intent.setAction(BroadcastAction.ACTION_OPEN_NETTY);
			intent.putExtra("robotNum", robotNum);
			sendBroadcast(intent);
		}

	}

	//连接netty
	private void connected() {
		if (socketChannel == null) {
			socketChannel = getSocketChannel();
			return;
		}

		socketChannel.writeAndFlush(JSON.toJSON(commandMsg).toString() + "\r\n");
		Log.i("netty", "socketChannel != null NettyService connect server success");
	}
	
	private SocketChannel getSocketChannel(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					group = new NioEventLoopGroup();
					Bootstrap bootstrap = new Bootstrap();
					bootstrap.channel(NioSocketChannel.class);
					bootstrap.option(ChannelOption.SO_RCVBUF, 10 * 1024 * 1024);
					bootstrap.option(ChannelOption.SO_SNDBUF, 1024 * 1024);
					bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
					bootstrap.group(group);
					bootstrap.remoteAddress(DataConfig.HOST, DataConfig.PORT);
					bootstrap.handler(new NettyClientInitializer());
					ChannelFuture future = bootstrap.connect(DataConfig.HOST, DataConfig.PORT).sync();
					if (future.isSuccess()) {
						socketChannel = (SocketChannel) future.channel();
						Log.i("netty", "NettyService connect server success");
						socketChannel.writeAndFlush(JSON.toJSON(commandMsg).toString() + "\r\n");
					}
				} catch (InterruptedException e) {
					Log.i("netty", "NettyService InterruptedException");
				}
			}
		}).start();
		return socketChannel;
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BroadcastAction.ACTION_OPEN_NETTY)){//打开netty
				Log.i("netty", "打开netty");
				String robotNum = intent.getStringExtra("robotNum");
				Log.i("netty", "robotNum===" + robotNum);
				commandMsg = new CommandMsg(robotNum, "", "");
				connected();

			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		if(group!=null){
			group.shutdownGracefully();
		}
	}
	
}

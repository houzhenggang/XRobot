package com.robot.et.core.software.netty;

import android.text.TextUtils;
import android.util.Log;

import com.robot.et.util.NettyResultHandle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
		Log.i("netty", "接受到netty服务器发来的消息msg===" + msg);

		String message = (String)msg;
		if(!TextUtils.isEmpty(message)){
			NettyResultHandle.setPushResult(message);
		}

	}

}

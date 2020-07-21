package com.agree.tlqAgent.netty.server;

import com.agree.tlqAgent.netty.client.NettyClient;
import com.agree.tlqAgent.netty.server.request.constants.RequestMessageTypes;
import io.netty.channel.ChannelFuture;

public class NettyServerProcessorAfterStarting {
	private ChannelFuture channelFuture;

	public NettyServerProcessorAfterStarting(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
	}

	public void process() {
		try {
			new NettyClient().sendMsgToServer(RequestMessageTypes.START_SCHEDULE_MSG);
		} catch (Exception e) {
		}
	}
}

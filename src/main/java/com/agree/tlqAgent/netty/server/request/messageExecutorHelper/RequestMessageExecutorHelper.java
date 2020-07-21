package com.agree.tlqAgent.netty.server.request.messageExecutorHelper;

import com.agree.tlqAgent.netty.server.request.messageExecutor.IRequestMessageExecutor;
import io.netty.channel.ChannelHandlerContext;

public class RequestMessageExecutorHelper {

	public void getExecutorAndExecute(ChannelHandlerContext ctx, String msg) {
		IRequestMessageExecutor executor = new RequestMessageStrategy().getExecutor(msg);
		executor.execute(ctx, msg);
	}
}

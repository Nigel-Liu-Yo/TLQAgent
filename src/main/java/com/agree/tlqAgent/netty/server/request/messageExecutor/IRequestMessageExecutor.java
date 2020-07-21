package com.agree.tlqAgent.netty.server.request.messageExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public interface IRequestMessageExecutor {
	Logger logger = LoggerFactory.getLogger(IRequestMessageExecutor.class);

	void execute(ChannelHandlerContext ctx, String msg);
}

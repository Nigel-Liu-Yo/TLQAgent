package com.agree.tlqAgent.netty.server;

import com.agree.tlqAgent.message.MessageConverter;
import com.agree.tlqAgent.netty.server.request.messageExecutorHelper.RequestMessageExecutorHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {
	private static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		logger.info("netty server channelRead："+msg);
		new RequestMessageExecutorHelper().getExecutorAndExecute(ctx, msg);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("client's address is :" + ctx.channel().remoteAddress());
		super.channelActive(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("Unexpected exception from downstream." + cause.getMessage());
		ctx.writeAndFlush(new MessageConverter(null).getFailReturnMessage(cause.getMessage()));
		ctx.close();
	}

}
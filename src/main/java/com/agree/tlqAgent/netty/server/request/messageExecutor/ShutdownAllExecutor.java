package com.agree.tlqAgent.netty.server.request.messageExecutor;

import com.agree.tlqAgent.annotations.RequestMessageExecutor;
import com.agree.tlqAgent.entrance.TLQAgent;
import com.agree.tlqAgent.netty.server.request.constants.RequestMessageTypes;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

@RequestMessageExecutor(RequestMessageTypes.SHUTDOWN_ALL_MSG)
public class ShutdownAllExecutor implements IRequestMessageExecutor {

    @Override
    public void execute(ChannelHandlerContext ctx, String msg) {
        ctx.writeAndFlush("shutdown all command received\n");
        TLQAgent.shutDownAll();
        ctx.writeAndFlush("shutdown all completed").addListener(ChannelFutureListener.CLOSE);
    }

}

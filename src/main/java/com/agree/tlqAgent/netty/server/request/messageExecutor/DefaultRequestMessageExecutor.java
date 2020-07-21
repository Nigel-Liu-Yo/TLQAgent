package com.agree.tlqAgent.netty.server.request.messageExecutor;

import com.agree.tlqAgent.annotations.RequestMessageExecutor;
import com.agree.tlqAgent.message.MessageConverter;
import com.agree.tlqAgent.tlqCli.sender.TLQSender;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

@RequestMessageExecutor
public class DefaultRequestMessageExecutor implements IRequestMessageExecutor {

    @Override
    public void execute(ChannelHandlerContext ctx, String msg) {
        MessageConverter messageConverter = new MessageConverter(msg);
        String returnMessage = "";
        try {
            new TLQSender().sendMessage(messageConverter.getMessageWithHeader());
            returnMessage = messageConverter.getSuccessReturnMessage();
        } catch (Exception e) {
            logger.error("send message to TLQ fail..." + e);
            returnMessage = messageConverter.getFailReturnMessage(e.getMessage());
        } finally {
            ctx.writeAndFlush(returnMessage).addListener(ChannelFutureListener.CLOSE);
        }
    }
}

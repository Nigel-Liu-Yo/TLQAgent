package com.agree.tlqAgent.netty.server.request.messageExecutor;

import com.agree.tlqAgent.annotations.RequestMessageExecutor;
import com.agree.tlqAgent.message.MessageConverter;
import com.agree.tlqAgent.netty.client.NettyClient;
import com.agree.tlqAgent.netty.server.request.constants.RequestMessageTypes;
import com.agree.tlqAgent.tlqCli.receiver.TLQReceiver;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

@RequestMessageExecutor(RequestMessageTypes.START_SCHEDULE_MSG)
public class StartScheduleExecutor implements IRequestMessageExecutor {
	volatile boolean StartScheduleIsStarted = false;

	@Override
	public void execute(ChannelHandlerContext ctx, String msg) {
		ctx.channel().eventLoop().execute(() -> {
			if (!StartScheduleIsStarted) {
				StartScheduleIsStarted = true;
				//TODO
				if (true) {
					logger.info("StartSchedule");
					return;
				}
				boolean getMessageSlowly = false;
				TLQReceiver rcver=new TLQReceiver();
				while (true) {
					try {
						if (getMessageSlowly) {
							TimeUnit.SECONDS.sleep(3);
						}
						String replyMsg = rcver.getMsgFromQueue();
						if (!isNull(replyMsg)) {
							getMessageSlowly = false;
							replyMsg = new MessageConverter(replyMsg).getMessageWithHeader();
							new NettyClient().sendMsgToAFE(replyMsg);
						} else {
							getMessageSlowly = true;
						}
					} catch (Exception e) {
						getMessageSlowly = true;
						logger.error("deal message from queue error", e);
					}
				}
			}
		});
		ctx.writeAndFlush("start-schedule command received").addListener(ChannelFutureListener.CLOSE);
	}

	private boolean isNull(String s) {
		return s != null && s.length() > 0;
	}
}

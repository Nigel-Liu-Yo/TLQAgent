package com.agree.tlqAgent.entrance;

import com.agree.tlqAgent.netty.client.NettyClient;
import com.agree.tlqAgent.netty.server.NettyServer;
import com.agree.tlqAgent.netty.server.request.constants.RequestMessageTypes;
import com.agree.tlqAgent.tlqCli.receiver.TLQReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TLQAgent {

	private static Logger logger = LoggerFactory.getLogger(TLQAgent.class);

	public static void main(String[] args) throws Exception {
		//reserved
		if (args != null && args.length > 0 && "shutdown".equals(args[0])) {
			NettyClient.startClient();
			new NettyClient().sendMsgToServer(RequestMessageTypes.SHUTDOWN_ALL_MSG);
		}

		startUpAll();
	}

	private static void startUpAll() {
		try {
			//TODO
//			TLQReceiver.startTLQReceiver();
			NettyClient.startClient();
			NettyServer.startServer();
		} catch (Exception e) {
			logger.error("TLQAgent starts unsuccessfully：" + e);
			shutDownAll();
		}
	}

	public static void shutDownAll() {
		try {
			TLQReceiver.shutdown();
			NettyClient.shutDown();
			NettyServer.shutDown();
		} catch (Exception e) {
			logger.error("TLQAgent shutdown unsuccessfully：" + e);
		}
	}
}

package com.agree.tlqAgent.tlqCli.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agree.tlqAgent.utils.ConfigUtils;
import com.tongtech.tlq.base.TlqConnContext;
import com.tongtech.tlq.base.TlqConnection;
import com.tongtech.tlq.base.TlqException;
import com.tongtech.tlq.base.TlqMessage;
import com.tongtech.tlq.base.TlqMsgOpt;
import com.tongtech.tlq.base.TlqQCU;

public class TLQSender {
	static Logger logger = LoggerFactory.getLogger(TLQSender.class);
	private static String SEND_QCU_NAME;
	private static String SEND_QUE_NAME;
	private static int SEND_BROKERID;
	private static String SEND_IP;

	private static int SEND_PORT;

	static int id = 0;

	volatile boolean INIT_FLAG = false;

	public void sendMessage(String message) throws TlqException {
		initConfig();
		logger.info("ready to send message to tlq :\n"+message);
		TlqQCU tlqQcu = null;
		TlqConnection tlqConnection = null;
		try {
			TlqConnContext tlqConnContext = new TlqConnContext();
			tlqConnContext.BrokerId = SEND_BROKERID; // 根据 tlqcli.conf中的配置
			tlqConnContext.HostName = SEND_IP;
			tlqConnContext.ListenPort = SEND_PORT;
			tlqConnection = new TlqConnection(tlqConnContext);
			tlqQcu = tlqConnection.openQCU(SEND_QCU_NAME);
			TlqMessage msgInfo = new TlqMessage();
			TlqMsgOpt msgOpt = new TlqMsgOpt();
			msgInfo.MsgType = TlqMessage.BUF_MSG; // 消息类型
			msgInfo.MsgSize = message.getBytes().length; // 消息大小
			byte[] msgContent = message.getBytes(); // 消息内容
			msgInfo.setMsgData(msgContent);

			msgInfo.Persistence = TlqMessage.TLQPER_Y; // 持久性
			msgInfo.Priority = TlqMessage.TLQPRI_NORMAL; // 优先级
			msgInfo.Expiry = 1000; // 生命周期
			msgOpt.QueName = SEND_QUE_NAME; // 队列名

			tlqQcu.putMessage(msgInfo, msgOpt);

			logger.info("-----------send msg over!!-----------");

		}  finally {
			try {
				tlqQcu.close();
				tlqConnection.close();
			} catch (TlqException e) {
				logger.error(e.getMessage(),e);
			}
		}

	}

	private void initConfig() {
		if (!INIT_FLAG) {
			synchronized (this) {
				if (!INIT_FLAG) {
					try {
						SEND_QCU_NAME = ConfigUtils.INSTANCE.getConfig("SEND_QCU_NAME", "qcu1");
						SEND_QUE_NAME = ConfigUtils.INSTANCE.getConfig("SEND_QUE_NAME", "sq");
						SEND_IP = ConfigUtils.INSTANCE.getConfig("SEND_IP", "127.0.0.1");
						SEND_PORT = Integer.parseInt(ConfigUtils.INSTANCE.getConfig("RCV_PORT", "10261"));
						SEND_BROKERID = Integer.parseInt(ConfigUtils.INSTANCE.getConfig("SEND_BROKERID", "111"));
						INIT_FLAG = true;
					} catch (Exception e) {
						logger.error("parse config error..." + e);
					}
				}
			}
		}
	}
}

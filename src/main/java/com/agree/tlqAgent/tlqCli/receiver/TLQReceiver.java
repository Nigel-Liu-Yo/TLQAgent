package com.agree.tlqAgent.tlqCli.receiver;

import com.agree.tlqAgent.utils.ConfigUtils;
import com.tongtech.tlq.base.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class TLQReceiver {

	static Logger logger = LoggerFactory.getLogger(TLQReceiver.class);

	private static String RCV_QCU_NAME;
	private static String RCV_QUE_NAME;
	private static int RCV_WAIT_INTERVAL;

	private static TlqConnection RCV_TLQ_CONNECTION;
	private static TlqConnContext RCV_TLQ_CONNCONTEXT;
	private static TlqQCU RCV_TLQ_QCU;

	public static void startTLQReceiver() throws Exception {
		RCV_QCU_NAME = ConfigUtils.INSTANCE.getConfig("RCV_QCU_NAME", "qcu1");
		RCV_QUE_NAME = ConfigUtils.INSTANCE.getConfig("RCV_QUE_NAME", "lq");
		RCV_WAIT_INTERVAL = Integer.parseInt(ConfigUtils.INSTANCE.getConfig("RCV_WAIT_INTERVAL", "10"));

		RCV_TLQ_CONNCONTEXT = new TlqConnContext();
		RCV_TLQ_CONNCONTEXT.BrokerId = Integer.parseInt(ConfigUtils.INSTANCE.getConfig("RCV_BROKER_ID", "111"));
		RCV_TLQ_CONNCONTEXT.HostName = ConfigUtils.INSTANCE.getConfig("RCV_HOSTNAME", "127.0.0.1");
		RCV_TLQ_CONNCONTEXT.ListenPort = Integer.parseInt(ConfigUtils.INSTANCE.getConfig("RCV_PORT", "10261"));

		RCV_TLQ_CONNECTION = new TlqConnection(RCV_TLQ_CONNCONTEXT, 3, 10);
		RCV_TLQ_QCU = RCV_TLQ_CONNECTION.openQCU(RCV_QCU_NAME);
	}

	public static void shutdown() {
		try {
			if (RCV_TLQ_QCU != null) {
				RCV_TLQ_QCU.close();
			}
			if (RCV_TLQ_CONNECTION != null) {
				RCV_TLQ_CONNECTION.close();
			}
			logger.info("TLQReceiver has been shutted down successfully...");
		} catch (TlqException e) {
			logger.info("TLQReceiver shut down fail...", e);
		}
	}

	public String getMsgFromQueue() throws TlqException, UnsupportedEncodingException {
		TlqMessage msgInfo = new TlqMessage();
		TlqMsgOpt msgOpt = new TlqMsgOpt();

		msgOpt.QueName = RCV_QUE_NAME;
		msgOpt.WaitInterval = RCV_WAIT_INTERVAL;
		msgOpt.OperateType = TlqMsgOpt.TLQOT_GET;

		RCV_TLQ_QCU.getMessage(msgInfo, msgOpt);

		String msg = "";
		if (msgInfo.getMsgData() != null && msgInfo.getMsgData().length > 0) {
			msg = new String(msgInfo.getMsgData(), "gbk");
		}
		logger.info("Received a Buffer Msg:\n" + msg);
		logger.info("msgInfo.MsgId=" + new String(msgInfo.MsgId));
		logger.info("msgInfo.MsgSize=" + msgInfo.MsgSize);

		if (msgOpt.AckMode == TlqMsgOpt.TLQACK_USER) {
			int acktype = TlqMsgOpt.TLQACK_COMMIT;
			RCV_TLQ_QCU.ackMessage(msgInfo, msgOpt, acktype);
		}

		return msg;
	}
}

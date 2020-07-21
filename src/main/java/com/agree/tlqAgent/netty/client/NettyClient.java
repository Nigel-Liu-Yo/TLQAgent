package com.agree.tlqAgent.netty.client;

import com.agree.tlqAgent.netty.server.constants.NettyServerConstants;
import com.agree.tlqAgent.utils.ConfigUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class NettyClient {
    private static EventLoopGroup clientGroup;
    private static Channel ch;
    private static Bootstrap client;

    private static final String AFE_IP = "AFE_IP";
    private static final String AFE_PORT = "AFE_PORT";

    private static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public static void startClient() {
        logger.info("Netty-client starting......");
        client = new Bootstrap();
        clientGroup = new NioEventLoopGroup();
        client.group(clientGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ChannelPipeline ph = ch.pipeline();
                ph.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 8, 0, 8));
                ph.addLast(new LengthFieldPrepender(8));
                ph.addLast(new StringDecoder(Charset.forName("gbk")));
                ph.addLast(new StringEncoder(Charset.forName("gbk")));
                ph.addLast(new NettyClientHandler()); //real client handler
            }
        });
        logger.info("Netty-client has been started...");
    }

    public static void shutDown() {
        if (clientGroup != null && !clientGroup.isShutdown()) {
            clientGroup.shutdownGracefully();
            logger.info("Netty-client has been shutted down");
        }
    }

    public void sendMsgToAFE(String msg) throws Exception {
        String afe_ip = ConfigUtils.INSTANCE.getConfig(AFE_IP, null);
        int port = Integer.parseInt(ConfigUtils.INSTANCE.getConfig(AFE_PORT, null));
        sendMsg(msg, afe_ip, port);
    }

    public void sendMsgToServer(String msg) throws Exception {
        sendMsg(msg, "127.0.0.1", NettyServerConstants.SERVER_PORT);
    }

    public void sendMsg(String msg, String ip, int port) throws Exception {
        try {
            ch = client.connect(ip, port).sync().channel();
            ch.writeAndFlush(msg);
            logger.info("netty client send message:" + msg);
        } finally {
            if (ch != null) {
                ch.closeFuture();
            }
        }
    }
}

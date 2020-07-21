package com.agree.tlqAgent.netty.server;

import com.agree.tlqAgent.netty.server.constants.NettyServerConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class NettyServer {
    static EventLoopGroup bossGroup = null;
    static EventLoopGroup workerGroup = null;
    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public static void startServer() throws InterruptedException {
        logger.info("Netty-server starting......");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .localAddress(NettyServerConstants.SERVER_PORT).option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        ChannelPipeline ph = channel.pipeline();
                        ph.addLast("framer", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 8, 0, 8));
                        ph.addLast("outframer", new LengthFieldPrepender(8));
                        ph.addLast("decoder", new StringDecoder(Charset.forName("gbk")));
                        ph.addLast("encoder", new StringEncoder(Charset.forName("gbk")));
                        ph.addLast("handler", new NettyServerHandler());// real server handler
                    }
                });
        ChannelFuture bindFuture = server.bind().sync();
        bindFuture.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                logger.info("Netty-server has been started , listening@" + channelFuture.channel().localAddress());
                new NettyServerProcessorAfterStarting(channelFuture).process();
            } else {
                logger.error("Netty-server started unsuccessfully \n" + channelFuture.cause());
            }
        });
        ChannelFuture closeFuture = bindFuture.channel().closeFuture().sync();
        closeFuture.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                logger.info("Netty-server has been shutdown," + channelFuture.channel().localAddress());
            } else {
                logger.error("Netty-server shutdown unsuccessfully " + channelFuture.cause());
            }
        });
    }

    public static void shutDown() {
        try {
            if ((bossGroup != null) && !bossGroup.isShutdown())
                bossGroup.shutdownGracefully();
            if (workerGroup != null & !workerGroup.isShutdown())
                workerGroup.shutdownGracefully();
        } catch (Exception e) {
            logger.error("TLQAgent failed to shutdown：", e);
        }
    }
}

package com.cloud.storage.server;

import com.cloud.storage.common.Settings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.logging.Level;

public class ServerMainClass {
    private int PORT;
    private static final int MAX_OBJ_SIZE = 1024 * 1024 * 100; // 100 mb

    public ServerMainClass() {
    }

    public void run() {
        Settings.ServerSettings settings = Settings.getServerSettings();
        PORT = Integer.parseInt(settings.port);
        if (PORT != 0) {
            EventLoopGroup mainGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(mainGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(
                                        new ObjectDecoder(MAX_OBJ_SIZE, ClassResolvers.cacheDisabled(null)),
                                        new ObjectEncoder(),
                                        new CloudServerHandler()

                                );
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                ChannelFuture future = b.bind(PORT).sync();
                future.channel().closeFuture().sync();

            } catch (Exception e) {
                Log.loggingEvent(Level.INFO, "ServerMainClass", e.getMessage());
            } finally {
                mainGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }else{
            Log.loggingEvent(Level.INFO, "ServerMainClass", "Заданы не все настройки сервера");
        }
    }
    public static void main(String[] args) {
        try {
            new ServerMainClass().run();
        } catch (Exception e) {
            Log.loggingEvent(Level.INFO,"ServerMainClass",e.getMessage());
        }
    }
}

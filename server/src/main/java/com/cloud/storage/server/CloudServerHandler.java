package com.cloud.storage.server;

import com.cloud.storage.common.Commands;
import com.cloud.storage.common.Message;
import com.cloud.storage.common.Messages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.logging.Level;


public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.loggingEvent(Level.INFO,this.getClass().getName(),"Client connected...");
        System.out.println("Client connected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null)
                return;
            System.out.println(msg.getClass());
            if (msg instanceof Message) {
                ctx.write(comandExecutor((Message) msg));
            } else {
                Log.loggingEvent(Level.INFO,this.getClass().getName(),"Server received wrong object!");
                return;
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public Message testConnection(Message msg){
        return Messages.okMessage("Соединение установлено");
    }

    public Message comandExecutor(Message msg) {
        switch ((Commands) msg.command) {
            case LOGIN:
                return UsersUtils.autorization(msg);
            case EXIT:
                return UsersUtils.logout(msg);
            case REGISTRATION:
                return UsersUtils.registration(msg);
            case UPLOAD:
                return FileUtils.uploadFile(msg);
            case DELETE:
                return FileUtils.deleteFile(msg);
            case DOWNLOAD:
                return FileUtils.downloadFile(msg);
            case RENAME:
                return FileUtils.renameFile(msg);
            case REFRESH:
                return FileUtils.refresh(msg);
            case TEST:
                return testConnection(msg);
            default:
                return null;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //ctx.flush();
        ctx.close();
    }
}

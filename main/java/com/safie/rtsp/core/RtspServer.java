package com.safie.rtsp.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpMessage;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.safie.rtp.session.*;

// サーバの概念クラス
// 以下の責務を負う
// ・リクエスト通知を受け取り、レスポンスを返す(レスポンスの組み立てなどは責任外）
// ・サーバーの基本情報を保持する

public class RtspServer {
    private static Logger logger = LogManager.getLogger(RtspServer.class);
    private String ip;
    private int port;
    private RtspRequestHandler handler;
    private RtspRequestReceiver receiver;
//    private RtspParticipantDatabase database;

    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int BIZTHREADSIZE = 4;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

    public RtspServer(String ip, int port, RtpSession session, RtcpSession rtcpSession) {
        this.ip = ip;
        this.port = port;
        this.handler = new RtspRequestHandler(ip, port, session, rtcpSession);
        
        this.receiver = new RtspRequestReceiver(){
            @Override
            public void requestReceived(HttpRequest request, ChannelHandlerContext ctx){
                for (HttpMessage msg : handler.handleRtspRequest(request)){
                    ctx.writeAndFlush(msg);
                }
            }
        };
    }

    // Nettyの実装。childHandlerに独自のイニシャライザを設定。他はいつも通り。
    public void run() {
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup);
            server.channel(NioServerSocketChannel.class);
            server.childHandler(new RtspServerInitializer(this.receiver).get());
            server.bind(port).sync();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void stop() {

    }

    public String getAddress() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }
}

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

// RTSPの中核をなすクラス
// レシーバーやハンドラなどのコントローラを持つ。

public class RtspServer {
    private static Logger logger = LogManager.getLogger(RtspServer.class);

    private final RtspConfig config;
    private final RtspRequestHandler handler;
    private final RtspRequestReceiver receiver;
    private final RtspParticipantDatabase database;

    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int BIZTHREADSIZE = 4;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);

    public RtspServer(RtspConfig config) {
        this.config = config;
        
        this.receiver = new RtspRequestReceiver(){
            @Override
            public void requestReceived(HttpRequest request, ChannelHandlerContext ctx){
                handler.receiveRequest(request, (response) ->{
                    ctx.writeAndFlush(response);
                });
            }
        };

        this.handler = new RtspRequestHandler(){
            @Override
            public RtspSession initSession(int sessionId){
                RtspSession session = new RtspSession(sessionId, config);
                database.add(session);
                return session;
            }

            @Override
            public RtspSession getSession(int id){
                return database.get(id);
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

    // TODO ------------------------------
    public void stop() {

    }

    public String getAddress() {
        return this.config.ip;
    }

    public int getPort() {
        return this.config.port;
    }
}

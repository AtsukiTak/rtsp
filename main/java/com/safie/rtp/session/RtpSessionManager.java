package com.safie.rtp.session;

import com.safie.rtp.network.*;
import com.safie.rtp.server.RtpServer;
import com.safie.rtp.participant.*;
import com.safie.rtp.session.*;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.ChannelOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketAddress;
import java.util.Set;

public class RtpSessionManager {

    protected Logger logger = LogManager.getLogger(RtpSessionManager.class.getName());

    protected RtpSession rtpSession;
    protected RtcpSession rtcpSession;

    protected Bootstrap dataBootstrap;
    protected Bootstrap controlBootstrap;
    protected ChannelFuture dataChannelFuture;
    protected ChannelFuture controlChannelFuture;
    
    protected EventLoopGroup dataBossGroup;
    protected EventLoopGroup dataWorkerGroup;
    protected EventLoopGroup conBossGroup;
    protected EventLoopGroup conWorkerGroup;

    public RtpSessionManager(RtpServer server, SocketAddress rtpClientAddress, SocketAddress rtcpClientAddress, Set<Integer> payloadTypes){
        this.rtpSession = new RtpSession(server, rtpClientAddress, payloadTypes);
        this.rtcpSession = new RtcpSession(server, rtcpClientAddress);
    }

    public RtpSessionManager(RtpSession rtpSession, RtcpSession rtcpSession){
        this.rtpSession = rtpSession;
        this.rtcpSession = rtcpSession;
    }


    public boolean build () {
        if (this.rtpSession.running.get() == true || this.rtcpSession.running.get() == true){
            logger.error("session is already running......");
            return false;
        }

        logger.info("session is being built...");
        logger.info("data target address is ...... : "+ rtpSession.getClientAddress());
        logger.info("control target address is ... : "+ rtcpSession.getClientAddress());
        // RTPをやりとりするチャネル
        this.dataWorkerGroup = new NioEventLoopGroup();
        this.dataBootstrap = new Bootstrap();
        this.dataBootstrap.group(dataWorkerGroup);
        this.dataBootstrap.channel(NioDatagramChannel.class);
        this.dataBootstrap.handler(new ChannelInitializer<NioDatagramChannel>(){
            @Override
            public void initChannel (NioDatagramChannel ch) throws Exception {
                // dataPacketは送信のみ対応
                ch.pipeline().addLast("encoder", new DataPacketEncoder());
                
                logger.info("connected !!!");
                rtpSession.joinedNewChannel(ch);
            }
        });
        
        // RTCPをやりとりするチャネル
        this.conWorkerGroup = new NioEventLoopGroup();
        this.controlBootstrap = new Bootstrap();
        this.controlBootstrap.group(conWorkerGroup);
        this.controlBootstrap.channel(NioDatagramChannel.class);
        this.controlBootstrap.handler(new ChannelInitializer<NioDatagramChannel>(){
            @Override
            public void initChannel (NioDatagramChannel ch) throws Exception {
                ch.pipeline().addLast("encoder", new ControlPacketEncoder());
                ch.pipeline().addLast("decoder", new ControlPacketDecoder());
                ch.pipeline().addLast("handler", new ControlPacketReceiver(rtcpSession));

                rtcpSession.joinedNewChannel(ch);
            }
        });
      
        try{
            this.dataChannelFuture = this.dataBootstrap.connect(rtpSession.getClientAddress()).sync();
        }catch(Exception e){
            this.terminateEventLoop();
            this.logger.error("catch error when bind data channel", e);
            return false;
        }
        if (this.dataChannelFuture.isSuccess() != true) {
            // チャネルの作成に失敗した時
            this.terminateEventLoop();
            this.logger.error("fail to make data channel");
            return false;
        }

        try{
            this.controlChannelFuture = this.controlBootstrap.connect(rtcpSession.getClientAddress()).sync();
        }catch(Exception e){
            this.terminateEventLoop();
            this.dataChannelFuture.channel().closeFuture();
            this.logger.error("catch error when bind control channel", e);
            return false;
        }
        if (this.controlChannelFuture.isSuccess() != true) {
            // チャネルの作成に失敗した時
            this.terminateEventLoop();
            this.dataChannelFuture.channel().closeFuture();
            this.logger.error("fail to make cont channel");
            return false;
        }

        this.rtpSession.running.set(true);
        this.rtcpSession.running.set(true);
        return true;

    }


    protected void terminateEventLoop(){
        if(this.dataBossGroup != null) this.dataBossGroup.shutdownGracefully();
        if(this.dataWorkerGroup != null) this.dataWorkerGroup.shutdownGracefully();
        if(this.conBossGroup != null) this.conBossGroup.shutdownGracefully();
        if(this.conWorkerGroup != null) this.conWorkerGroup.shutdownGracefully();
    }

    public void terminate () {
        this.terminateEventLoop();
        this.rtpSession.terminate();
        this.rtcpSession.terminate();
    }
}

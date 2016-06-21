package com.safie.rtp.server;

import com.safie.rtp.session.*;
import com.safie.rtp.packet.*;
import com.safie.rtp.participant.Participant;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.Channel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.net.SocketAddress;
import java.util.Set;
import java.util.HashSet;


public class RtpServer{

    protected Logger logger = LogManager.getLogger(RtpServer.class.getName());

    public InetSocketAddress clientAddress;
    public AtomicBoolean running;
    public AtomicBoolean isConnected;

    private NioEventLoopGroup worker;
    private Bootstrap bootstrap;

    public RtpServer (InetSocketAddress clientAddress) {
        this.clientAddress = clientAddress;
        this();
    }

    public RtpServer (){
        this.running = new AtomicBoolean(false);
        this.isConnected = new AtomicBoolean(false);
    }

    public void setClientAddress(InetSocketAddress address){
        this.clientAddress = address;
    }

    public void run() {
        if (this.running.get() == true){
            logger.error("session is already running .....");
            return;
        }

        if (this.clientAddress == null){
            logger.error("client address field is null .....");
            return;
        }

        logger.debug("rtp server is being build");

        this.worker = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap()
            .group(worker)
            .channel(NioDatagramChannel.class)
            .handler(new ChannelInitializer<NioDatagramChannel>(){
                @Override
                public void initChannel (NioDatagramChannel ch) throws Exception{
                    ch.pipeline().addLast("encoder", new DataPacketEncoder());
                    logger.debug("connected!!");
                    this.connected0(ch);
                }
            });
        ChannelFuture channel = this.bootstrap.connect(this.clientAddress);
        try{
            if (channel.sync.isSuccess() == false){
                this.terminate();
                logger.error("fail to make data channel");
                return;
            }
        }catch(Exception e){
            this.terminate();
            logger.error("catch error when connect data channel...");
            return;
        }

        this.running.set(true);
    }


    private void terminate(){
        if(this.workerGroup != null) this.workerGroup.shutdownGracefully();
        logger.debug("terminate");
    }

    public void connected(){}

    public void connected0(NioDatagramChannel ch){
        this.clientChannel = ch;
        this.isConnected.set(true);
        this.connected(ch);
    }


    public void send(DataPacket packet){
        this.clientChannel.writeAndFlush(packet);
    }

}

package com.safie.rtp.session;

import com.safie.rtp.participant.*;
import com.safie.rtp.packet.*;
import com.safie.rtp.server.*;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import java.util.HashSet;
import java.net.SocketAddress;

//-------------Obligation---------------------
// セッションの状態情報を保持する
//--------------------------------------------

// TODO tiemout処理

public class RtcpSession extends Session<ControlPacket> {

    private Logger logger = LogManager.getLogger(RtcpSession.class.getName());

    private SocketAddress clientAddress;
    private Channel clientChannel;

    public RtcpSession (RtpServer server) {
        super(server);
    }

    public RtcpSession (RtpServer server, SocketAddress clientAddress) {
        this(server);
        this.clientAddress = clientAddress;
    }

    public void setClientAddress(SocketAddress clientAddress){
        this.clientAddress = clientAddress;
    }
    
    public SocketAddress getClientAddress(){
        return this.clientAddress;
    }

    @Override
    public void joinedNewChannel(Channel ch){
        this.clientChannel = ch;
        server.joinedNewRtcpChannel(ch);
    }

    // データの送信
    @Override
    public boolean sendPacket (ControlPacket packet) {
        logger.debug("send control packet : "+ packet);
        
        this.clientChannel.writeAndFlush(packet);
        
        return true;
    }

    public void receivedControlPacket(ChannelHandlerContext ctx, ControlPacket msg){
        logger.debug("control packet received ....\n"+msg.toString());
    }

    @Override
    public void terminate(){};
}

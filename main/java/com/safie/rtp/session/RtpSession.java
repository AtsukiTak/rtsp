package com.safie.rtp.session;

import com.safie.rtp.participant.*;
import com.safie.rtp.packet.*;
import com.safie.rtp.server.*;

import io.netty.channel.Channel;

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

public class RtpSession extends Session<DataPacket> {

    private Logger logger = LogManager.getLogger(RtpSession.class.getName());

    private SocketAddress clientAddress;
    public final Set<Integer> payloadTypes;
    private AtomicInteger sequence;
    private Channel clientChannel;

    public RtpSession (RtpServer server, SocketAddress clientAddress, Set<Integer> payloadTypes) {
        this(server, payloadTypes);
        this.clientAddress = clientAddress;
    }

    public RtpSession (RtpServer server, Set<Integer> payloadTypes) {
        super(server);

        for (int payloadType : payloadTypes) {
            if ((payloadType < 0) || (payloadType > 127)) {
                throw new IllegalArgumentException("PayloadTypes must be in range [0:127]");
            }
        }

        this.payloadTypes = payloadTypes;
        this.sequence = new AtomicInteger(0);
    }

    public void setClientAddress(SocketAddress clientAddress){
        this.clientAddress = clientAddress;
    }

    public SocketAddress getClientAddress(){
        return this.clientAddress;
    }


    // payloadTypeがこのセッションで許可されたものであるか
    public boolean isAcceptedPayloadType(int payloadType) {
        boolean result = this.payloadTypes.contains(payloadType);
        if(!result) logger.info("this payload type is not accepted : "+ payloadType);
        return result;
    }

    @Override
    public void joinedNewChannel(Channel ch){
        this.clientChannel = ch;
        server.joinedNewRtpChannel(ch);
    }

    // データの送信
    @Override
    public boolean sendPacket (DataPacket packet) {
        logger.debug("sending data packet....");
        if (!isAcceptedPayloadType(packet.getPayloadType())) {
            logger.error("fail to send.....");
            return false;
        }
        packet.setSequenceNumber(this.sequence.incrementAndGet());
        logger.debug("send data packet : "+ packet.toBnrString());
        
        this.clientChannel.writeAndFlush(packet);
        
        return true;
    }

    @Override
    public void terminate(){};
}

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

    private AtomicInteger sequence;
    private Consumer<DataPacket> sender;
    private int ssrc;

    public RtpSession () {
        this.sequence = new AtomicInteger(0);
    }

    public void setSender(Consumer<DataPacket> sender){
        this.sender = sender;
    }

    public void setSsrc(int ssrc){
        this.ssrc = ssrc;
    }

    // データの送信
    @Override
    public void sendPacket (DataPacket packet) {
        packet.setSequenceNumber(this.sequence.incrementAndGet());
        logger.debug("send data packet : "+ packet.toBnrString());
        sender.accept;
    }

    @Override
    public void terminate(){};
}

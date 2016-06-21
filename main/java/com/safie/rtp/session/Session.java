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

//-------------Obligation---------------------
// セッションの状態情報を保持する
//--------------------------------------------

// TODO tiemout処理

public abstract class Session <P extends Packet>{

    private Logger logger = LogManager.getLogger(RtpSession.class.getName());

    // internal vars
    protected AtomicBoolean running;

    public Session () {
        this.running = new AtomicBoolean(false);
    }


    public abstract void joinedNewChannel(Channel ch);

    // データの送信
    public abstract boolean sendPacket (P packet);

    public abstract void terminate();

}

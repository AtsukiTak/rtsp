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

    public long ssrc;

    public RtpServer () {
    }


    public void joinedNewRtpChannel(Channel ch){
    }

    public void joinedNewRtcpChannel(Channel ch){
    }

}

package com.safie.rtp.server;

import com.safie.rtp.packet.*;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;
import java.util.HashSet;

public class SimpleRtpServer extends RtpServer{

    private Logger logger = LogManager.getLogger(SimpleRtpServer.class.getName());

    public SimpleRtpServer (){
    }

}

package com.safie.rtsp.session;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Set;

public class RtspSession {

    private Logger logger = LogManager.getLogger(RtspSession.class);

    public String id;
    public RtpPlayer rtpPlayer;
    protected RtpServer rtpServer;
    protected RtpSession rtpSession;
    // TODO rtcpの実装
    protected InetSocketAddress clientRtcpAddress;

    public RtspSession(String id, RtspConfig config){
        this.id = id;
        this.rtpPlayer = RtpPlayer.newPlayer(config);
    }

    public void setClientRtpAddress(InetSocketAddress address){
        this.rtpServer = RtpSessionManager.build(address);
        this.rtpSession = rtpServer.session;
        this.player.setSession(this.rtpSession);
    }

    public void setClientRtcpAddress(InetSocketAddress address){
        this.clientRtcpAddress = address;
    }

}

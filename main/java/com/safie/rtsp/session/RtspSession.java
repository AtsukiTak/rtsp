package com.safie.rtsp.session;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Set;

public class RtspSession {

    private Logger logger = LogManager.getLogger(RtspSession.class);

    public int id;
    public RtpPlayer rtpPlayer;
    public RtpServer rtpServer;
    protected RtpSession rtpSession;
    // TODO rtcpの実装
    protected InetSocketAddress clientRtcpAddress;
    protected RtcpServer rtcpServer;

    public RtspSession(String id, RtspConfig config){
        this.id = id;
        this.rtpSession = new RtpSession();

        this.rtpServer = new RtpServer(){
            @Override
            public void connected(){
                rtpSession.setSender((packet) -> {
                    rtpServer.send(packet);
                });
            }
        }

        this.rtpPlayer = RtpPlayerFactory.generate(config);
        this.rtpPlayer.setSender((packet) -> {
            this.rtpSession.sendPacket(packet);
        });
    }

    public void setClientRtpAddress(InetSocketAddress address){
        this.rtpServer.setClientAddress(address);
    }

    public void runRtpServer(){
        this.rtpServer.run();
    }

    public void setClientRtcpAddress(InetSocketAddress address){
        this.clientRtcpAddress = address;
    }

    public void setSsrc(int ssrc){
        this.rtpSession.setSsrc(ssrc);
    }

    public void destory(){
        this.rtpSession.destory();
    }

}

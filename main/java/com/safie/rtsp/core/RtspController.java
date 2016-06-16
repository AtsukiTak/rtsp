package com.safie.rtsp.core;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.rtsp.RtspMethods;
import io.netty.util.CharsetUtil;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

// import com.darkmi.server.rtsp.GetParameterAction;
import com.safie.rtsp.action.*;
import com.safie.rtsp.session.*;
import com.safie.rtsp.util.*;
import com.safie.rtp.session.*;

public class RtspController {
    private static Logger logger = LogManager.getLogger(RtspController.class);

    public static final String SERVER = "RtspServer";
    public static final String REQUIRE_VALUE_NGOD_R2 = "com.comcast.ngod.r2";
    public static final String REQUIRE_VALUE_NGOD_C1 = "com.comcast.ngod.c1";

    private String ip;
    private int port;
    private RtspServer server;
    public static final SessionDatabase sessionDatabase = new SessionDatabase();
    public static final RtspSessionKeyFactory keyFactory = new SimpleRandomKeyFactory();


    public RtspController (String ip, int port, RtpSession session, RtcpSession rtcpSession){
        this.ip = ip;
        this.port = port;
        this.server = new RtspServer(ip, port, session, rtcpSession);
    }


    public void start() throws Exception {
        this.server.run();
        logger.debug("Started Rtsp Server. ");
    }

    public void stop() {
        this.server.stop();
    }


    public void onRtspResponse(HttpResponse response) {
    }

    /*-----------Setter And Getter --------------*/

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

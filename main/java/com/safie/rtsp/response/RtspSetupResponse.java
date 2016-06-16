package com.safie.rtsp.action;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.rtsp.RtspHeaders;
import io.netty.handler.codec.rtsp.RtspHeaders.Names;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.SocketAddress;
import java.net.InetSocketAddress;

import com.safie.rtsp.core.RtspController;
import com.safie.rtsp.session.SessionDatabase;
import com.safie.rtsp.session.RtspSession;
import com.safie.rtsp.util.DateUtil;
import com.safie.rtp.session.*;

public class SetupAction extends Action {
  private static Logger logger = LogManager.getLogger(SetupAction.class);

  private String ip;
  private Integer port;
  private RtpSession rtpSession;
  private RtcpSession rtcpSession;

  public SetupAction(HttpRequest request, RtpSession rtpSession, RtcpSession rtcpSession) {
    super(request);
    this.rtpSession = rtpSession;
    this.rtcpSession = rtcpSession;
  }


  @Override
  public HttpResponse call(){
    FullHttpResponse response = null;

    String cseq = checkCseq();
    if (cseq == null) return internalServerErrorWithoutCseq();

    if (checkRequire() == false) return optionNotSupported();

    // get Transport
    String transport = request.headers().get(Names.TRANSPORT);
    if (null == transport || transport.equals("")) {
      logger.error("transport is null.........");
      return internalServerErrorWithCseq();
    }

    // get client_port
    String regex = "client_port=(\\d+)-(\\d+)";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(transport);
    if (!m.find()){
        logger.error("client port is null............");
        return internalServerErrorWithCseq();
    }
    String rtpPort = m.group(1);
    String rtcpPort = m.group(2);
    SocketAddress dataAddress = new InetSocketAddress("localhost", Integer.parseInt(rtpPort));
    SocketAddress controlAddress = new InetSocketAddress("localhost", Integer.parseInt(rtcpPort));
    rtpSession.setClientAddress(dataAddress);
    rtcpSession.setClientAddress(controlAddress);


    String sessionKey = RtspController.keyFactory.createSessionKey();
    logger.debug("sessionKey --> " + sessionKey);
    RtspSession session = RtspController.sessionDatabase.get(sessionKey);
    if (session == null){
        session = new RtspSession(sessionKey);
        RtspController.sessionDatabase.put(session, sessionKey);
    }
    // save transport
    session.setTransport(transport);

    response =
        new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
    response.headers().set(Names.CSEQ, cseq);
    response.headers().set(Names.DATE, DateUtil.getGmtDate());
    response.headers().set(Names.SESSION, sessionKey + ";timeout=60");
    response.headers().set(Names.TRANSPORT, transport);
    response.headers().set(Names.CONTENT_LENGTH, "0");
    return response;
  }
}

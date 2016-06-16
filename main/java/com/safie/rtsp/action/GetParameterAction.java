package com.safie.rtsp.action;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.rtsp.RtspHeaders;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;
import io.netty.handler.codec.rtsp.RtspHeaders.Names;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.safie.rtsp.core.RtspController;
import com.safie.rtsp.session.RtspSession;
import com.safie.rtsp.util.DateUtil;

public class GetParameterAction extends Action {
  private static Logger logger = LogManager.getLogger(GetParameterAction.class);
  private HttpRequest request = null;

  public GetParameterAction(HttpRequest request) {
      super(request);
  }

  public HttpResponse call() throws Exception {
    HttpResponse response = null;
    
    String cseq = checkCseq();
    if (cseq == null) return internalServerErrorWithoutCseq();

    if (checkRequire() == false) return optionNotSupported();

    String sessionKey = this.request.headers().get(Names.SESSION);
    if (null == sessionKey || "".equals(sessionKey)) {
      response = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.BAD_REQUEST);
      response.headers().set(Names.SERVER, RtspController.SERVER);
      response.headers().set(Names.CSEQ, request.headers().get(Names.CSEQ));
      response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
      return response;
    }

    // get session
    RtspSession rtspSession = RtspController.sessionDatabase.get(sessionKey);
    if (null == rtspSession) {
      logger.error("rtspSession is null.");
      response = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.BAD_REQUEST);
      response.headers().set(Names.SERVER, RtspController.SERVER);
      response.headers().set(Names.CSEQ, request.headers().get(Names.CSEQ));
      response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
      return response;
    }

    response = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
    response.headers().set(Names.CSEQ, request.headers().get(Names.CSEQ));
    response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
    response.headers().set(RtspHeaders.Names.DATE, DateUtil.getGmtDate());
    response.headers().set(RtspHeaders.Names.SESSION, sessionKey);
    response.headers().set(RtspHeaders.Names.RANGE, request.headers().get(RtspHeaders.Names.RANGE));

    String scale = request.headers().get(RtspHeaders.Names.SCALE);
    if (null != scale) {
      response.headers().set(RtspHeaders.Names.SCALE, scale);
    } else {
      response.headers().set(RtspHeaders.Names.SCALE, "1.00");
    }
    return response;
  }

}

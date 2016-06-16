package com.safie.rtsp.action;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.rtsp.RtspHeaders;
import io.netty.handler.codec.rtsp.RtspHeaders.Names;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.safie.rtsp.core.RtspController;

class Action implements Callable<HttpResponse> {
  private Logger logger = LogManager.getLogger(Action.class);

  protected HttpRequest request;

  public Action(HttpRequest request) {
    this.request = request;
  }

  @Override
  public HttpResponse call() throws Exception {
    return internalServerErrorWithoutCseq();
  }


  protected String checkCseq(){
    String cseq = request.headers().get(Names.CSEQ);
    if (null == cseq || "".equals(cseq)) {
      logger.error("cesq is null.........");
      return null;
    }else{
      return cseq;
    }
  }

  protected String getRequire(){
    String require = request.headers().get(Names.REQUIRE);
    if (null == require || "".equals(require)
        || (!require.equals(RtspController.REQUIRE_VALUE_NGOD_R2))) {
      return null;
    }else{
      return require;
    }
  }

  protected boolean checkRequire(){
      String require = getRequire();
      if (require == null) return true;
      else{
          logger.error("option not supported : "+ getRequire());
          // 対応しているかどうかのチェックをすること
          return false;
      }
  }


  protected HttpResponse internalServerErrorWithoutCseq(){
    HttpResponse response =
        new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
            RtspResponseStatuses.INTERNAL_SERVER_ERROR);
    response.headers().set(Names.SERVER, RtspController.SERVER);
    response.headers().set("OnDemandSessionId", request.headers().get("OnDemandSessionId"));
    return response;
  }


  protected HttpResponse internalServerErrorWithCseq(){
    HttpResponse response =
        new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
            RtspResponseStatuses.INTERNAL_SERVER_ERROR);
    response.headers().set(HttpHeaders.Names.SERVER, RtspController.SERVER);
    response.headers().set(RtspHeaders.Names.CSEQ, request.headers().get(RtspHeaders.Names.CSEQ));
    String odsi = request.headers().get("OnDemandSessionId");
    if (odsi != null) response.headers().set("OnDemandSessionId", odsi);
    return response;
  }

  protected HttpResponse optionNotSupported(){
      HttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0,
              RtspResponseStatuses.OPTION_NOT_SUPPORTED);
      response.headers().set(HttpHeaders.Names.SERVER, RtspController.SERVER);
      response.headers().set(RtspHeaders.Names.CSEQ, request.headers().get(RtspHeaders.Names.CSEQ));
      response.headers().set(RtspHeaders.Names.UNSUPPORTED, getRequire());
      String odsi = request.headers().get("OnDemandSessionId");
      if (odsi != null) response.headers().set("OnDemandSessionId", odsi);
      return response;
  }


  protected HttpResponse badRequestResponse(){
    HttpResponse response = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.BAD_REQUEST);
    response.headers().set(Names.SERVER, RtspController.SERVER);
    response.headers().set(Names.CSEQ, request.headers().get(Names.CSEQ));
    String odsi = request.headers().get("OnDemandSessionId");
    if (odsi != null) response.headers().set("OnDemandSessionId", odsi);
    return response;
  }
}

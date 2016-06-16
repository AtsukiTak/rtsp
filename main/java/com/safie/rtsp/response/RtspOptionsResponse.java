package com.safie.rtsp.action;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.rtsp.RtspHeaders.Names;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

public class OptionsAction extends Action {

  public OptionsAction(HttpRequest request) {
    super(request);
  }

  @Override
  public HttpResponse call() throws Exception {
    HttpResponse response = null;
   
    String cseq = checkCseq();
    if (cseq == null) return internalServerErrorWithoutCseq();

    if (checkRequire() == false) return optionNotSupported();

    response = new DefaultHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK);
    response.headers().add(Names.SERVER, "RtspServer");
    response.headers().add(Names.CSEQ, this.request.headers().get(Names.CSEQ));
    response.headers().add(Names.PUBLIC, "SETUP,PLAY,PAUSE,TEARDOWN,GET_PARAMETER,OPTION");
    return response;
  }
}

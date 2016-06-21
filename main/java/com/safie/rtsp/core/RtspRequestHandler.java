package com.safie.rtsp.core;

import com.safie.rtsp.action.*;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.rtsp.RtspMethods;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.safie.rtp.session.*;

public abstract class RtspRequestHandler { 
    private static Logger logger = LogManager.getLogger(RtspRequestHandler.class);

    public abstract RtspSession getSession(int id);

    // request内容を処理するメソッド
    public void handleRtspRequest(HttpRequest request, Consumer<HttpResponse> sender) {
        try {
            if (request.getMethod().equals(RtspMethods.SETUP)) {
                onSetupRequest(request);
            } else if (request.getMethod().equals(RtspMethods.DESCRIBE)){
                onDescribeRequest(request, sender);
            } else if (request.getMethod().equals(RtspMethods.PLAY)) {
                onPlayRequest(request, sender);
            } else if (request.getMethod().equals(RtspMethods.PAUSE)) {
                onPauseRequest(request, sender);
            } else if (request.getMethod().equals(RtspMethods.GET_PARAMETER)) {
                onGetParameterRequest(request, sender);
            } else if (request.getMethod().equals(RtspMethods.TEARDOWN)) {
                onTeardownRequest(request, sender);
            } else if (request.getMethod().equals(RtspMethods.OPTIONS)) {
                onOptionRequest(request, sender);
            } else {
                logger.error("Unsupported request method : "+ request.getMethod());
            }
        } catch (Exception e) {
            logger.error("Unexpected error during processing,Caused by ", e);
        }
    }

    private void onSetupRequest(HttpRequest request, Comsumer<HttpResponse> sender) {
        try {
            RtspSetupResponse action = new RtspSetupResponse(request);

            //新しいsessionの作成
            int sessionId = action.sessionId;
            RtspSession session = initSession(sessionId);

            // client port の設定
            int rtpPort = action.rtpPort;
            int rtcpPort = action.rtcpPort;
            session.setClientRtpPort(rtpPort);
            session.setClientRtcpPort(rtcpPort);

            HttpResponse setupResponse = action.response;

            logger.debug("setup response header =====> \n" + setupResponse);
            sender.accept(setupResponse);
        } catch (Exception e) {
            logger.error("Setup Request Handle Error.........", e);
        }
    }

    private void onDescribeRequest(HttpRequest request, Comsumer<HttpResponse> sender) {
        try {
            RtspDescribeResponse action = new RtspDescribeResponse(request);
            FullHttpResponse describeResponse = (FullHttpResponse) action.response;

            logger.debug("describe response header ====> \n" + describeResponse);
            logger.debug("describe response content =====> \n"
                    + describeResponse.content().toString(CharsetUtil.UTF_8));

            sender.accept(describeResponse);
        }catch (Exception e) {
            logger.error("Describe request handle error.........", e);
        }
    }

    private void onPlayRequest(HttpRequest request, Comsumer<HttpResponse> sender) {
        try {
            RtspPlayResponse action = new RtspPlayResponse(request);

            RtspSession session = getSessionById(action.sessionId);
            session.rtpPlayer.play();

            // return response
            HttpResponse playResponse = action.response;
            sender.accept(playResponse);

            logger.debug("play response =====> \n" + playResponse);
        } catch (Exception e) {
            logger.error("Play Request Handle Error.........", e);
        }
    }

    private void onPauseRequest(HttpRequest request, Comsumer<HttpResponse> sender) {
        try {
            RtspPauseResponse responseAction = new RtspPauseResponse(request);
            
            RtspSession session = getSessionById(action.sessionId);
            session.rtpPlayer.pause();

            HttpResponse pauseResponse = responseAction.response;
            sender.accept(pauseResponse);

            logger.debug("pause response header =====> \n" + pauseResponse);

            // TODO what is doing here
            //Callable<HttpRequest> announceAction = new AnnounceAction(request);
            //HttpRequest announceRequest = announceAction.call();
            //logger.debug("announce request =====> \n" + announceRequest);

        } catch (Exception e) {
            logger.error("Pause Request Handle Error.........", e);
        }

    }

    private void onGetParameterRequest(HttpRequest request, Comsumer<HttpResponse> sender) {
        try {
            RtspGetparameterResponse action = new RtspGetparameterResponse(request);
            HttpResponse response = action.response;
            logger.debug("get_parameter response =====> \n" + response);

            sender.accept(response);
        } catch (Exception e) {
            logger.error("get_parameter Request Handle Error.........", e);
        }
    }

    private void onTeardownRequest(HttpRequest request, Comsumer<HttpResponse> sender) {
        try {
            RtspTeardownResponse action = new RtspTeardownResponse(request);
            HttpResponse response = action.response;
            logger.debug("teardown response =====> \n" + response);

            sender.accept(respnse);
        } catch (Exception e) {
            logger.error("teardown Request Handle Error.........", e);
        }
    }


    private void onOptionRequest(HttpRequest request, Comsumer<HttpResponse> sender) {
        try {
            RtspOptionsResponse action = new RtspOptionsResponse(request);
            HttpResponse setupResponse = action.response;

            logger.debug("options response header =====> \n" + setupResponse);

            sender.accept(setupResponse);
        } catch (Exception e) {
            logger.error("Options Request Handle Error.........", e);
        }
    }

}

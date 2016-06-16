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

    protected RtspRequestHandler() {
    } 

    public abstract RtspSession getSessionBySsrc(int ssrc);

    // request内容を処理するメソッド
    public void handleRtspRequest(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            if (request.getMethod().equals(RtspMethods.SETUP)) {
                onSetupRequest(request, ctx);
            } else if (request.getMethod().equals(RtspMethods.DESCRIBE)){
                onDescribeRequest(request, ctx);
            } else if (request.getMethod().equals(RtspMethods.PLAY)) {
                onPlayRequest(request, ctx);
            } else if (request.getMethod().equals(RtspMethods.PAUSE)) {
                onPauseRequest(request, ctx);
            } else if (request.getMethod().equals(RtspMethods.GET_PARAMETER)) {
                onGetParameterRequest(request, ctx);
            } else if (request.getMethod().equals(RtspMethods.TEARDOWN)) {
                onTeardownRequest(request, ctx);
            } else if (request.getMethod().equals(RtspMethods.OPTIONS)) {
                onOptionRequest(request, ctx);
            } else {
                logger.error("Unsupported request method : "+ request.getMethod());
            }
        } catch (Exception e) {
            logger.error("Unexpected error during processing,Caused by ", e);
        }
    }

    private void onSetupRequest(HttpRequest request, ChannelHandlerContext ctx) {
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
            ctx.writeAndFlush(setupResponse);
        } catch (Exception e) {
            logger.error("Setup Request Handle Error.........", e);
        }
    }

    private void onDescribeRequest(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            RtspDescribeResponse action = new RtspDescribeResponse(request);
            FullHttpResponse describeResponse = (FullHttpResponse) action.response;

            logger.debug("describe response header ====> \n" + describeResponse);
            logger.debug("describe response content =====> \n"
                    + describeResponse.content().toString(CharsetUtil.UTF_8));

            ctx.writeAndFlush(describeResponse);
        }catch (Exception e) {
            logger.error("Describe request handle error.........", e);
        }
    }

    private void onPlayRequest(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            RtspPlayResponse action = new RtspPlayResponse(request);

            RtspSession session = getSessionById(action.sessionId);
            session.rtpPlayer.play();

            // return response
            HttpResponse playResponse = action.response;
            ctx.writeAndFlush(playResponse);

            logger.debug("play response =====> \n" + playResponse);
        } catch (Exception e) {
            logger.error("Play Request Handle Error.........", e);
        }
    }

    private void onPauseRequest(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            RtspPauseResponse responseAction = new RtspPauseResponse(request);
            
            RtspSession session = getSessionById(action.sessionId);
            session.rtpPlayer.pause();

            HttpResponse pauseResponse = responseAction.response;
            ctx.writeAndFlush(pauseResponse);

            logger.debug("pause response header =====> \n" + pauseResponse);

            // TODO what is doing here
            //Callable<HttpRequest> announceAction = new AnnounceAction(request);
            //HttpRequest announceRequest = announceAction.call();
            //logger.debug("announce request =====> \n" + announceRequest);

        } catch (Exception e) {
            logger.error("Pause Request Handle Error.........", e);
        }

    }

    private void onGetParameterRequest(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            RtspGetparameterResponse action = new RtspGetparameterResponse(request);
            HttpResponse response = action.response;
            logger.debug("get_parameter response =====> \n" + response);

            ctx.writeAndFlush(response);
        } catch (Exception e) {
            logger.error("get_parameter Request Handle Error.........", e);
        }
    }

    private void onTeardownRequest(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            RtspTeardownResponse action = new RtspTeardownResponse(request);
            HttpResponse response = action.response;
            logger.debug("teardown response =====> \n" + response);

            ctx.writeAndFlush(respnse);
        } catch (Exception e) {
            logger.error("teardown Request Handle Error.........", e);
        }
    }


    private void onOptionRequest(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            RtspOptionsResponse action = new RtspOptionsResponse(request);
            HttpResponse setupResponse = action.response;

            logger.debug("options response header =====> \n" + setupResponse);

            ctx.writeAndFlush(setupResponse);
        } catch (Exception e) {
            logger.error("Options Request Handle Error.........", e);
        }
    }

}

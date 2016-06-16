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

public class RtspRequestHandler { 
    private static Logger logger = LogManager.getLogger(RtspRequestHandler.class);

    private String ip;
    private int port;
    private RtpSession rtpSession;
    private RtcpSession rtcpSession;

    protected RtspRequestHandler(String ip, int port, RtpSession rtpSession, RtcpSession rtcpSession) {
        this.ip = ip;
        this.port = port;
        this.rtpSession = rtpSession;
        this.rtcpSession = rtcpSession;
    } 

    // request内容を処理するメソッド
    public List<HttpMessage> handleRtspRequest(HttpRequest request) {
        try {
            if (request.getMethod().equals(RtspMethods.SETUP)) {
                return onSetupRequest(request, rtpSession, rtcpSession);
            } else if (request.getMethod().equals(RtspMethods.DESCRIBE)){
                return onDescribeRequest(request);
            } else if (request.getMethod().equals(RtspMethods.PLAY)) {
                return onPlayRequest(request);
            } else if (request.getMethod().equals(RtspMethods.PAUSE)) {
                return onPauseRequest(request);
            } else if (request.getMethod().equals(RtspMethods.GET_PARAMETER)) {
                return onGetParameterRequest(request);
            } else if (request.getMethod().equals(RtspMethods.TEARDOWN)) {
                return onTeardownRequest(request);
            } else if (request.getMethod().equals(RtspMethods.OPTIONS)) {
                return onOptionRequest(request);
            } else {
                logger.error("Unsupported request method : "+ request.getMethod());
                return new ArrayList();
            }
        } catch (Exception e) {
            logger.error("Unexpected error during processing,Caused by ", e);
            return new ArrayList();
        }
    }

    private List<HttpMessage> onSetupRequest(HttpRequest request, RtpSession rtpSession, RtcpSession rtcpSession) {
        try {
            Callable<HttpResponse> action = new SetupAction(request, rtpSession, rtcpSession);
            HttpResponse setupResponse = action.call();

            logger.debug("setup response header =====> \n" + setupResponse);
            List<HttpMessage> list = new ArrayList();
            list.add(setupResponse);
            return list;
        } catch (Exception e) {
            logger.error("Setup Request Handle Error.........", e);
            return new ArrayList();
        }
    }

    private List<HttpMessage> onDescribeRequest(HttpRequest request) {
        try {
            Callable<HttpResponse> action = new DescribeAction(request, this.ip, this.port);
            FullHttpResponse describeResponse = (FullHttpResponse) action.call();

            logger.debug("describe response header ====> \n" + describeResponse);
            logger.debug("describe response content =====> \n"
                    + describeResponse.content().toString(CharsetUtil.UTF_8));
            List<HttpMessage> list = new ArrayList();
            list.add(describeResponse);
            return list;
        }catch (Exception e) {
            logger.error("Describe request handle error.........", e);
            return new ArrayList();
        }
    }

    private List<HttpMessage> onPlayRequest(HttpRequest request) {
        try {
            // play rtp
            RtpSessionManager manager = new RtpSessionManager(this.rtpSession, this.rtcpSession);
            manager.build();

            // return response
            Callable<HttpResponse> action = new PlayAction(request);
            HttpResponse playResponse = action.call();
            logger.debug("play response =====> \n" + playResponse);
            List<HttpMessage> list = new ArrayList();
            list.add(playResponse);
            return list;
        } catch (Exception e) {
            logger.error("Play Request Handle Error.........", e);
            return new ArrayList();
        }
    }

    private List<HttpMessage> onPauseRequest(HttpRequest request) {
        try {
            Callable<HttpResponse> responseAction = new PauseAction(request);
            HttpResponse pauseResponse = responseAction.call();
            logger.debug("pause response header =====> \n" + pauseResponse);

            Callable<HttpRequest> announceAction = new AnnounceAction(request);
            HttpRequest announceRequest = announceAction.call();
            logger.debug("announce request =====> \n" + announceRequest);

            List<HttpMessage> list = new ArrayList();
            list.add(pauseResponse);
            list.add(announceRequest);
            return list;
        } catch (Exception e) {
            logger.error("Pause Request Handle Error.........", e);
            return new ArrayList();
        }

    }

    private List<HttpMessage> onGetParameterRequest(HttpRequest request) {
        try {
            Callable<HttpResponse> action = new GetParameterAction(request);
            HttpResponse response = action.call();
            logger.debug("get_parameter response =====> \n" + response);

            List<HttpMessage> list = new ArrayList();
            list.add(response);
            return list;
        } catch (Exception e) {
            logger.error("get_parameter Request Handle Error.........", e);
            return new ArrayList();
        }
    }

    private List<HttpMessage> onTeardownRequest(HttpRequest request) {
        try {
            Callable<HttpResponse> action = new TeardownAction(request);
            HttpResponse response = action.call();
            logger.debug("teardown response =====> \n" + response);

            List<HttpMessage> list = new ArrayList();
            list.add(response);
            return list;
        } catch (Exception e) {
            logger.error("teardown Request Handle Error.........", e);
            return new ArrayList();
        }
    }


    private List<HttpMessage> onOptionRequest(HttpRequest request) {
        try {
            Callable<HttpResponse> action = new OptionsAction(request);
            HttpResponse setupResponse = (HttpResponse) action.call();

            logger.debug("options response header =====> \n" + setupResponse);

            List<HttpMessage> list = new ArrayList();
            list.add(setupResponse);
            return list;
        } catch (Exception e) {
            logger.error("Options Request Handle Error.........", e);
            return new ArrayList();
        }
    }

}

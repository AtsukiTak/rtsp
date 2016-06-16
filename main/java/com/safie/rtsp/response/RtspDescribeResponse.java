package com.safie.rtsp.action;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.rtsp.RtspHeaders;
import io.netty.handler.codec.rtsp.RtspResponseStatuses;
import io.netty.handler.codec.rtsp.RtspVersions;

import java.util.concurrent.Callable;

import com.safie.rtsp.core.RtspController;

public class DescribeAction extends Action {

    private final String ip;
    private final int port;

    public DescribeAction(HttpRequest request, String ip, int port) {
        super(request);
        this.ip = ip;
        this.port = port;
    }

    @Override
    public HttpResponse call() throws Exception {

        String cseq = checkCseq();
        if (cseq == null) return internalServerErrorWithoutCseq();

        // set sdp extension
        StringBuffer sdp = new StringBuffer();
        sdp.append("v=0\r\n");
        sdp.append("s=RTSP Session\r\n");
        sdp.append("t=0 0\r\n");
        sdp.append("c=IN IP4 0.0.0.0\r\n");
        sdp.append("m=audio 8080 RTP/AVP 0\r\n");
        //sdp.append("a=rtpmap:96 PCMU/16000/2\r\n");
        //sdp.append("a=control:rtsp://" + this.ip + ":" + "8080" + "\r\n");


        FullHttpResponse response = new DefaultFullHttpResponse(RtspVersions.RTSP_1_0, RtspResponseStatuses.OK,
                Unpooled.wrappedBuffer(sdp.toString().getBytes()));
        response.headers().set(HttpHeaders.Names.SERVER, RtspController.SERVER);
        response.headers().set(RtspHeaders.Names.CSEQ,
                this.request.headers().get(RtspHeaders.Names.CSEQ));
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(sdp.length()));
        response.headers().set(RtspHeaders.Names.CONTENT_TYPE, "application/sdp");
        return response;
    }

//    private String sdpOfAac(){
//        StringBuffer sdp = new StringBuffer();
//        sdp.append("v=0\r\n");
//        sdp.append("s=RTSP Session\r\n");
//        sdp.append("t=0 0\r\n");
//        sdp.append("m=audio "+dstPort+" RTP/AVP 96\r\n");
//        adp.append("a=rtpmap:96 mpeg4-generic/"+clockRate+"/"+channel+"\r\n");
//        sdp.append("a=fmtp:96 streamType=5; profile-level-id=14; mode=AAC-lbr; config="+config+"sizeLength=6; indexLength=2; indexDeltaLength=2; constantDuration=1024; maxDisplacement=5\r\n");
//        return sdp.toString();
//    }

}

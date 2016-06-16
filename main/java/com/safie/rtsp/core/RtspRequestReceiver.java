package com.safie.rtsp.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelHandler.Sharable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Sharable
public abstract class RtspRequestReceiver extends ChannelInboundHandlerAdapter{
    private final Logger logger = LogManager.getLogger(RtspRequestReceiver.class.getName());

    @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) msg;
                logger.debug("client request =========>\n{}", request.toString());
                requestReceived(request, ctx);
            }
        }

    public abstract void requestReceived(HttpRequest request, ChannelHandlerContext ctx);

    @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("channel error : " + cause.getMessage(), cause);
        }

    @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.debug("channelActive...............");
        }

    @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.debug("channelInactive...............");
        }

}

package com.safie.rtp.network;

import com.safie.rtp.session.RtcpSession;
import com.safie.rtp.packet.ControlPacket;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class ControlPacketReceiver extends ChannelInboundHandlerAdapter{

    protected final RtcpSession rtcpSession;

    public ControlPacketReceiver (RtcpSession rtcpSession) {
        this.rtcpSession = rtcpSession;
    }

    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ControlPacket)
            rtcpSession.receivedControlPacket(ctx, (ControlPacket) msg);
    }
}

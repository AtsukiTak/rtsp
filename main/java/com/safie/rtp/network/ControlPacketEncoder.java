package com.safie.rtp.network;

import com.safie.rtp.packet.ControlPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ControlPacketEncoder extends MessageToByteEncoder<ControlPacket> {

    @Override
    public void encode (ChannelHandlerContext ctx, ControlPacket msg, ByteBuf out) throws Exception {
        try {
            out.writeBytes(msg.encode());
        } catch (Exception e1) {
            throw e1;
        }
    }
}

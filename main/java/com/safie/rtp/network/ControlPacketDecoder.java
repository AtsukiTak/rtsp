package com.safie.rtp.network;

import com.safie.rtp.packet.ControlPacket;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class ControlPacketDecoder extends ByteToMessageDecoder {

    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if ((in.readableBytes() % 4) != 0) {
            return;
        }

        // While there's data to read, keep on decoding.
        while (in.readableBytes() > 0) {
            try {
                out.add(ControlPacket.decode(in));
            } catch (Exception e1) {
                return;
            }
        }
    }
}

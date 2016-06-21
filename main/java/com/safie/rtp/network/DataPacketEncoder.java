package com.safie.rtp.network;

import com.safie.rtp.packet.DataPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataPacketEncoder extends MessageToByteEncoder<DataPacket> {

    protected Logger logger = LogManager.getLogger(DataPacketEncoder.class.getName());

    @Override
    protected void encode(ChannelHandlerContext ctx, DataPacket packet, ByteBuf out) throws Exception {
        if (packet.getDataSize() == 0) {
            return;
        }
        out.writeBytes(packet.encode());
        this.logger.debug("encoding data packet...");
    }
}

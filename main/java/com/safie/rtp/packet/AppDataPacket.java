package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;

public class AppDataPacket extends ControlPacket {

    // constructors ---------------------------------------------------------------------------------------------------

    public AppDataPacket(Type type) {
        super(type);
    }

    // public static methods ------------------------------------------------------------------------------------------

    public static ByteBuf encode(int currentCompoundLength, int fixedBlockSize, AppDataPacket packet) {
        return null;
    }

    // ControlPacket --------------------------------------------------------------------------------------------------

    @Override
    public ByteBuf encode(int currentCompoundLength, int fixedBlockSize) {
        return encode(currentCompoundLength, fixedBlockSize, this);
    }

    @Override
    public ByteBuf encode() {
        return encode(0, 0, this);
    }
}

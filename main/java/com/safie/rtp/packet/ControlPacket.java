package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ControlPacket extends Packet {

    protected static Logger logger = LogManager.getLogger(ControlPacket.class.getName());

    protected RtpVersion version;
    protected Type type;

    protected ControlPacket(Type type) {
        this.version = RtpVersion.V2;
        this.type = type;
    }

    // public methods -------------------------------------------------------------------------------------------------

    public static ControlPacket decode(ByteBuf buffer) {
        if ((buffer.readableBytes() % 4) > 0) {
            throw new IllegalArgumentException("Invalid RTCP packet length: expecting multiple of 4 and got " +
                                               buffer.readableBytes());
        }
        byte b = buffer.readByte();
        RtpVersion version = RtpVersion.fromByte(b);
        if (!version.equals(RtpVersion.V2)) {
            return null;
        }
        boolean hasPadding = (b & 0x20) > 0; // mask 0010 0000
        byte innerBlocks = (byte) (b & 0x1f); // mask 0001 1111

        ControlPacket.Type type = ControlPacket.Type.fromByte(buffer.readByte());

        // This length is in 32bit (4byte) words. These first 4 bytes already read don't count.
        int length = buffer.readShort();
        if (length == 0) {
            return null;
        }

        // No need to pass version downwards, only V2 is supported so subclasses can safely assume V2.
        // I know it's ugly when the superclass knows about the subclasses but since this method is static (and NEEDS
        // to be) the alternative was having this method in a external class. Pointless. 
        switch (type) {
            case SENDER_REPORT:
                logger.debug("decoding Sender Report");
                return SenderReportPacket.decode(buffer, hasPadding, innerBlocks, length);
            case RECEIVER_REPORT:
                logger.debug("decoding Receiver Report");
                return ReceiverReportPacket.decode(buffer, hasPadding, innerBlocks, length);
            case SOURCE_DESCRIPTION:
                logger.debug("decoding Source Description");
                return SourceDescriptionPacket.decode(buffer, hasPadding, innerBlocks, length);
            case BYE:
                logger.debug("decoding Bye");
                return ByePacket.decode(buffer, hasPadding, innerBlocks, length);
            case APP_DATA:
                return null;
            default:
                logger.warn("Unknown RTCP packet type: " + type);
                throw new IllegalArgumentException("Unknown RTCP packet type: " + type);
        }
    }

    public abstract ByteBuf encode(int currentCompoundLength, int fixedBlockSize);

    public abstract ByteBuf encode();

    // getters & setters ----------------------------------------------------------------------------------------------

    public RtpVersion getVersion() {
        return version;
    }

    public void setVersion(RtpVersion version) {
        if (version != RtpVersion.V2) {
            throw new IllegalArgumentException("Only V2 is supported");
        }
        this.version = version;
    }

    public Type getType() {
        return type;
    }

    // public classes -------------------------------------------------------------------------------------------------

    public static enum Type {

        // constants --------------------------------------------------------------------------------------------------

        SENDER_REPORT((byte) 0xc8),
        RECEIVER_REPORT((byte) 0xc9),
        SOURCE_DESCRIPTION((byte) 0xca),
        BYE((byte) 0xcb),
        APP_DATA((byte) 0xcc);

        // internal vars ----------------------------------------------------------------------------------------------

        private byte b;

        // constructors -----------------------------------------------------------------------------------------------

        Type(byte b) {
            this.b = b;
        }

        // public methods ---------------------------------------------------------------------------------------------

        public static Type fromByte(byte b) {
            switch (b) {
                case (byte) 0xc8:
                    return SENDER_REPORT;
                case (byte) 0xc9:
                    return RECEIVER_REPORT;
                case (byte) 0xca:
                    return SOURCE_DESCRIPTION;
                case (byte) 0xcb:
                    return BYE;
                case (byte) 0xcc:
                    return APP_DATA;
                default:
                    throw new IllegalArgumentException("Unknown RTCP packet type: " + b);
            }
        }

        // getters & setters ------------------------------------------------------------------------------------------

        public byte getByte() {
            return this.b;
        }
    }
}

package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class SdesChunkItem {

    // internal vars --------------------------------------------------------------------------------------------------

    protected final Type type;
    protected final String value;

    // constructors ---------------------------------------------------------------------------------------------------

    protected SdesChunkItem(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    // public methods -------------------------------------------------------------------------------------------------

    public ByteBuf encode() {
        // Technically, this never happens as you're not allowed to add NULL items to a SdesChunk instance, but...
        if (this.type == Type.NULL) {
            ByteBuf buffer = Unpooled.buffer(1);
            buffer.writeByte(0x00);
            return buffer;
        }

        byte[] valueBytes;
        if (this.value != null) {
            // RFC section 6.5 mandates that this must be UTF8
            // http://tools.ietf.org/html/rfc3550#section-6.5
            valueBytes = this.value.getBytes(CharsetUtil.UTF_8);
        } else {
            valueBytes = new byte[]{};
        }

        if (valueBytes.length > 255) {
            throw new IllegalArgumentException("Content (text) can be no longer than 255 bytes and this has " +
                                               valueBytes.length);
        }

        // Type (1b), length (1b), value (xb)
        ByteBuf buffer = Unpooled.buffer(2 + valueBytes.length);
        buffer.writeByte(this.type.getByte());
        buffer.writeByte(valueBytes.length);
        buffer.writeBytes(valueBytes);

        return buffer;
    }

    // getters & setters ----------------------------------------------------------------------------------------------

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder()
                .append("SdesChunkItem{")
                .append("type=").append(this.type)
                .append(", value='").append(this.value).append('\'')
                .append('}').toString();
    }

    // public classes -------------------------------------------------------------------------------------------------

    public static enum Type {

        // constants --------------------------------------------------------------------------------------------------

        NULL((byte) 0),
        CNAME((byte) 1),
        NAME((byte) 2),
        EMAIL((byte) 3),
        PHONE((byte) 4),
        LOCATION((byte) 5),
        TOOL((byte) 6),
        NOTE((byte) 7),
        PRIV((byte) 8);

        // internal vars ----------------------------------------------------------------------------------------------

        private final byte b;

        // constructors -----------------------------------------------------------------------------------------------

        Type(byte b) {
            this.b = b;
        }

        // public static methods --------------------------------------------------------------------------------------

        public static Type fromByte(byte b) {
            switch (b) {
                case 0: return NULL;
                case 1: return CNAME;
                case 2: return NAME;
                case 3: return EMAIL;
                case 4: return PHONE;
                case 5: return LOCATION;
                case 6: return TOOL;
                case 7: return NOTE;
                case 8: return PRIV;
                default: throw new IllegalArgumentException("Unknown SSRC Chunk Item type: " + b);
            }
        }

        // getters & setters ------------------------------------------------------------------------------------------

        public byte getByte() {
            return b;
        }
    }
}

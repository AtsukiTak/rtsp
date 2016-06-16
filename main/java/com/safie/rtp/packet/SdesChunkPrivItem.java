package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class SdesChunkPrivItem extends SdesChunkItem {

    // internal vars --------------------------------------------------------------------------------------------------

    private final String prefix;

    // constructors ---------------------------------------------------------------------------------------------------

    protected SdesChunkPrivItem(String prefix, String value) {
        super(SdesChunkItem.Type.PRIV, value);
        this.prefix = prefix;
    }

    // public methods -------------------------------------------------------------------------------------------------

    @Override
    public ByteBuf encode() {
        byte[] prefixBytes;
        if (this.prefix != null) {
            // RFC section 6.5 mandates that this must be UTF8
            // http://tools.ietf.org/html/rfc3550#section-6.5
            prefixBytes = this.prefix.getBytes(CharsetUtil.UTF_8);
        } else {
            prefixBytes = new byte[]{};
        }

        byte[] valueBytes;
        if (this.value != null) {
            // RFC section 6.5 mandates that this must be UTF8
            // http://tools.ietf.org/html/rfc3550#section-6.5
            valueBytes = this.value.getBytes(CharsetUtil.UTF_8);
        } else {
            valueBytes = new byte[]{};
        }

        if ((prefixBytes.length + valueBytes.length) > 254) {
            throw new IllegalArgumentException("Content (prefix + text) can be no longer than 255 bytes and this has " +
                                               valueBytes.length);
        }

        // Type (1b), total item length (1b), prefix length (1b), prefix (xb), text (xb)
        ByteBuf buffer = Unpooled.buffer(2 + 1 + prefixBytes.length + valueBytes.length);
        buffer.writeByte(this.type.getByte());
        buffer.writeByte(1 + prefixBytes.length + valueBytes.length);
        buffer.writeByte(prefixBytes.length);
        buffer.writeBytes(prefixBytes);
        buffer.writeBytes(valueBytes);

        return buffer;
    }

    // getters & setters ----------------------------------------------------------------------------------------------

    public String getPrefix() {
        return prefix;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder()
                .append("SdesChunkPrivItem{")
                .append("prefix='").append(this.prefix).append('\'')
                .append(", value='").append(this.value).append('\'')
                .append('}').toString();
    }
}

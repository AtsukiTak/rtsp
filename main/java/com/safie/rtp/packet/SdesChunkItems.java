package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class SdesChunkItems {

    // constants ------------------------------------------------------------------------------------------------------

    public static final SdesChunkItem NULL_ITEM = new SdesChunkItem(SdesChunkItem.Type.NULL, null);

    // public static methods ------------------------------------------------------------------------------------------

    public static SdesChunkItem createNullItem() {
        return NULL_ITEM;
    }

    public static SdesChunkItem createCnameItem(String cname) {
        return new SdesChunkItem(SdesChunkItem.Type.CNAME, cname);
    }

    public static SdesChunkItem createNameItem(String name) {
        return new SdesChunkItem(SdesChunkItem.Type.NAME, name);
    }

    public static SdesChunkItem createEmailItem(String email) {
        return new SdesChunkItem(SdesChunkItem.Type.EMAIL, email);
    }

    public static SdesChunkItem createPhoneItem(String phone) {
        return new SdesChunkItem(SdesChunkItem.Type.PHONE, phone);
    }

    public static SdesChunkItem createLocationItem(String location) {
        return new SdesChunkItem(SdesChunkItem.Type.LOCATION, location);
    }

    public static SdesChunkItem createToolItem(String tool) {
        return new SdesChunkItem(SdesChunkItem.Type.TOOL, tool);
    }

    public static SdesChunkItem createNoteItem(String note) {
        return new SdesChunkItem(SdesChunkItem.Type.NOTE, note);
    }

    public static SdesChunkPrivItem createPrivItem(String prefix, String value) {
        return new SdesChunkPrivItem(prefix, value);
    }

    public static SdesChunkItem decode(ByteBuf buffer) {
        SdesChunkItem.Type type = SdesChunkItem.Type.fromByte(buffer.readByte());
        switch (type) {
            case NULL:
                return NULL_ITEM;
            case CNAME:
            case NAME:
            case EMAIL:
            case PHONE:
            case LOCATION:
            case TOOL:
            case NOTE:
                byte[] value = new byte[buffer.readUnsignedByte()];
                buffer.readBytes(value);
                return new SdesChunkItem(type, new String(value, CharsetUtil.UTF_8));
            case PRIV:
                short valueLength = buffer.readUnsignedByte();
                short prefixLength = buffer.readUnsignedByte();
                // Value length field indicates the length of all that follows this field:
                // Prefix length (1b), Prefix (xb) and Value itself (xb). Thus, the actual value length is equal to
                // the length indicated by this field - 1b (prefix length) and - xb (prefix value).
                value = new byte[valueLength - prefixLength - 1];
                byte[] prefix = new byte[prefixLength];
                buffer.readBytes(prefix);
                buffer.readBytes(value);
                return new SdesChunkPrivItem(new String(prefix, CharsetUtil.UTF_8),
                                             new String(value, CharsetUtil.UTF_8));
            default:
                throw new IllegalArgumentException("Unknown type of SDES chunk: " + type);
        }
    }

    public static ByteBuf encode(SdesChunkItem item) {
        return item.encode();
    }
}

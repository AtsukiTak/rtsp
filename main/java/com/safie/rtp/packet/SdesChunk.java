package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SdesChunk {

    // internal vars --------------------------------------------------------------------------------------------------

    private long ssrc;
    private List<SdesChunkItem> items;

    // constructors ---------------------------------------------------------------------------------------------------

    public SdesChunk() {
    }

    public SdesChunk(long ssrc) {
        this.ssrc = ssrc;
    }

    // public static methods ------------------------------------------------------------------------------------------

    public static SdesChunk decode(ByteBuf buffer) {
        SdesChunk chunk = new SdesChunk();
        chunk.ssrc = buffer.readUnsignedInt();

        // Because some genious thought that 32bit alignment would be cool, we must count the amount of bytes remaining
        // after decoding each SdesChunkItem so that when we read the end/null item, we know how many more bytes we
        // must read to discard the padding bytes (hit the 32bit alignment barrier).
        int read = 0;
        for (;;) {
            if (buffer.readableBytes() == 0) {
                // Some implementations don't write the mandatory last item (end/null).
                return chunk;
            }
            int remaining = buffer.readableBytes();
            SdesChunkItem item = SdesChunkItems.decode(buffer);
            read += remaining - buffer.readableBytes();
            if (item.getType().equals(SdesChunkItem.Type.NULL)) {
                int paddingBytes = 4 - (read % 4);
                if (paddingBytes != 4) {
                    buffer.skipBytes(paddingBytes);
                }
                return chunk;
            }

            chunk.addItem(item);
        }
    }

    public static ByteBuf encode(SdesChunk chunk) {
        ByteBuf buffer;
        if (chunk.items == null) {
            // Allocate 8 bytes: 4 for ssrc, 1 for null item and other 3 null octets for 32 bit alignment
            buffer = Unpooled.buffer(8);
            buffer.writeInt((int) chunk.ssrc); // ssrc
            buffer.writeInt(0); // 4 null octets (1 for null item and 3 for 32bit alignment)
            return buffer;
        } else {
            // Start with SSRC
            int size = 4;
            // Add the length of each item
            List<ByteBuf> encodedChunkItems = new ArrayList<ByteBuf>(chunk.items.size());
            for (SdesChunkItem item : chunk.items) {
                ByteBuf encodedChunk = item.encode();
                encodedChunkItems.add(encodedChunk);
                size += encodedChunk.readableBytes();
            }
            // Add the null item
            size += 1;
            // Calculate padding and add it (for 32bit alignment).
            int padding = 4 - (size % 4);
            if (padding == 4) {
                padding = 0;
            }
            size += padding;

            // Write the buffer contents: SSRC, chunks, null item and padding
            buffer = Unpooled.buffer(size);
            buffer.writeInt((int) chunk.ssrc);
            for (ByteBuf encodedChunk : encodedChunkItems) {
                buffer.writeBytes(encodedChunk);
            }
            buffer.writeByte(0x00);
            for (int i = 0; i < padding; i++) {
                buffer.writeByte(0x00);
            }
        }

        return buffer;
    }

    // public methods -------------------------------------------------------------------------------------------------

    public ByteBuf encode() {
        return encode(this);
    }

    public boolean addItem(SdesChunkItem item) {
        if (item.getType() == SdesChunkItem.Type.NULL) {
            throw new IllegalArgumentException("You don't need to manually add the null/end element");
        }

        if (this.items == null) {
            this.items = new ArrayList<SdesChunkItem>();
        }

        return this.items.add(item);
    }

    public String getItemValue(SdesChunkItem.Type type) {
        if (this.items == null) {
            return null;
        }

        for (SdesChunkItem item : this.items) {
            if (item.getType() == type) {
                return item.getValue();
            }
        }

        return null;
    }

    // getters & setters ----------------------------------------------------------------------------------------------

    public long getSsrc() {
        return ssrc;
    }

    public void setSsrc(long ssrc) {
        if ((ssrc < 0) || (ssrc > 0xffffffffL)) {
            throw new IllegalArgumentException("Valid range for SSRC is [0;0xffffffff]");
        }
        this.ssrc = ssrc;
    }

    public List<SdesChunkItem> getItems() {
        if (this.items == null) {
            return null;
        }

        return Collections.unmodifiableList(this.items);
    }

    public void setItems(List<SdesChunkItem> items) {
        this.items = items;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder()
                .append("SdesChunk{")
                .append("ssrc=").append(this.ssrc)
                .append(", items=").append(this.items)
                .append('}').toString();
    }
}

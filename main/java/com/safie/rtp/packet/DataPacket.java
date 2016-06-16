
package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

/**
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |V=2|P|X|  CC   |M|     PT      |       sequence number         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                           timestamp                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |           synchronization source (SSRC) identifier            |
 * +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 * |            contributing source (CSRC) identifiers             |
 * |                             ....                              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 *  0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      defined by profile       |           length              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        header extension                       |
 * |                             ....                              |
 */
public class DataPacket extends Packet {

    // internal vars --------------------------------------------------------------------------------------------------

    private RtpVersion version;
    private boolean marker;
    private int payloadType;
    private int sequenceNumber;
    private long timestamp;
    private long ssrc;

    private short extensionHeaderData;
    private byte[] extensionData;

    private List<Long> contributingSourceIds;

    private ByteBuf data;

    // constructors ---------------------------------------------------------------------------------------------------

    public DataPacket() {
        this.version = RtpVersion.V2;
    }

    public DataPacket(ByteBuf data, long ssrc, int payloadType, long timestamp){
        this(data, ssrc, payloadType, timestamp, new ArrayList<Long>());
    }

    public DataPacket(ByteBuf data, long ssrc, int payloadType, long timestamp, List<Long> csrcs){
        this(data, ssrc, payloadType, timestamp, csrcs, false);
    }

    public DataPacket(ByteBuf data, long ssrc, int payloadType, long timestamp, List<Long> csrcs, boolean marker){
        this.version = RtpVersion.V2;
        this.marker = marker;
        this.payloadType = payloadType;
        this.timestamp = timestamp;
        this.ssrc = ssrc;
        this.contributingSourceIds = csrcs;
        this.data = data;
    }
    // public static methods ------------------------------------------------------------------------------------------

    public static DataPacket decode(byte[] data) {
        return decode(Unpooled.wrappedBuffer(data));
    }

    public static DataPacket decode(ByteBuf buffer) throws IndexOutOfBoundsException {
        if (buffer.readableBytes() < 12) {
            throw new IllegalArgumentException("A RTP packet must be at least 12 octets long");
        }

        // Version, Padding, eXtension, CSRC Count
        DataPacket packet = new DataPacket();
        byte b = buffer.readByte();
        packet.version = RtpVersion.fromByte(b);
        boolean padding = (b & 0x20) > 0; // mask 0010 0000
        boolean extension = (b & 0x10) > 0; // mask 0001 0000
        int contributingSourcesCount = b & 0x0f; // mask 0000 1111

        // Marker, Payload Type
        b = buffer.readByte();
        packet.marker = (b & 0x80) > 0; // mask 1000 0000
        packet.payloadType = (b & 0x7f); // mask 0111 1111

        packet.sequenceNumber = buffer.readUnsignedShort();
        packet.timestamp = buffer.readUnsignedInt();
        packet.ssrc = buffer.readUnsignedInt();

        // Read CCRC's
        if (contributingSourcesCount > 0) {
            packet.contributingSourceIds = new ArrayList<Long>(contributingSourcesCount);
            for (int i = 0; i < contributingSourcesCount; i++) {
                long contributingSource = buffer.readUnsignedInt();
                packet.contributingSourceIds.add(contributingSource);
            }
        }

        // Read extension headers & data
        if (extension) {
            packet.extensionHeaderData = buffer.readShort();
            packet.extensionData = new byte[buffer.readUnsignedShort() * 4];
            buffer.readBytes(packet.extensionData);
        }

        if (!padding) {
            // No padding used, assume remaining data is the packet
            byte[] remainingBytes = new byte[buffer.readableBytes()];
            buffer.readBytes(remainingBytes);
            packet.setData(remainingBytes);
        } else {
            // Padding bit was set, so last byte contains the number of padding octets that should be discarded.
            short lastByte = buffer.getUnsignedByte(buffer.readerIndex() + buffer.readableBytes() - 1);
            byte[] dataBytes = new byte[buffer.readableBytes() - lastByte];
            buffer.readBytes(dataBytes);
            packet.setData(dataBytes);
            // Discard rest of buffer.
            buffer.skipBytes(buffer.readableBytes());
        }

        return packet;
    }

    public static ByteBuf encode(int fixedBlockSize, DataPacket packet) {
        int size = 12; // Fixed width
        if (packet.hasExtension()) {
            size += 4 + packet.getExtensionDataSize();
        }
        size += packet.getContributingSourcesCount() * 4;
        size += packet.getDataSize();

        // If packet was configured to have padding (fixed block size), calculate padding and add it.
        int padding = 0;
        if (fixedBlockSize > 0) {
            // If padding modulus is > 0 then the padding is equal to:
            // (global size of the compound RTCP packet) mod (block size)
            // Block size alignment might be necessary for some encryption algorithms
            // RFC section 6.4.1
            padding = fixedBlockSize - (size % fixedBlockSize);
            if (padding == fixedBlockSize) {
                padding = 0;
            }
        }
        size += padding;

        ByteBuf out = Unpooled.buffer(size);

        // Version, Padding, eXtension, CSRC Count
        byte b = packet.getVersion().getByte();
        if (padding > 0) {
            b |= 0x20;
        }
        if (packet.hasExtension()) {
            b |= 0x10;
        }
        b |= packet.getContributingSourcesCount();
        out.writeByte(b);

        // Marker, Payload Type
        b = (byte) packet.getPayloadType();
        if (packet.hasMarker()) {
            b |= 0x80; // 1000 0000
        }
        out.writeByte(b);

        out.writeShort(packet.sequenceNumber);
        out.writeInt((int) packet.timestamp);
        out.writeInt((int) packet.ssrc);

        // Write CCRC's
        if (packet.getContributingSourcesCount() > 0) {
            for (Long contributingSourceId : packet.getContributingSourceIds()) {
                out.writeInt(contributingSourceId.intValue());
            }
        }

        // Write extension headers & data
        if (packet.hasExtension()) {
            out.writeShort(packet.extensionHeaderData);
            out.writeShort(packet.extensionData.length / 4);
            out.writeBytes(packet.extensionData);
        }

        // Write RTP data
        if (packet.data != null) {
            out.writeBytes(packet.data.array());
        }

        if (padding > 0) {
            // Final bytes: padding
            for (int i = 0; i < (padding - 1); i++) {
                out.writeByte(0x00);
            }

            // Final byte: the amount of padding bytes that should be discarded.
            // Unless something's wrong, it will be a multiple of 4.
            out.writeByte(padding);
        }

        return out;
    }

    // public methods -------------------------------------------------------------------------------------------------

    public ByteBuf encode(int fixedBlockSize) {
        return encode(fixedBlockSize, this);
    }

    public ByteBuf encode() {
        return encode(0, this);
    }

    public void addContributingSourceId(long contributingSourceId) {
        if (this.contributingSourceIds == null) {
            this.contributingSourceIds = new ArrayList<Long>();
        }

        this.contributingSourceIds.add(contributingSourceId);
    }

    public int getDataSize() {
        if (this.data == null) {
            return 0;
        }

        return this.data.capacity();
    }

    public int getExtensionDataSize() {
        if (this.extensionData == null) {
            return 0;
        }

        return this.extensionData.length;
    }

    public int getContributingSourcesCount() {
        if (this.contributingSourceIds == null) {
            return 0;
        }

        return this.contributingSourceIds.size();
    }

    public void setExtensionHeader(short extensionHeaderData, byte[] extensionData) {
        if (extensionData.length > 65536) {
            throw new IllegalArgumentException("Extension data cannot exceed 65536 bytes");
        }
        if ((extensionData.length % 4) != 0) {
            throw new IllegalArgumentException("Extension data must be one or more 32-bit words.");
        }
        this.extensionHeaderData = extensionHeaderData;
        this.extensionData = extensionData;
    }

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

    public boolean hasExtension() {
        return this.extensionData != null;
    }

    public boolean hasMarker() {
        return marker;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
    }

    public int getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(int payloadType) {
        if ((payloadType < 0) || (payloadType > 127)) {
            throw new IllegalArgumentException("PayloadType must be in range [0;127]");
        }
        this.payloadType = payloadType;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSsrc() {
        return ssrc;
    }

    public void setSsrc(long ssrc) {
        if ((ssrc < 0) || (ssrc > 0xffffffffL)) {
            throw new IllegalArgumentException("Valid range for SSRC is [0;0xffffffff]");
        }
        this.ssrc = ssrc;
    }

    public short getExtensionHeaderData() {
        return extensionHeaderData;
    }

    public byte[] getExtensionData() {
        return extensionData;
    }

    public List<Long> getContributingSourceIds() {
        return contributingSourceIds;
    }

    public void setContributingSourceIds(List<Long> contributingSourceIds) {
        this.contributingSourceIds = contributingSourceIds;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    public byte[] getDataAsArray() {
        return this.data.array();
    }

    public void setData(byte[] data) {
        this.data = Unpooled.wrappedBuffer(data);
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder()
                .append("DataPacket{V=").append(this.version)
                .append(", X=").append(this.hasExtension())
                .append(", CC=").append(this.getContributingSourcesCount())
                .append(", M=").append(this.marker)
                .append(", PT=").append(this.payloadType)
                .append(", SN=").append(this.sequenceNumber)
                .append(", TS=").append((int)this.timestamp)
                .append(", SSRC=").append(this.ssrc)
                .append(", CSRCs=").append(this.contributingSourceIds)
                .append(", data=").append(this.getDataSize()).append(" bytes}")
                .toString();
    }

    public String toBnrString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n0         1         2         3 \n");
        sb.append("01234567890123456789012345678901\n");
        ByteBuf bf = this.encode();
        try{
            while(true){
                String binaryStr = ("00000000000000000000000000000000" + Integer.toBinaryString(bf.readInt()));
                binaryStr = binaryStr.substring(binaryStr.length()-32);
                sb.append(binaryStr + "\n");
            }
        }catch(java.lang.IndexOutOfBoundsException e){
        }
        return sb.toString();
    }
}

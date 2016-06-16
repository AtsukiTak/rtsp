package com.safie.rtp.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ReceptionReport {

    // internal vars --------------------------------------------------------------------------------------------------

    private long ssrc;
    private short fractionLost;
    private int cumulativeNumberOfPacketsLost;
    private long extendedHighestSequenceNumberReceived;
    private long interArrivalJitter;
    private long lastSenderReport;
    private long delaySinceLastSenderReport;

    // constructors ---------------------------------------------------------------------------------------------------

    public ReceptionReport() {
    }

    // public static methods ------------------------------------------------------------------------------------------

    public static ByteBuf encode(ReceptionReport block) {
        ByteBuf buffer = Unpooled.buffer(24); // 4 + 1 + 3 + 4 + 4 + 4 + 4
        buffer.writeInt((int) block.ssrc);
        buffer.writeByte(block.fractionLost);
        buffer.writeMedium(block.cumulativeNumberOfPacketsLost);
        buffer.writeInt((int) block.extendedHighestSequenceNumberReceived);
        buffer.writeInt((int) block.interArrivalJitter);
        buffer.writeInt((int) block.lastSenderReport);
        buffer.writeInt((int) block.delaySinceLastSenderReport);
        return buffer;
    }

    public static ReceptionReport decode(ByteBuf buffer) {
        ReceptionReport block = new ReceptionReport();
        block.setSsrc(buffer.readUnsignedInt());
        block.setFractionLost(buffer.readUnsignedByte());
        block.setCumulativeNumberOfPacketsLost(buffer.readUnsignedMedium());
        block.setExtendedHighestSequenceNumberReceived(buffer.readUnsignedInt());
        block.setInterArrivalJitter(buffer.readUnsignedInt());
        block.setLastSenderReport(buffer.readUnsignedInt());
        block.setDelaySinceLastSenderReport(buffer.readUnsignedInt());
        return block;
    }

    // public methods -------------------------------------------------------------------------------------------------

    public ByteBuf encode() {
        return encode(this);
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

    public short getFractionLost() {
        return fractionLost;
    }

    public void setFractionLost(short fractionLost) {
        if ((fractionLost < 0) || (fractionLost > 0xffffffffL)) {
            throw new IllegalArgumentException("Valid range for Fraction Lost is [0;0xff]");
        }
        this.fractionLost = fractionLost;
    }

    public int getCumulativeNumberOfPacketsLost() {
        return cumulativeNumberOfPacketsLost;
    }

    public void setCumulativeNumberOfPacketsLost(int cumulativeNumberOfPacketsLost) {
        if ((cumulativeNumberOfPacketsLost < 0) || (cumulativeNumberOfPacketsLost > 0x00ffffff)) {
            throw new IllegalArgumentException("Valid range for Cumulative Number of Packets Lost is [0;0x00ffffff]");
        }
        this.cumulativeNumberOfPacketsLost = cumulativeNumberOfPacketsLost;
    }

    public long getExtendedHighestSequenceNumberReceived() {
        return extendedHighestSequenceNumberReceived;
    }

    public void setExtendedHighestSequenceNumberReceived(long extendedHighestSequenceNumberReceived) {
        if ((extendedHighestSequenceNumberReceived < 0) || (extendedHighestSequenceNumberReceived > 0xffffffffL)) {
            throw new IllegalArgumentException("Valid range for Extended Highest SeqNumber Received is [0;0xffffffff]");
        }
        this.extendedHighestSequenceNumberReceived = extendedHighestSequenceNumberReceived;
    }

    public long getInterArrivalJitter() {
        return interArrivalJitter;
    }

    public void setInterArrivalJitter(long interArrivalJitter) {
        if ((interArrivalJitter < 0) || (interArrivalJitter > 0xffffffffL)) {
            throw new IllegalArgumentException("Valid range for Interarrival Jitter is [0;0xffffffff]");
        }
        this.interArrivalJitter = interArrivalJitter;
    }

    public long getLastSenderReport() {
        return lastSenderReport;
    }

    public void setLastSenderReport(long lastSenderReport) {
        if ((lastSenderReport < 0) || (lastSenderReport > 0xffffffffL)) {
            throw new IllegalArgumentException("Valid range for Last Sender Report is [0;0xffffffff]");
        }
        this.lastSenderReport = lastSenderReport;
    }

    public long getDelaySinceLastSenderReport() {
        return delaySinceLastSenderReport;
    }

    public void setDelaySinceLastSenderReport(long delaySinceLastSenderReport) {
        if ((delaySinceLastSenderReport < 0) || (delaySinceLastSenderReport > 0xffffffffL)) {
            throw new IllegalArgumentException("Valid range for Delay Since Last Sender Report is [0;0xffffffff]");
        }
        this.delaySinceLastSenderReport = delaySinceLastSenderReport;
    }

    // low level overrides --------------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return new StringBuilder()
                .append("ReceptionReport{")
                .append("ssrc=").append(this.ssrc)
                .append(", fractionLost=").append(this.fractionLost)
                .append(", cumulativeNumberOfPacketsLost=").append(this.cumulativeNumberOfPacketsLost)
                .append(", extendedHighestSequenceNumberReceived=").append(this.extendedHighestSequenceNumberReceived)
                .append(", interArrivalJitter=").append(this.interArrivalJitter)
                .append(", lastSenderReport=").append(this.lastSenderReport)
                .append(", delaySinceLastSenderReport=").append(this.delaySinceLastSenderReport)
                .append('}').toString();
    }
}

package com.safie.rtp.packet;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a:mailto="bruno.carvalho@wit-software.com" />Bruno de Carvalho</a>
 */
public class CompoundControlPacket {

    // internal vars --------------------------------------------------------------------------------------------------

    private final List<ControlPacket> controlPackets;

    // constructors ---------------------------------------------------------------------------------------------------

    public CompoundControlPacket(ControlPacket... controlPackets) {
        if (controlPackets.length == 0) {
            throw new IllegalArgumentException("At least one RTCP packet must be provided");
        }
        this.controlPackets = Arrays.asList(controlPackets);
    }

    public CompoundControlPacket(List<ControlPacket> controlPackets) {
        if ((controlPackets == null) || controlPackets.isEmpty()) {
            throw new IllegalArgumentException("ControlPacket list cannot be null or empty");
        }
        this.controlPackets = controlPackets;
    }

    // public methods -------------------------------------------------------------------------------------------------

    public int getPacketCount() {
        return this.controlPackets.size();
    }

    // getters & setters ----------------------------------------------------------------------------------------------

    public List<ControlPacket> getControlPackets() {
        return this.controlPackets;
    }

    // low level overrides --------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CompoundControlPacket{\n");
        for (ControlPacket packet : this.controlPackets) {
            builder.append("  ").append(packet.toString()).append('\n');
        }
        return builder.append('}').toString();
    }
}

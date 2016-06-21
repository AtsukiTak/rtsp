package com.safie.rtp.player;

public interface RtpPacketSender<P extends Packet> {

    @FunctionalInterface
    public void send(P packet);

}

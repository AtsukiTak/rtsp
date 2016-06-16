package com.safie.rtp.participant;

import com.safie.rtp.packet.ControlPacket;

import io.netty.channel.Channel;

public class WithoutSsrcRtcpParticipant extends WithoutSsrcParticipant {

    public WithoutSsrcRtcpParticipant (Channel ch) {
        super(ch);
    }

    @Override
    public boolean sendControlPacket (ControlPacket packet) {
        this.channel.writeAndFlush(packet);
        return true;
    }
}

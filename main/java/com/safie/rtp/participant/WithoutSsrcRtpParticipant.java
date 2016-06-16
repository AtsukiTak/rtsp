package com.safie.rtp.participant;

import com.safie.rtp.packet.DataPacket;

import io.netty.channel.Channel;

public class WithoutSsrcRtpParticipant extends WithoutSsrcParticipant {

    public WithoutSsrcRtpParticipant (Channel ch) {
        super(ch);
    }

    @Override
    public boolean sendDataPacket (DataPacket packet) {
        this.channel.writeAndFlush(packet);
        return true;
    }
}

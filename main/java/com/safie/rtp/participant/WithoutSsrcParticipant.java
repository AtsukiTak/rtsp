package com.safie.rtp.participant;

import com.safie.rtp.packet.DataPacket;
import com.safie.rtp.packet.ControlPacket;

import io.netty.channel.Channel;

public class WithoutSsrcParticipant extends Participant {

    protected Channel channel;

    public WithoutSsrcParticipant (Channel ch) {
        this.channel = ch;
    }

    @Override
    public boolean sendDataPacket (DataPacket packet) {
        return false;
    }

    @Override
    public boolean sendControlPacket (ControlPacket packet) {
        return false;
    }
}

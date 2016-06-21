package com.safie.rtp.player;


public abstract class RtpPlayer implements RtpPlayerAction {

    abstract public void setSender(RtpPacketSender<DataPacket> sender);

}

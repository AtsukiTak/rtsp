package com.safie.rtp.media;

import com.safie.rtp.packet.*;
import com.safie.rtp.server.RtpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

abstract public class PacketGenerator {

    private Logger logger = LogManager.getLogger(PacketGenerator.class.getName());

    protected long timestampOffset;

    public PacketGenerator(){
        this.timestampOffset = new Random().nextLong();
    }

    public abstract boolean hasNext();

    public abstract DataPacket nextPacket();

    public abstract void moveHead();
}

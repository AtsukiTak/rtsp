package com.safie.rtp.media;

import com.safie.rtp.packet.*;
import com.safie.rtp.server.RtpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

abstract public class PacketGenerator {

    private Logger logger = LogManager.getLogger(PacketGenerator.class.getName());

    protected MediaGeneratedListener generatedListener;
    protected long timestampOffset;

    public PacketGenerator(){
        Random r = new Random();
        this.timestampOffset = (long)r.nextInt() + (long)Integer.MAX_VALUE / 2;
    }

    public void setGeneratedListener(MediaGeneratedListener listener){
        this.generatedListener = listener;
    }

    public abstract void start();

}

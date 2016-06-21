package com.safie.rtp.media;

import com.safie.rtp.packet.*;
import com.safie.rtp.server.RtpServer;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Audio implements Media {

    private Logger logger = LogManager.getLogger(Audio.class.getName());

    private static final int MP3 = 1;
    private static final int PCMU = 2;

    private int audioFormat = -1;
    private int payloadType;
    private int frameSize;
    private float frameRate;
    private int clockRate;
    private String encodeName;
    public AudioInputStream stream;

    public Audio (File file) throws UnsupportedAudioFileException, IOException{
        this.stream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = stream.getFormat();
        this.frameSize = format.getFrameSize();
        logger.debug("frame size : "+ this.frameSize);
        this.frameRate = format.getFrameRate();
        logger.debug("frame rate : "+ this.frameRate);

        String filename = file.getName();
        String expression = filename.substring(filename.lastIndexOf(".") + 1);
        logger.info("audio file's expression : "+ expression);
        if (expression.equals("mp3")) this.audioFormat = MP3;
        if (expression.equals("wav")) this.audioFormat = PCMU;

        switch(this.audioFormat){
            case MP3:
                this.payloadType = 96;
                this.clockRate = 90000;
                this.encodeName = "mpa-robust";
                logger.debug("generate MP3 Audio : "+ file.toString());
                break;
            case PCMU:
                this.payloadType = 0;
                this.clockRate = 8000;
                this.encodeName = "PCMU";
                logger.debug("generate PCMU Audio : "+file.toString());
                break;
            case AAC:
            default:
                logger.error("invalid audio format : "+ this.audioFormat);
                throw new IOException("invalid audio format");
        }
    }

    public int getPayloadType(){
        return this.payloadType;
    }

    public int getFrameSize(){
        return this.frameSize;
    }

    public float getFrameRate(){
        return this.frameRate;
    }

    public int getClockRate(){
        return this.clockRate;
    }
}

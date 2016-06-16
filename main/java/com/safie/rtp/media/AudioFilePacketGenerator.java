package com.safie.rtp.media;

import com.safie.rtp.server.RtpServer;
import com.safie.rtp.packet.DataPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioInputStream;

public class AudioFilePacketGenerator extends PacketGenerator {

    private Logger logger = LogManager.getLogger(AudioFilePacketGenerator.class.getName());

    private static int MAX_EXTERNAL_BUFFER_SIZE = 16 * 4;

    private long ssrc;
    private long timestamp;
    private Audio audio;
    private int bufferSize;
    
    public AudioFilePacketGenerator (long ssrc, Audio audio){
        this.ssrc = ssrc;
        this.audio = audio;
        this.timestamp = this.timestampOffset;
        this.bufferSize = MAX_EXTERNAL_BUFFER_SIZE / this.audio.getFrameSize() * this.audio.getFrameSize();
    }


    private void newPacketIsGenerated(DataPacket packet){
        if(this.generatedListener != null){
            this.generatedListener.whenGenerated(packet);
        }
    }

    @Override
    public void start(){
        logger.info("start to generate packet");
        while (true){
            DataPacket packet = generateFromInputStream(audio.stream);
            if (packet == null) {
                logger.info("finish to generate packet");
                break;
            }
            newPacketIsGenerated(packet);

            try{
                Thread.sleep(0);
            }catch(java.lang.InterruptedException e){
                logger.error(e);
            }
        }
    }

    private DataPacket generateFromInputStream(AudioInputStream stream){
        byte[] data = new byte[bufferSize];
        try{
            int readBytes = stream.read(data, 0, bufferSize);
            if (readBytes == -1) {
                logger.info("finish to read stream");
                return null;
            }
            int countOfFrame = readBytes / audio.getFrameSize();
            long timestampInc = (long)((double)audio.getClockRate() / (double)audio.getFrameRate() * (double)countOfFrame);
            this.timestamp += timestampInc;

            ByteBuf bytebuf = Unpooled.buffer(bufferSize+4);
            bytebuf.writeBytes(data);
            DataPacket packet = new DataPacket(bytebuf, ssrc, audio.getPayloadType(), timestamp);
            logger.debug("packet is generated !!");
            return packet;
        }catch(Throwable th){
            logger.info(th);
            return null;
        }
    }
}

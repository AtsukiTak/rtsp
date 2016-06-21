package com.safie.rtp.media;

import com.safie.rtp.server.RtpServer;
import com.safie.rtp.packet.DataPacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioInputStream;

public class AacFilePacketGenerator extends PacketGenerator {

    private Logger logger = LogManager.getLogger(AudioFilePacketGenerator.class.getName());

    private static int MAX_EXTERNAL_BUFFER_SIZE = 16 * 4;

    private long ssrc;
    private long timestamp;
    private Audio audio;
    private int bufferSize;
    
    public AacFilePacketGenerator (long ssrc, Audio audio){
        this.ssrc = ssrc;
        this.audio = audio;
        this.timestamp = this.timestampOffset;
        this.bufferSize = MAX_EXTERNAL_BUFFER_SIZE / this.audio.getFrameSize() * this.audio.getFrameSize();
    }

    @Override
    public boolean hasNext(){
        audio.strea.available() >= this.audio.getFrameSize();
    }

    @Override
    public DataPacket nextPacket(){
         return generateFromInputStream(audio.stream);
    }

    //FIXME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private DataPacket generateFromInputStream(AudioInputStream stream){
        byte[] data = new byte[bufferSize];
        try{
            int readBytes = stream.read(data, 0, bufferSize);
            if (readBytes < this.audio.getFrameSize()) {
                logger.info("finish to read stream");
                return null;
            }
            int countOfFrame = readBytes / audio.getFrameSize();
            long timestampInc = (long)((double)audio.getClockRate() / audio.getFrameRate() * countOfFrame);
            this.timestamp += timestampInc;

            ByteBuf bytebuf = Unpooled.buffer(bufferSize);
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

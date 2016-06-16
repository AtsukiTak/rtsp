package com.safie.rtp.packet;

import junit.framework.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;

public class DataPacketTest extends TestCase {

    protected Logger logger = LogManager.getLogger(DataPacketTest.class);

    private DataPacket packet;

    public DataPacketTest (String name){
        super(name);
    }

    protected void setUp(){
        ByteBuf buf = Unpooled.buffer(8);
        buf.writeLong(1024l);
        DataPacket packet = new DataPacket(buf, 1000l, 0, 5000l, new ArrayList<Long>(), false);
        packet.setSequenceNumber(777);
        this.packet = packet;
    }

    public void testEncodeDecode(){
        ByteBuf encodedBuf = this.packet.encode();
        DataPacket decodedPacket = DataPacket.decode(encodedBuf);
        assertEquals("version", RtpVersion.V2, decodedPacket.getVersion());
        assertEquals("extension", false, decodedPacket.hasExtension());
        assertEquals("marker", false, decodedPacket.hasMarker());
        assertEquals("payloadType", 0, decodedPacket.getPayloadType());
        assertEquals("sequence number", 777, decodedPacket.getSequenceNumber());
        assertEquals("timestamp", 5000l, decodedPacket.getTimestamp());
        assertEquals("ssrc", 1000l, decodedPacket.getSsrc());
        assertEquals("extension header", 0, decodedPacket.getExtensionHeaderData());
        //assertEquals("csrc", new ArrayList<Long>(), decodedPacket.getContributingSourceIds());
        assertEquals("data", 1024l, decodedPacket.getData().readLong());
        assertEquals("compare binary", this.packet.toBnrString(), decodedPacket.toBnrString());
        logger.debug("original packet" + this.packet.toBnrString() + "\nencode decode packet" + decodedPacket.toBnrString());
    }

    public void testBinary(){
        logger.debug("origin packet" + this.packet.toBnrString());
        this.packet.setTimestamp(20000l);
        logger.debug("change timestamp" + this.packet.toBnrString());
    }

}

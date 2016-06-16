package com.safie.rtsp;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import com.safie.rtsp.core.*;
import com.safie.rtsp.util.GeneralUtil;
import com.safie.rtp.server.*;
import com.safie.rtp.session.*;
import com.safie.rtp.media.*;
import com.safie.rtp.packet.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {
  private static Logger logger = LogManager.getLogger(Main.class);

  private static final String DEFAULT_IP = "localhost";
  private static final Integer DEFAULT_PORT = 8070;

  public static void main(String[] args) {

    // ipとportの初期化
    String ip = DEFAULT_IP;
    Integer port = DEFAULT_PORT;
    switch(args.length){
      case 1:
        ip = args[0];
        break;
      case 2:
        ip = args[0];
        port = GeneralUtil.stringToIntegerWithDafault(args[1], DEFAULT_PORT);
        break;
    }
    
    Set<Integer> payloadTypes = new HashSet(1);
    payloadTypes.add(96);
    payloadTypes.add(0);
    RtpServer rtpServer = new SimpleRtpServer();
    rtpServer.ssrc = 1024;
    final RtpSession rtpSession = new RtpSession(rtpServer, payloadTypes);
    RtcpSession rtcpSession = new RtcpSession(rtpServer);

    RtspServer server = new RtspServer(ip, port, rtpSession, rtcpSession);
    //RtspController rtspController = new RtspController(ip, port, session);
    try {
      //rtspController.start();
      server.run();
      
      Thread.sleep(1000*20);
      String filepath = "/Users/atsuki/Downloads/jazz_02.wav";
        File aFile = new File(filepath);
        int counter = 0;
        while(counter < 100){
            Audio audio = new Audio(aFile);
            logger.debug("audio stream frame size : "+ String.valueOf(audio.stream.available()));
            PacketGenerator generator = new AudioFilePacketGenerator(rtpServer.ssrc, audio);
            generator.setGeneratedListener(new MediaGeneratedListener(){
                @Override
                public void whenGenerated(DataPacket packet){
                    rtpSession.sendPacket(packet);
                }
            });
            generator.start();
            counter += 1;
            Thread.sleep(1);
        }
    }catch(IOException e){
        logger.error(e);
        return;
    }catch(UnsupportedAudioFileException e){
        logger.error(e);
        return;
    }catch(java.lang.InterruptedException e){
        logger.error(e);
        return;
    } catch (Exception e) {
      // logger.error(e.getMessage(), e);
    }
  }
}

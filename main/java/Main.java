

import com.safie.rtp.server.*;
import com.safie.rtp.session.*;
import com.safie.rtp.media.*;
import com.safie.rtp.packet.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {

    private static Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main (String[] args){
        String host = "localhost";
        SocketAddress dataAddress = new InetSocketAddress(host, 8080);
        SocketAddress controlAddress = new InetSocketAddress(host, 8081);
        Set<Integer> payloadTypes = new HashSet(1);
        payloadTypes.add(0);
        RtpServer server = new SimpleRtpServer();
        server.ssrc = 1024;
        final RtpSession rtpSession = new RtpSession(server, dataAddress, payloadTypes);
        final RtcpSession rtcpSession = new RtcpSession(server, controlAddress);
        RtpSessionManager sessionManager = new RtpSessionManager(rtpSession, rtcpSession);
        if (sessionManager.build() == false) return;

        String filepath = "/Users/atsuki/Downloads/jazz_02.wav";
        File aFile = new File(filepath);
        try{
            int counter = 0;
            while(counter < 100){
                Audio audio = new Audio(aFile);
                logger.debug("audio stream frame size : "+ String.valueOf(audio.stream.available()));
                PacketGenerator generator = new AudioFilePacketGenerator(server.ssrc, audio);
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
        }
        return;
    }
}

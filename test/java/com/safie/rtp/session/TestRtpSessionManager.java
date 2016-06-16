package com.safie.rtp.session;

import com.safie.rtp.server.*;

import junit.framework.*;

import java.net.InetSocketAddress;
import java.util.HashSet;

public class TestRtpSessionManager extends TestCase {

    public TestRtpSessionManager(String name){
        super(name);
    }

    public void testBuild(){
        InetSocketAddress da = new InetSocketAddress("localhost", 8080);
        InetSocketAddress ca = new InetSocketAddress("localhost", 8081);
        RtpServer server = new RtpServer();
        RtpSessionManager manager = new RtpSessionManager(server, da, ca, new HashSet());
        assertTrue(manager.build());
        manager.terminate();
    }
}

package com.safie.rtsp.session;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Set;

public class RtspSession {

    private Logger logger = LogManager.getLogger(RtspSession.class);

    private String id;
    private String transport;

    public RtspSession(String id){
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setTransport(String transport){
        this.transport = transport;
    }

    public void destroy(){
        logger.debug("session which id is "+id.toString()+" is destoroied");
    }
}

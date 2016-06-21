package com.safie.rtsp.session;

import com.safie.expiringmap.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SessionDatabase{

    private Logger logger = LogManager.getLogger(SessionDatabase.class);

    private Map<int, RtspSession> map;

    public SessionDatabase(){
        this.map = ExpiringMap.builder()
        .expirationPolicy(ExpirationPolicy.ACCESSED)
        .expiration(5, TimeUnit.MINUTES)
        .expirationListener(new ExpirationListener<String, RtspSession>(){
            @Override
            public void expired(String key, RtspSession session){
                this.expired(key, session);
            }
        })
        .build();

    }

    public RtspSession get(int key){
        return this.map.get(key);
    }

    public void put(int key, RtspSession session){
        this.map.put(key, session);
    }

    private void expired(int key, RtspSession session){
        session.destroy();
    }
}

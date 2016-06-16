package com.safie.rtp.session;


public RtpFileSrcPlayer extends RtpPlayer{

    private final RtpFileSrcPlayerConfig config;
    private final RtpSession session;

    public RtpFileSrcPlayer(RtpFileSrcPlayerConfig config, RtpSession rtpSession){
        this.config = config;
        this.rtpSession = rtpSession;
    }


    @Override
    public void start(){
        String filepath = config.filepath;

    }

    @Override
    public void play(){
    }

    @Override
    public void pause(){
    }

    @Override
    public void terminate(){
    }

}

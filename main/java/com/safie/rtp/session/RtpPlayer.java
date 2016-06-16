package com.safie.rtp.session;

public abstract class RtpPlayer {


    public static RtpPlayer newPlayer(RtspConfig config){
        switch(config.rtpPlayerType){
            case FILEPLAYER:
                return new RtpFileSrcPlayer(config);
                break;
            case LIVEPLAYER:
                return new RtpLiveSrcPlayer(config);
                break;
            default:
                throw new IllegalArgumentException("invalid player type : "+config.rtpPlayerType);
                break;
        }
    }

    public abstract void setSession(RtpSession session);

    public abstract void start();

    public abstract void play();

    public abstract void pause();

    public abstract void terminate();
}

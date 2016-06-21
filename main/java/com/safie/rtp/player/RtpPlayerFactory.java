package com.safie.rtp.player;

public class RtpPlayerFactory{

    public static RtpPlayer generate(RtpConfig config){
        if (config instanceof RtpFileSrcPlayerConfig){
            return new RtpFileSrcPlayer((RtpFileSrcPlayerConfig) config);
        }else if(config instanceof RtpLIveSrcPlayerConfig){
            return new RtpLiveSRcPlayer((RtpLiveSrcPlayerConfig) config);
        }else{
            throw new IllegalArgumentException("invalid config class : "+config.class.getName());
        }
    }
}

package com.safie.rtp.player;


public RtpFileSrcPlayer extends RtpPlayer implements Runnable{

    private final RtpFileSrcPlayerConfig config;
    private final RtpPacketSender<DataPacket> sender;

    private final PacketGenerator packetGenerator;
    private AtomicBoolean loopStopFlag;

    public RtpFileSrcPlayer(RtpFileSrcPlayerConfig config){
        this(config, false);
    }

    public RtpFileSrcPlayer(RtpFileSrcPlayerConfig config, boolean isOneTime){
        this.config = config;
        this.packetGenerator = PacketGeneratorFactory(config);
        this.loopStopFlag = new AtomicBoolean(true);
        this.isOneTimeOnlyPlay = isOneTime;
    }

    @Override
    public void setSender(RtpPacketSender<DataPacket> sender){
        this.sender = sender;
    }


    @Override
    public void play(){
        Thread loopThread = new Thread(new Runnable(){
            @Override
            public void run(){
                while(loopStopFlag.get() == false){
                    playWork();
                }
            }
        });
        this.loopStopFlag.set(false);
        loopThread.start();
    }

    public void playWork(){
        if (!generator.hasNext()){
            if (config.isOneTimePlay) {
                this.loopStopFlag.set(true);
                return;
            }
            else generator.reset();
        }
        sender.send(generator.nextPacket());
    }

    @Override
    public void pause(){
        this.loopStopFlag.set(true); //これによって、ループスレッドは破棄される
    }

    @Override
    public void terminate(){
    }

}

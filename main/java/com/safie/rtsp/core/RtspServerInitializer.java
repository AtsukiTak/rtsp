package com.safie.rtsp.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.rtsp.RtspRequestDecoder;
import io.netty.handler.codec.rtsp.RtspResponseEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RtspServerInitializer {

  private final Logger logger = LogManager.getLogger(RtspServerInitializer.class);

  public final RtspRequestReceiver receiver;

  protected RtspServerInitializer(RtspRequestReceiver receiver) {
    this.receiver = receiver;
  }

  public ChannelInitializer<SocketChannel> get() throws Exception {
    return new ChannelInitializer<SocketChannel>() {
      @Override
      public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new RtspRequestDecoder());
        pipeline.addLast("encoder", new RtspResponseEncoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("receiver", receiver);

        logger.info("new RTSP participant joined");
      }
    };
  }
}

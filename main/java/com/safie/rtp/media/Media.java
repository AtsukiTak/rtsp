package com.safie.rtp.media;


public interface Media {

    int getPayloadType();
    int getFrameSize();
    float getFrameRate();
    int getClockRate();
}

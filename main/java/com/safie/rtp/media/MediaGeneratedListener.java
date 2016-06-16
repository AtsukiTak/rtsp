package com.safie.rtp.media;

import com.safie.rtp.packet.*;

public interface MediaGeneratedListener {
    public void whenGenerated(DataPacket packet);
}

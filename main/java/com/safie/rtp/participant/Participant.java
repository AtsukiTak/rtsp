package com.safie.rtp.participant;

import com.safie.rtp.packet.DataPacket;
import com.safie.rtp.packet.ControlPacket;

//----------Obrigation--------------------
// 参加者へのデータ送信の方法を知っている
//----------------------------------------


public abstract class Participant {

    public abstract boolean sendDataPacket(DataPacket packet);

    public abstract boolean sendControlPacket(ControlPacket packet);
}

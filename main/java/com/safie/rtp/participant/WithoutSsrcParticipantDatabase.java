package com.safie.rtp.participant;

import java.util.Set;
import java.util.HashSet;

public class WithoutSsrcParticipantDatabase extends ParticipantDatabase {

    protected Set<Participant> rtpParticipants = new HashSet();
    protected Set<Participant> rtcpParticipants = new HashSet();

    @Override
    public boolean addRtpParticipant (Participant participant) {
        if (participant instanceof WithoutSsrcRtpParticipant){
            this.rtpParticipants.add((WithoutSsrcRtpParticipant)participant);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean addRtcpParticipant (Participant participant) {
        if (participant instanceof WithoutSsrcRtcpParticipant){
            this.rtcpParticipants.add((WithoutSsrcRtcpParticipant)participant);
            return true;
        }else{
            return false;
        }
    }


    @Override
    public Set<Participant> getRtpParticipants() {
        return this.rtpParticipants;
    }

    @Override
    public Set<Participant> getRtcpParticipants() {
        return this.rtcpParticipants;
    }
}


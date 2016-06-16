package com.safie.rtp.participant;

import java.util.Set;

public abstract class ParticipantDatabase {


    public abstract boolean addRtpParticipant (Participant participant);

    public abstract boolean addRtcpParticipant (Participant participant);

    public abstract Set<Participant> getRtpParticipants();

    public abstract Set<Participant> getRtcpParticipants();

}

package com.safie.rtsp.session;

//import org.apache.commons.lang.RandomStringUtils;
import java.lang.Math;

public class SimpleRandomKeyFactory implements RtspSessionKeyFactory {

  private static final int HEADER_COUNT = 8;
  private static final int FOOTER_COUNT = 10;
  private static final String DELIMITER = "-";

  @Override
  public synchronized String createSessionKey() {
    String header = String.valueOf((int)(Math.random()*(Math.pow(10, HEADER_COUNT - 1))));
    String footer = String.valueOf((int)(Math.random()*(Math.pow(10, FOOTER_COUNT - 1))));
    return header + DELIMITER + footer;
  }
}

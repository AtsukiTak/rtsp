package com.safie.rtsp.util;


public class GeneralUtil {

  public static int stringToIntegerWithDafault(String row, int defNum){
    int val = defNum;
    try {
      val = Integer.parseInt(row);
    } catch(java.lang.NumberFormatException e) {
      val = defNum;
    }
    return val;
  }
}

package crm.chifco.com.controller;

import java.util.Arrays;

public class Utils {
  public static int[] merge(int[]... intarrays) {
    return Arrays.stream(intarrays).flatMapToInt(Arrays::stream).toArray();
  }
}

package com.kanomiya.hinaoutlineprocessor.util;

/**
 * Created by Kanomiya in 2017/02.
 */
public class StringUtils {
    public static final String RN_WIN = "\\r\\n";
    public static final String N_UNIX = "\\n";
    public static final String NR = "\\n\\r";
    public static final String R = "\\r";

    public static String lineSeparator(String text) {
        return text.contains(RN_WIN) ? RN_WIN :
                (text.contains(NR) ? NR :
                        (text.contains(N_UNIX) ? N_UNIX :
                                (text.contains(R) ? R :
                                    System.lineSeparator())));
    }


}

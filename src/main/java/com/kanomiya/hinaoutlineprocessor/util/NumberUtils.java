package com.kanomiya.hinaoutlineprocessor.util;

/**
 * Created by Kanomiya in 2017/02.
 */
public class NumberUtils {

    public static Integer asIntegerOrNull(String string)
    {
        return asIntegerOrDefault(string, null);
    }

    public static Integer asIntegerOrDefault(String string, Integer defaultValue) {
        try
        {
            return Integer.valueOf(string);
        } catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }



}

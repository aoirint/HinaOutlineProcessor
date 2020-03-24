package com.kanomiya.hinaoutlineprocessor.text;

import com.kanomiya.hinaoutlineprocessor.util.NumberUtils;

/**
 * Created by Kanomiya in 2017/02.
 */
public class IntegerDocumentFilter extends AbstractDocumentFilter
{
    public static IntegerDocumentFilter min(int minValue) {
        return range(minValue, Integer.MAX_VALUE);
    }

    public static IntegerDocumentFilter max(int maxValue) {
        return range(Integer.MIN_VALUE, maxValue);
    }

    public static IntegerDocumentFilter range(int minValue, int maxValue) {
        return new IntegerDocumentFilter(minValue, maxValue);
    }

    public static IntegerDocumentFilter allRange() {
        return range(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }



    public final int minValue, maxValue;

    private IntegerDocumentFilter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }


    @Override
    public boolean verify(String string)
    {
        Integer i = NumberUtils.asIntegerOrNull(string);
        return i != null && minValue <= i && i <= maxValue && (string.charAt(0) != '0' || string.length() == 1);
    }

}

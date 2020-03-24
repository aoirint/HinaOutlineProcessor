package com.kanomiya.hinaoutlineprocessor.structure.address;

import com.kanomiya.hinaoutlineprocessor.structure.HOPNode;

import javax.swing.tree.TreeNode;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPAddressHelper
{
    public static Integer[] parseAddressText(String addressText)
    {
        return Arrays.stream(addressText.split(",")).map(Integer::valueOf).toArray(Integer[]::new);
    }

    public static Integer[] parseNodeAddress(HOPNode nodeObj) {

        IntStream.Builder addressBuilder = IntStream.builder();
        {
            TreeNode parent = nodeObj;
            while (parent.getParent() != null)
            {
                addressBuilder.accept(parent.getParent().getIndex(parent) +1);
                parent = parent.getParent();
            }
        }

        int[] reversed = addressBuilder.build().toArray();
        Integer[] address = new Integer[reversed.length];
        for (int i=0, len=reversed.length; i<len; i++)
            address[i] = reversed[len -i -1];

        return address;
    }

    public static String stringifyNodeAddress(HOPNode nodeObj) {
        return stringifyAddressArray(parseNodeAddress(nodeObj));
    }

    public static String stringifyAddressArray(Integer[] address) {
        int depth = address.length;
        if (depth == 0) return "";

        StringBuilder builder = new StringBuilder();
        for (int i=0; i<depth; i++) builder.append(',').append(address[i]);

        return builder.substring(1);
    }


}

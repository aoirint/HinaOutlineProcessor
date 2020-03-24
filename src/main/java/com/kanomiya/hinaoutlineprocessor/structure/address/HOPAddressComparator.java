package com.kanomiya.hinaoutlineprocessor.structure.address;

import java.util.Comparator;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPAddressComparator implements Comparator<Integer[]>
{
    @Override
    public int compare(Integer[] a, Integer[] b)
    {
        int a_depth = a.length;
        int b_depth = b.length;

        int minDepth = Math.min(a_depth, b_depth);
        for (int i=0; i<minDepth; i++)
            if (a[i] == b[i]) continue;
            else return a[i] < b[i] ? -1 : 1;

        return a_depth < b_depth ? -1 : (a_depth == b_depth ? 0 : 1);
    }

}

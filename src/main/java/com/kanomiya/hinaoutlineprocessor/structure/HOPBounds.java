package com.kanomiya.hinaoutlineprocessor.structure;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPBounds
{
    public static final HOPBounds DEFAULT = new HOPBounds(100, 580, 100, 740);

    public int top;
    public int bottom;
    public int left;
    public int right;

    public HOPBounds(int top, int bottom, int left, int right)
    {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public int getX() {
        return left;
    }

    public int getY() {
        return top;
    }

    public int getWidth() {
        return right -left +1;
    }

    public int getHeight() {
        return bottom -top +1;
    }

}

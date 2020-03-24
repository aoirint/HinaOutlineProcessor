package com.kanomiya.hinaoutlineprocessor.structure;

import java.awt.*;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.colorCode;

/**
 * Created by Kanomiya in 2017/01.
 */
public class HOPNodeMarker
{
    public static final Color DEFAULT_COLOR_RED = Color.getHSBColor(0, 167 /255f, 146 /255f);
    public static final Color DEFAULT_COLOR_YELLOW = Color.getHSBColor(50 /360f, 167 /255f, 146 /255f);
    public static final Color DEFAULT_COLOR_GREEN = Color.getHSBColor(100 /360f, 167 /255f, 146 /255f);
    public static final Color DEFAULT_COLOR_BLUE = Color.getHSBColor(200 /360f, 167 /255f, 146 /255f);
    public static final Color DEFAULT_COLOR_PURPLE = Color.getHSBColor(300 /360f, 167 /255f, 146 /255f);

    public HOPNodeMarkerType type;
    public Color color;

    public HOPNodeMarker(HOPNodeMarkerType type, Color color)
    {
        this.type = type;
        this.color = color;
    }

    public static HOPNodeMarker parse(String markerText) {
        String[] args = markerText.split(" ");

        String typeText = args.length > 0 ? args[0] : "diamond";
        HOPNodeMarkerType type = HOPNodeMarkerType.find(typeText, HOPNodeMarkerType.DIAMOND);

        Color color = args.length > 1 ? Color.decode(args[1]) : DEFAULT_COLOR_GREEN;

        return new HOPNodeMarker(type, color);
    }

    public static HOPNodeMarker getDefaultMarker(boolean directlyUnderRoot) {
        return directlyUnderRoot ? new HOPNodeMarker(HOPNodeMarkerType.SQUARE, HOPNodeMarker.DEFAULT_COLOR_GREEN) : new HOPNodeMarker(HOPNodeMarkerType.DIAMOND, HOPNodeMarker.DEFAULT_COLOR_BLUE);
    }

    public HOPNodeMarker clone() {
        return new HOPNodeMarker(type, color);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type.markerName);
        builder.append(' ').append(colorCode(color));

        return builder.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HOPNodeMarker that = (HOPNodeMarker) o;

        if (type != that.type) return false;
        return color.equals(that.color);
    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + color.hashCode();
        return result;
    }
}

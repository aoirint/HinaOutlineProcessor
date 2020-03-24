package com.kanomiya.hinaoutlineprocessor.structure;

/**
 * Created by Kanomiya in 2017/02.
 */
public enum HOPNodeMarkerType
{
    DIAMOND("diamond", "diamond.png"),
    SQUARE("square", "square.png"),
    DISC("disc", "disc.png"),
    RIGHT_ARROW("rightArrow", "rightArrow.png"),
    ;

    public final String markerName;
    public final String fileName;

    HOPNodeMarkerType(String markerName, String fileName) {
        this.markerName = markerName;
        this.fileName = fileName;
    }

    public static HOPNodeMarkerType find(String markerName, HOPNodeMarkerType defaultValue) {
        for (HOPNodeMarkerType value: values()) {
            if (value.markerName.equals(markerName)) return value;
        }

        return defaultValue;
    }

}

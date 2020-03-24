package com.kanomiya.hinaoutlineprocessor.structure;

/**
 * Created by Kanomiya in 2017/02.
 */
public enum HOPMode {
    VIEW("View"),
    EDIT("Edit"),
    ;

    public static HOPMode find(String name) {
        for (HOPMode mode: values())
            if (mode.name.equals(name))
                return mode;

        return null;
    }

    public final String name;
    HOPMode(String name) {
        this.name = name;
    }
}

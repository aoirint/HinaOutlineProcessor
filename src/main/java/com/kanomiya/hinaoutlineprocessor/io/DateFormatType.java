package com.kanomiya.hinaoutlineprocessor.io;

import java.time.format.DateTimeFormatter;

public enum DateFormatType {
    ZONED_DATE_TIME("ZonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME),
    DATE("Date", DateTimeFormatter.ISO_DATE),
    ;

    public static DateFormatType find(String name) {
        for (DateFormatType type: values())
            if (type.name.equals(name))
                return type;
        return null;
    }

    public final String name;
    public final DateTimeFormatter formatter;

    DateFormatType(String name, DateTimeFormatter formatter) {
        this.name = name;
        this.formatter = formatter;
    }

    @Override
    public String toString() {
        return name;
    }
    }
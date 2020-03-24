package com.kanomiya.hinaoutlineprocessor;

/**
 * Created by Kanomiya in 2017/02.
 */
public class CommandException extends Exception {
    public enum Cause {
        UNKNOWN,
        INVALID_ARGUMENTS,

    }


    public CommandException(Cause cause) {
        super(cause.toString());
    }

    public CommandException(Cause cause, Throwable e) {
        super(cause.toString(), e);
    }

    public CommandException(Throwable e) {
        this(Cause.UNKNOWN, e);
    }

}

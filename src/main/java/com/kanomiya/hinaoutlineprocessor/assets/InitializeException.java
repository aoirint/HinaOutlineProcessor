package com.kanomiya.hinaoutlineprocessor.assets;

/**
 * Created by Kanomiya in 2017/02.
 */
public class InitializeException extends Exception {
    public enum Cause {
        UNKNOWN,
        ILLEGAL_SETTINGS,
        ILLEGAL_ASSETS,
        ILLEGAL_ASSETS_LANGUAGES,
        ILLEGAL_ASSETS_APP_ICON,
        ILLEGAL_ASSETS_MARKER_ICONS,
        ILLEGAL_ASSETS_FLAG_ICONS,
        ILLEGAL_ASSETS_MENU_DOCUMENT,
        ILLEGAL_ASSETS_THEME_DOCUMENT,

    }

    public InitializeException(Cause cause) {
        super(cause.toString());
    }

    public InitializeException(Cause cause, Throwable e) {
        super(cause.toString(), e);
    }

    public InitializeException(Throwable e) {
        this(Cause.UNKNOWN, e);
    }
}

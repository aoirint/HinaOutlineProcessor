package com.kanomiya.hinaoutlineprocessor.io;

import com.kanomiya.hinaoutlineprocessor.structure.HOPDocument;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by Kanomiya in 2017/02.
 */
public abstract class HOPDocumentIOFormat extends FileFilter
{
    /**
     *
     * フォーマットを特定するすべて小文字の文字列
     *
     * @return
     */
    public abstract String getName();
    public abstract String getRecommendExtension();

    public abstract String format(HOPDocument document) throws FormatException;
    public abstract HOPDocument parse(String text) throws FormatException;

    @Override
    public boolean accept(File f)
    {
        return f.isDirectory() || f.getName().endsWith("." + getRecommendExtension());
    }


    public static class FormatException extends Exception {
        public enum Cause {
            UNKNOWN,
            LACK_OF_NODETREE,

        }

        public FormatException(Cause cause) {
            super("Cause: " + cause.toString());
        }

        public FormatException(Exception e) {
            super(e);
        }
    }

}

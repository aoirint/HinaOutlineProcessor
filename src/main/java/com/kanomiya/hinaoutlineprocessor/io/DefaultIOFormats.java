package com.kanomiya.hinaoutlineprocessor.io;

import com.kanomiya.hinaoutlineprocessor.util.FileUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kanomiya in 2017/02.
 */
public class DefaultIOFormats {

    public static HOPDocumentIOFormat find(Path path) throws UnknownFormatException {
        String filename = path.getFileName().toString();
        String ext = FileUtils.extension(filename).toLowerCase();

        if (HOPD.getRecommendExtension().equals(ext)) {
            return HOPD;
        }
        else if (OPML.getRecommendExtension().equals(ext)) {
            return OPML;
        }
        else if (WZMEMO.getRecommendExtension().equals(ext)) {
            return WZMEMO;
        }

        throw new UnknownFormatException();
    }

    public static HOPDocumentIOFormat find(String name) throws UnknownFormatException {
        name = name.toLowerCase();

        for (HOPDocumentIOFormat format: list())
            if (name.equals(format.getName())) return format;

        throw new UnknownFormatException();
    }

    private static List<HOPDocumentIOFormat> LIST_CACHE;
    public static List<HOPDocumentIOFormat> list() {
        if (LIST_CACHE != null) return LIST_CACHE;
        return LIST_CACHE = Arrays.asList(HOPD, OPML, WZMEMO);
    }

    public static final IOFormatHOPD HOPD = new IOFormatHOPD();
    public static final IOFormatOPML OPML = new IOFormatOPML();
    public static final IOFormatWzMemoAndText WZMEMO = new IOFormatWzMemoAndText();

}

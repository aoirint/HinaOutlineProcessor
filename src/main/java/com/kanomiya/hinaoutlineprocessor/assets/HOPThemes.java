package com.kanomiya.hinaoutlineprocessor.assets;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPThemes {
    public static final Path BUILTIN_DIR = Paths.get("assets", "themes");
    public static final Path USER_DIR = Paths.get("themes");

    public final Map<String, HOPTheme> themeMap;

    public HOPThemes(Map<String, HOPTheme> themeMap) {
        this.themeMap = themeMap;
    }

    public HOPTheme find(String id) {
        return themeMap.get(id);
    }

    public Collection<HOPTheme> list() {
        return themeMap.values();
    }

    public void saveAll() throws ParserConfigurationException, IOException, TransformerException {
        for (HOPTheme theme: list()) {
            theme.saveToFile();
        }
    }

}

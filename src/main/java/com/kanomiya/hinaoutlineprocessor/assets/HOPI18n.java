package com.kanomiya.hinaoutlineprocessor.assets;

import com.kanomiya.hinaoutlineprocessor.assets.langdoc.Language;

import java.util.*;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPI18n {
    private final Map<String, Language> languageMap;

    public HOPI18n(Map<String, Language> languageMap) {
        this.languageMap = languageMap;
    }

    public String localize(String localeCode, String id, Object... args) {
        if (! languageMap.containsKey(localeCode))
            return id;

        return languageMap.get(localeCode).localize(id, args);
    }

    public Collection<Language> list() {
        return languageMap.values();
    }

    public Language find(String localeCode) {
        return languageMap.get(localeCode);
    }

}

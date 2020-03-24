package com.kanomiya.hinaoutlineprocessor.assets.langdoc;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kanomiya in 2017/02.
 */
public class Language
{
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("%(\\d+)");

    public String localeCode;
    public String name;
    public String flagFileName;
    public Map<String, String> localizeMap;

    public Language(String localeCode, String name, String flagFileName, Map<String, String> localizeMap)
    {
        this.localeCode = localeCode;
        this.name = name;
        this.flagFileName = flagFileName;
        this.localizeMap = localizeMap;
    }

    public String localize(String id, Object... args) {
        if (! localizeMap.containsKey(id)) return id;

        String localized = localizeMap.get(id);
        Matcher paramMatcher = PARAMETER_PATTERN.matcher(localized);
        if (! paramMatcher.find())
            return localized;

        paramMatcher.reset();

        StringBuffer buffer = new StringBuffer();

        while (paramMatcher.find()) {
            int index = Integer.valueOf(paramMatcher.group(1)) -1;
            paramMatcher.appendReplacement(buffer, String.valueOf(args[index]));
        }
        paramMatcher.appendTail(buffer);

        return buffer.toString();
    }


}

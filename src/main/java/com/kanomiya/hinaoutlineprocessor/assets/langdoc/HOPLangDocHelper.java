package com.kanomiya.hinaoutlineprocessor.assets.langdoc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPLangDocHelper
{
    public static Language parseLanguage(Path langDocPath) throws ParserConfigurationException, IOException, SAXException
    {
        Map<String, String> localizeMap = new HashMap<>();

        try (InputStream is = Files.newInputStream(langDocPath)) {
            Document langDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            Element rootNode = langDoc.getDocumentElement();

            String localeCode = rootNode.getAttribute("lang");
            String name = rootNode.getAttribute("name");
            String flagFileName = rootNode.getAttribute("flag");

            NodeList nodeList = rootNode.getElementsByTagName("localize");
            for (int i=0, len=nodeList.getLength(); i<len; i++) {
                Element node = (Element) nodeList.item(i);

                String id = node.getAttribute("id");
                String localized = node.getTextContent();

                localizeMap.put(id, localized);
            }

            return new Language(localeCode, name, flagFileName, localizeMap);
        }
    }

}

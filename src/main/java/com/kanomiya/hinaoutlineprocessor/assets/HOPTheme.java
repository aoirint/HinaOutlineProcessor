package com.kanomiya.hinaoutlineprocessor.assets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static com.kanomiya.hinaoutlineprocessor.HOPUtils.colorCode;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPTheme {
    public final boolean builtin;
    public final String id;
    public String name;
    public final HashMap<String, Color> colorMap;

    public HOPTheme(boolean builtin, String id, String name, HashMap<String, Color> colorMap) {
        this.builtin = builtin;
        this.id = id;
        this.name = name;
        this.colorMap = colorMap;
    }

    public Color getColor(String key) {
        return colorMap.get(key);
    }

    public void setColor(String key, Color value) {
        colorMap.put(key, value);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

    public void saveToFile() throws TransformerException, ParserConfigurationException, IOException {
        Document document = HOPTheme.saveToFile(this);

        Transformer t = TransformerFactory.newInstance().newTransformer();

        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        Path parentDir = builtin ? HOPThemes.BUILTIN_DIR : HOPThemes.USER_DIR;
        Files.createDirectories(parentDir);

        try (BufferedWriter bw = Files.newBufferedWriter(parentDir.resolve(id + ".xml"))) {
            t.transform(new DOMSource(document), new StreamResult(bw));
        }
    }


    public static HashMap<String, Color> createDefaultColorMap() {
        HashMap<String, Color> colorMap = new HashMap<>();

        colorMap.put("treeComponent.foreground", UIManager.getColor("Tree.textForeground"));
        colorMap.put("treeComponent.background", UIManager.getColor("Tree.textBackground"));
        colorMap.put("treeComponent.selectionForeground", UIManager.getColor("Tree.selectionForeground"));
        colorMap.put("treeComponent.selectionBackground", UIManager.getColor("Tree.selectionBackground"));

        colorMap.put("titleComponent.foreground", UIManager.getColor("TextField.foreground"));
        colorMap.put("titleComponent.background", UIManager.getColor("TextField.background"));
        colorMap.put("titleComponent.selectionForeground", UIManager.getColor("TextField.selectionForeground"));
        colorMap.put("titleComponent.selectionBackground", UIManager.getColor("TextField.selectionBackground"));

        colorMap.put("bodyComponent.foreground", UIManager.getColor("TextArea.foreground"));
        colorMap.put("bodyComponent.background", UIManager.getColor("TextArea.background"));
        colorMap.put("bodyComponent.selectionForeground", UIManager.getColor("TextArea.selectionForeground"));
        colorMap.put("bodyComponent.selectionBackground", UIManager.getColor("TextArea.selectionBackground"));

        colorMap.put("emphasisBorder", Color.GREEN.darker());

        return colorMap;
    }

    public static HOPTheme loadFrom(boolean builtin, Document document) {
        Element rootNode = document.getDocumentElement();

        String id = rootNode.getAttribute("id");
        String name = rootNode.getAttribute("name");
        HashMap<String, Color> colorMap = createDefaultColorMap();

        NodeList rNodeList = rootNode.getChildNodes();

        for (int r=0, rLen=rNodeList.getLength(); r<rLen; r++) {
            if (rNodeList.item(r).getNodeType() == Node.ELEMENT_NODE) {
                Element rNode = (Element) rNodeList.item(r);
                String tagName = rNode.getTagName();
                String key = rNode.getAttribute("key");
                String value = rNode.getAttribute("value");

                if ("color".equals(tagName)) {
                    colorMap.put(key, Color.decode(value));
                }
            }
        }

        return new HOPTheme(builtin, id, name, colorMap);
    }

    public static Document saveToFile(HOPTheme theme) throws ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootNode = document.createElement("themeDoc");

        rootNode.setAttribute("id", theme.id);
        rootNode.setAttribute("name", theme.name);

        theme.colorMap.forEach((key, value) -> {
            Element colorNode = document.createElement("color");
            colorNode.setAttribute("key", key);
            colorNode.setAttribute("value", colorCode(value));

            rootNode.appendChild(colorNode);
        });

        document.appendChild(rootNode);

        return document;
    }
}

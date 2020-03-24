package com.kanomiya.hinaoutlineprocessor.assets.menudoc;

import com.kanomiya.hinaoutlineprocessor.assets.HOPIcons;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Kanomiya in 2017/02.
 */
public class HOPMenuDocHelper
{
    public static HOPMenuComposite parseMenuDoc(Path menuDocPath, HOPIcons icons) throws IOException, ParserConfigurationException, SAXException
    {
        HOPMenuComposite composite = new HOPMenuComposite();

        try (InputStream is = Files.newInputStream(menuDocPath)) {
            Document menuDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            Element rootNode = menuDoc.getDocumentElement();

            NodeList nodeList = rootNode.getChildNodes();
            for (int i=0, len=nodeList.getLength(); i<len; i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE)
                    parseElement((Element) node, composite, null, icons);
            }
        }

        return composite;
    }

    private static void parseElement(Element element, HOPMenuComposite composite, HOPMenuComposite.HOPMenu parent, HOPIcons icons)
    {
        String tagName = element.getTagName();

        if ("menu".equals(tagName)) {
            String name = element.getAttribute("name");
            String command = element.getAttribute("command");
            if (command.isEmpty()) command = name;

            String iconString = element.hasAttribute("icon") ? element.getAttribute("icon") : "";
            char mnemonic = element.hasAttribute("mnemonic") ? element.getAttribute("mnemonic").charAt(0) : '\0';
            String accelerator = element.hasAttribute("accelerator") ? element.getAttribute("accelerator") : "";

            HOPMenuComposite.HOPMenu menu = new HOPMenuComposite.HOPMenu(name, command, icons.parseIconString(iconString), mnemonic, accelerator);

            NodeList nodeList = element.getChildNodes();
            for (int i=0, len=nodeList.getLength(); i<len; i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE)
                    parseElement((Element) node, composite, menu, icons);
            }

            if (parent == null)
                composite.elements.add(menu);
            else
                parent.children.add(menu);
        }
        else if ("menuItem".equals(tagName)) {
            String command = element.getAttribute("command");
            String name = element.hasAttribute("name") ? element.getAttribute("name") : command;
            String iconString = element.hasAttribute("icon") ? element.getAttribute("icon") : "";
            char mnemonic = element.hasAttribute("mnemonic") ? element.getAttribute("mnemonic").charAt(0) : '\0';
            String accelerator = element.hasAttribute("accelerator") ? element.getAttribute("accelerator") : "";

            HOPMenuComposite.HOPMenuItem menuItem = new HOPMenuComposite.HOPMenuItem(name, command, icons.parseIconString(iconString), mnemonic, accelerator);

            if (parent == null)
                composite.elements.add(menuItem);
            else
                parent.children.add(menuItem);
        }
        else if ("separator".equals(tagName)) {
            HOPMenuComposite.HOPMenuItemSeparator separator = new HOPMenuComposite.HOPMenuItemSeparator();

            if (parent == null)
                composite.elements.add(separator);
            else
                parent.children.add(separator);
        }
    }


}

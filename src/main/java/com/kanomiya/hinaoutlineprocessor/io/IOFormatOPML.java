package com.kanomiya.hinaoutlineprocessor.io;

import com.kanomiya.hinaoutlineprocessor.structure.*;
import com.kanomiya.hinaoutlineprocessor.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.filechooser.FileFilter;
import javax.swing.tree.MutableTreeNode;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kanomiya in 2017/02.
 */
public class IOFormatOPML extends HOPDocumentIOFormat {

    @Override
    public String getName() {
        return "opml";
    }

    @Override
    public String getRecommendExtension() {
        return "opml";
    }

    @Override
    public String getDescription()
    {
        return "OPMLドキュメント(." + getRecommendExtension() + ")";
    }

    @Override
    public String format(HOPDocument document) throws FormatException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new FormatException(e);
        }

        Element rootNode = doc.createElement("opml");
        rootNode.setAttribute("version", "2.0");

        rootNode.appendChild(export_Head(doc, document));

        Element bodyNode = doc.createElement("body");
        {
            for (HOPNode nodeObj: Collections.list(document.nodeTree.children()))
                export_Outline_Reclusive(doc, nodeObj, bodyNode);
        }
        rootNode.appendChild(bodyNode);

        doc.appendChild(rootNode);

        Transformer t;
        try {
            t = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new FormatException(e);
        }

        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        try (StringWriter writer = new StringWriter()) {
            t.transform(new DOMSource(doc), new StreamResult(writer));
            writer.flush();

            return writer.getBuffer().toString();
        } catch (TransformerException | IOException e) {
            throw new FormatException(e);
        }
    }

    @Override
    public HOPDocument parse(String text) throws FormatException {
        HOPDocumentOwner owner = null;
        ZonedDateTime createdDate = null;
        ZonedDateTime lastModifiedDate = null;
        HOPBounds bounds = null;
        HOPNodeTree nodeTree = null;

        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(text.getBytes()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new FormatException(e);
        }

        Element rootNode = doc.getDocumentElement();
        NodeList rootNodeList = rootNode.getChildNodes();

        for (int r=0, rLen=rootNodeList.getLength(); r<rLen; r++) {
            if (rootNodeList.item(r).getNodeType() == Node.ELEMENT_NODE) {
                Element rNode = (Element) rootNodeList.item(r);
                String rTagName = rNode.getTagName();

                if ("head".equals(rTagName))
                {
                    String ownerName, ownerMail, ownerPhone, ownerWebsite;
                    ownerName = ownerMail = ownerPhone = ownerWebsite = "";

                    String createdDateText, lastModifiedDateText;
                    createdDateText = lastModifiedDateText = "";

                    Integer top, bottom, left, right;
                    top = left = bottom = right = null;


                    NodeList headNodeList = rNode.getChildNodes();

                    for (int j=0, lenj=headNodeList.getLength(); j<lenj; j++) {
                        if (headNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element node = (Element) headNodeList.item(j);
                            final String tagName = node.getTagName();

                            if ("ownerName".equals(tagName))
                                ownerName = node.getTextContent();
                            else if ("ownerEmail".equals(tagName))
                                ownerMail = node.getTextContent();
                            else if ("ownerPhone".equals(tagName))
                                ownerPhone = node.getTextContent();
                            else if ("ownerUrl".equals(tagName))
                                ownerWebsite = node.getTextContent();
                            else if ("dateCreated".equals(tagName))
                                createdDateText = node.getTextContent();
                            else if ("dateModified".equals(tagName))
                                lastModifiedDateText = node.getTextContent();
                            else if ("windowTop".equals(tagName))
                                top = Integer.valueOf(node.getTextContent());
                            else if ("windowLeft".equals(tagName))
                                left = Integer.valueOf(node.getTextContent());
                            else if ("windowBottom".equals(tagName))
                                bottom = Integer.valueOf(node.getTextContent());
                            else if ("windowRight".equals(tagName))
                                right = Integer.valueOf(node.getTextContent());
                        }
                    }

                    owner = new HOPDocumentOwner(ownerName, ownerMail, ownerPhone, ownerWebsite);
                    createdDate = ZonedDateTime.parse(createdDateText, DateTimeFormatter.RFC_1123_DATE_TIME);
                    lastModifiedDate = ZonedDateTime.parse(lastModifiedDateText, DateTimeFormatter.RFC_1123_DATE_TIME);

                    if (top == null || left == null || bottom == null || right == null)
                        bounds = HOPBounds.DEFAULT;
                    else
                        bounds = new HOPBounds(top, bottom, left, right);
                }
                else if ("body".equals(rTagName)) {
                    nodeTree = new HOPNodeTree();
                    parse_Body_Reclusive(rNode, nodeTree);
                }
            }
        }



        // NULLチェック
        if (createdDate == null || lastModifiedDate == null) {
            ZonedDateTime now = ZonedDateTime.now();
            if (createdDate == null) {
                createdDate = lastModifiedDate = now;
            }
            else {
                lastModifiedDate = now;
            }
        }

        if (owner == null)
            owner = HOPDocumentOwner.empty();

        if (bounds == null)
            bounds = HOPBounds.DEFAULT;

        if (nodeTree == null)
            throw new FormatException(FormatException.Cause.LACK_OF_NODETREE);

        return new HOPDocument(createdDate, lastModifiedDate, owner, bounds, HOPMode.EDIT, DateFormatType.ZONED_DATE_TIME, nodeTree);
    }



    private static void parse_Body_Reclusive(Element parentNode, MutableTreeNode parentObj) {
        NodeList nodeList = parentNode.getChildNodes();

        for (int i=0, len=nodeList.getLength(); i<len; i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element nNode = (Element) nodeList.item(i);

                if ("outline".equals(nNode.getTagName())) {

                    ZonedDateTime createdDate;
                    if (nNode.hasAttribute("created"))
                        createdDate = ZonedDateTime.parse(nNode.getAttribute("created"), DateTimeFormatter.RFC_1123_DATE_TIME);
                    else
                        createdDate = ZonedDateTime.now();

                    String text = nNode.getAttribute("text");
                    String title, body;
                    String lineSeparator = StringUtils.lineSeparator(text);

                    if (text.contains(lineSeparator)) {
                        String[] array = text.split(lineSeparator, 2);
                        title = array[0].trim();
                        body = array[1].trim();
                    }
                    else {
                        title = text;
                        body = "";
                    }

                    HOPNodeMarker marker = parentNode.getTagName() == "body" ?
                            new HOPNodeMarker(HOPNodeMarkerType.SQUARE, HOPNodeMarker.DEFAULT_COLOR_GREEN) :
                            new HOPNodeMarker(HOPNodeMarkerType.DIAMOND, HOPNodeMarker.DEFAULT_COLOR_BLUE);

                    HOPNode nodeObj = new HOPNode(title, createdDate, createdDate, marker, body);
                    parentObj.insert(nodeObj, parentObj.getChildCount());

                    parse_Body_Reclusive(nNode, nodeObj);
                }
            }
        }
    }



    private static Element export_Head(Document doc, HOPDocument document) {
        Element headNode = doc.createElement("head");

        {
            Element createdDateNode = doc.createElement("dateCreated");
            Element lastModifiedDateNode = doc.createElement("dateModified");
            createdDateNode.setTextContent(document.createdDate.format(DateTimeFormatter.RFC_1123_DATE_TIME));
            lastModifiedDateNode.setTextContent(document.lastModifiedDate.format(DateTimeFormatter.RFC_1123_DATE_TIME));

            headNode.appendChild(createdDateNode);
            headNode.appendChild(lastModifiedDateNode);
        }
        {
            Element ownerNameNode = doc.createElement("ownerName");
            ownerNameNode.setTextContent(document.owner.name);

            Element ownerEmailNode = doc.createElement("ownerEmail");
            ownerEmailNode.setTextContent(document.owner.mail);

            Element ownerPhoneNode = doc.createElement("ownerPhone");
            ownerPhoneNode.setTextContent(document.owner.phone);

            Element ownerUrlNode = doc.createElement("ownerUrl");
            ownerUrlNode.setTextContent(document.owner.website);

            headNode.appendChild(ownerNameNode);
            headNode.appendChild(ownerEmailNode);
            headNode.appendChild(ownerPhoneNode);
            headNode.appendChild(ownerUrlNode);
        }
        {
            Element windowTopNode = doc.createElement("windowTop");
            Element windowLeftNode = doc.createElement("windowLeft");
            Element windowBottomNode = doc.createElement("windowBottom");
            Element windowRightNode = doc.createElement("windowRight");

            windowTopNode.setTextContent(String.valueOf(document.bounds.top));
            windowLeftNode.setTextContent(String.valueOf(document.bounds.left));
            windowBottomNode.setTextContent(String.valueOf(document.bounds.bottom));
            windowRightNode.setTextContent(String.valueOf(document.bounds.right));

            headNode.appendChild(windowTopNode);
            headNode.appendChild(windowLeftNode);
            headNode.appendChild(windowBottomNode);
            headNode.appendChild(windowRightNode);
        }

        return headNode;
    }

    private static void export_Outline_Reclusive(Document doc, HOPNode nodeObj, Element parentNode) {
        Element outlineNode = doc.createElement("outline");

        StringBuilder text = new StringBuilder();

        text.append(nodeObj.title + System.lineSeparator()).append(nodeObj.body);

        outlineNode.setAttribute("text", text.toString());
        outlineNode.setAttribute("created", nodeObj.createdDate.format(DateTimeFormatter.RFC_1123_DATE_TIME));

        parentNode.appendChild(outlineNode);

        if (nodeObj.getChildCount() > 0)
            for (HOPNode childObj: Collections.list(nodeObj.children()))
                export_Outline_Reclusive(doc, childObj, outlineNode);
    }


}

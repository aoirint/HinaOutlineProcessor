package com.kanomiya.hinaoutlineprocessor.io;

import com.kanomiya.hinaoutlineprocessor.structure.*;
import com.kanomiya.hinaoutlineprocessor.structure.address.HOPAddressComparator;
import com.kanomiya.hinaoutlineprocessor.util.NumberUtils;
import com.kanomiya.hinaoutlineprocessor.util.Pair;
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
import java.io.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.kanomiya.hinaoutlineprocessor.structure.address.HOPAddressHelper.parseAddressText;
import static com.kanomiya.hinaoutlineprocessor.structure.address.HOPAddressHelper.parseNodeAddress;
import static com.kanomiya.hinaoutlineprocessor.structure.address.HOPAddressHelper.stringifyNodeAddress;

/**
 * Created by Kanomiya in 2017/02.
 */
public class IOFormatHOPD extends HOPDocumentIOFormat {

    @Override
    public String getName() {
        return "hopd";
    }

    @Override
    public String getRecommendExtension() {
        return "hopd";
    }

    @Override
    public String getDescription()
    {
        return "ひなホップドキュメント(."+ getRecommendExtension() + ")";
    }

    @Override
    public String format(HOPDocument document) throws FormatException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new FormatException(e);
        }


        Element rootNode = doc.createElement("hopd");
        rootNode.setAttribute("version", "1.0");

        rootNode.appendChild(export_Meta(doc, document));

        Element nodesNode = doc.createElement("nodes");
        {
            List<Pair<Integer[], Element>> nodeList = new ArrayList<>();

            for (HOPNode nodeObj: Collections.list(document.nodeTree.children())) {
                nodeList.add(Pair.of(parseNodeAddress(nodeObj), export_Node(doc, document, nodeObj)));

                for (HOPNode childObj: Collections.list(nodeObj.childrenTraversal())) {
                    nodeList.add(Pair.of(parseNodeAddress(childObj), export_Node(doc, document, childObj)));
                }
            }

            HOPAddressComparator comparator = new HOPAddressComparator();
            nodeList.sort((a, b) -> comparator.compare(a.left, b.left));

            nodeList.forEach(pair -> nodesNode.appendChild(pair.right));
        }
        rootNode.appendChild(nodesNode);

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

    public static ZonedDateTime parseDate(DateFormatType dateFormatType, String text) {
        if (dateFormatType == DateFormatType.ZONED_DATE_TIME) {
            return ZonedDateTime.parse(text);
        } else if (dateFormatType == DateFormatType.DATE) {
            return ZonedDateTime.of(LocalDate.parse(text, dateFormatType.formatter), LocalTime.MIDNIGHT, ZoneId.systemDefault());
        }

        return null;
    }

    @Override
    public HOPDocument parse(String text) throws FormatException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(text.getBytes()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new FormatException(e);
        }

        Element rootNode = doc.getDocumentElement();

        HOPDocumentOwner owner = null;
        ZonedDateTime createdDate = null;
        ZonedDateTime lastModifiedDate = null;
        HOPBounds bounds = null;
        HOPMode mode = null;
        DateFormatType dateFormatType = null;
        HOPNodeTree nodeTree = null;

        String hopd_version = rootNode.getAttribute("version");

        NodeList rNodeList = rootNode.getChildNodes();

        Element metaNode = null;
        Element nodesNode = null;

        for (int r=0, rLen=rNodeList.getLength(); r<rLen; r++) {
            if (rNodeList.item(r).getNodeType() == Node.ELEMENT_NODE) {
                Element rNode = (Element) rNodeList.item(r);
                String rTagName = rNode.getTagName();

                if ("meta".equals(rTagName)) {
                    metaNode = rNode;
                }
                else if ("nodes".equals(rTagName)) {
                    nodesNode = rNode;
                }
            }
        }

        { // meta node
            String createdDateText, lastModifiedDateText;
            createdDateText = lastModifiedDateText = null;

            NodeList mNodeList = metaNode.getChildNodes();
            for (int m=0, mLen=mNodeList.getLength(); m<mLen; m++) {
                if (mNodeList.item(m).getNodeType() == Node.ELEMENT_NODE) {
                    Element mNode = (Element) mNodeList.item(m);
                    String mTagName = mNode.getTagName();

                    if ("owner".equals(mTagName)) {
                        String ownerName = mNode.getAttribute("name");
                        String ownerMail = mNode.getAttribute("mail");
                        String ownerPhone = mNode.getAttribute("phone");
                        String ownerWebsite = mNode.getAttribute("website");

                        owner = new HOPDocumentOwner(ownerName, ownerMail, ownerPhone, ownerWebsite);
                    }
                    else if ("createdDate".equals(mTagName)) {
                        createdDateText = mNode.getTextContent();
                    }
                    else if ("lastModifiedDate".equals(mTagName)) {
                        lastModifiedDateText = mNode.getTextContent();
                    }
                    else if ("bounds".equals(mTagName)) {
                        Integer top = NumberUtils.asIntegerOrNull(mNode.getAttribute("top"));
                        Integer bottom = NumberUtils.asIntegerOrNull(mNode.getAttribute("bottom"));
                        Integer left = NumberUtils.asIntegerOrNull(mNode.getAttribute("left"));
                        Integer right = NumberUtils.asIntegerOrNull(mNode.getAttribute("right"));

                        if (! (top == null || bottom == null || left == null || right == null))
                            bounds = new HOPBounds(top, bottom, left, right);
                    }
                    else if ("mode".equals(mTagName)) {
                        String name = mNode.getAttribute("name");
                        mode = HOPMode.find(name);
                    }
                    else if ("dateFormatType".equals(mTagName)) {
                        String name = mNode.getAttribute("name");
                        dateFormatType = DateFormatType.find(name);
                    }
                }
            }


            // NULLチェック 1
            if (dateFormatType == null) { // TODO: ログ
                dateFormatType = DateFormatType.ZONED_DATE_TIME;
            }

            // 日付変換
            createdDate = parseDate(dateFormatType, createdDateText);
            lastModifiedDate = parseDate(dateFormatType, lastModifiedDateText);


            // NULLチェック 2
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

            if (mode == null)
                mode = HOPMode.EDIT;
        }



        {
            nodeTree = new HOPNodeTree();


            List<Pair<Integer[], Element>> nodeAddressList = new ArrayList<>();
            NodeList nodeList = nodesNode.getChildNodes();
            for (int i=0, len=nodeList.getLength(); i<len; i++)
            {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element nodeNode = (Element) nodeList.item(i);
                    if ("node".equals(nodeNode.getTagName())) {
                        String addressText = nodeNode.getAttribute("address");

                        nodeAddressList.add(Pair.of(parseAddressText(addressText), nodeNode));
                    }
                }
            }

            HOPAddressComparator comparator = new HOPAddressComparator();
            nodeAddressList.sort((a, b) -> comparator.compare(a.left, b.left));

            for (Pair<Integer[], Element> pair: nodeAddressList) {
                Integer[] address = pair.left;
                Element nodeNode = pair.right;

                String title = nodeNode.getAttribute("title");
                String body = nodeNode.getAttribute("body");
                ZonedDateTime nodeCreatedDate;
                ZonedDateTime nodeLastModifiedDate;

                String nodeCreatedDateText = nodeNode.getAttribute("createdDate");
                String nodeLastModifiedText = nodeNode.getAttribute("lastModifiedDate");

                nodeCreatedDate = parseDate(dateFormatType, nodeCreatedDateText);
                nodeLastModifiedDate = parseDate(dateFormatType, nodeLastModifiedText);

                String markerText = nodeNode.getAttribute("marker");
                HOPNodeMarker marker = HOPNodeMarker.parse(markerText);

                HOPNode node = new HOPNode(title, nodeCreatedDate, nodeLastModifiedDate, marker, body);

                MutableTreeNode parent = nodeTree;
                for (int i=0,len=address.length; i<len-1; i++)
                    parent = (MutableTreeNode) parent.getChildAt(address[i] -1);

                parent.insert(node, address[address.length -1] -1);
            }
        }

        if (nodeTree == null)
            throw new FormatException(FormatException.Cause.LACK_OF_NODETREE);

        return new HOPDocument(createdDate, lastModifiedDate, owner, bounds, mode, dateFormatType, nodeTree);
    }




    private static Element export_Node(Document doc, HOPDocument document, HOPNode nodeObj) {
        Element nodeNode = doc.createElement("node");

        nodeNode.setAttribute("address", stringifyNodeAddress(nodeObj));

        nodeNode.setAttribute("title", nodeObj.title);
        nodeNode.setAttribute("body", nodeObj.body);

        nodeNode.setAttribute("createdDate", nodeObj.createdDate.format(document.dateFormatType.formatter));
        nodeNode.setAttribute("lastModifiedDate", nodeObj.lastModifiedDate.format(document.dateFormatType.formatter));
        nodeNode.setAttribute("marker", nodeObj.marker.toString());

        return nodeNode;
    }

    private static Element export_Meta(Document doc, HOPDocument document) {
        Element metaNode = doc.createElement("meta");

        {
            Element createdDateNode = doc.createElement("createdDate");
            Element lastModifiedDateNode = doc.createElement("lastModifiedDate");
            createdDateNode.setTextContent(document.createdDate.format(document.dateFormatType.formatter));
            lastModifiedDateNode.setTextContent(document.lastModifiedDate.format(document.dateFormatType.formatter));

            metaNode.appendChild(createdDateNode);
            metaNode.appendChild(lastModifiedDateNode);
        }
        {
            Element ownerNode = doc.createElement("owner");
            ownerNode.setAttribute("name", document.owner.name);
            ownerNode.setAttribute("mail", document.owner.mail);
            ownerNode.setAttribute("phone", document.owner.phone);
            ownerNode.setAttribute("website", document.owner.website);

            metaNode.appendChild(ownerNode);
        }
        {
            Element boundsNode = doc.createElement("bounds");
            boundsNode.setAttribute("top", String.valueOf(document.bounds.top));
            boundsNode.setAttribute("bottom", String.valueOf(document.bounds.bottom));
            boundsNode.setAttribute("left", String.valueOf(document.bounds.left));
            boundsNode.setAttribute("right", String.valueOf(document.bounds.right));

            metaNode.appendChild(boundsNode);
        }
        {
            Element modeNode = doc.createElement("mode");
            modeNode.setAttribute("name", document.mode.name);

            metaNode.appendChild(modeNode);
        }
        {
            Element dateFormatTypeNode = doc.createElement("dateFormatType");
            dateFormatTypeNode.setAttribute("name", document.dateFormatType.name);

            metaNode.appendChild(dateFormatTypeNode);
        }

        return metaNode;
    }


}

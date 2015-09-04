package com.alex.kroniax.levelparser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Parser {
    String mPath;

    Map mMap;

    public Parser(String filePath) {
        mPath = filePath;

        mMap = new Map();
    }

    public boolean parse(StringBuilder errorText) {
        File file;
        try {
            file = new File(mPath);
        } catch (Exception e) {
            errorText.append("Couldn't find file!");
            return false;
        }

        Document doc;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (ParserConfigurationException e) {
            errorText.append("Not a valid xml file!");
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            errorText.append("Not a valid xml file!");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            errorText.append("Couldn't find file!");
            e.printStackTrace();
            return false;
        }

        doc.normalize();

        // Check file root node
        if (!checkRootNode(doc, errorText))
            return false;

        if (!checkSubNotes(doc.getDocumentElement(), errorText))
            return false;
        // Save file
        mMap.save(mPath.substring(0, mPath.length() - 4));
        return true;
    }

    private boolean checkRootNode(Document doc, StringBuilder errorText) {
        Element rootNode = doc.getDocumentElement();
        if (rootNode.getNodeName() != "map") {
            errorText.append("map tag missing!");
            return false;
        }
        if (rootNode.hasAttribute("width"))
            mMap.width = Integer.parseInt(rootNode.getAttribute("width"));
        else {
            errorText.append("width tag in map missing");
            return false;
        }
        if (rootNode.hasAttribute("height"))
            mMap.height = Integer.parseInt(rootNode.getAttribute("height"));
        else {
            errorText.append("height tag in map missing");
            return false;
        }
        if (rootNode.hasAttribute("tilewidth"))
            mMap.tilewidth = Integer.parseInt(rootNode.getAttribute("tilewidth"));
        else {
            errorText.append("tilewidth tag in map missing");
            return false;
        }
        if (rootNode.hasAttribute("tileheight"))
            mMap.tileheight = Integer.parseInt(rootNode.getAttribute("tileheight"));
        else {
            errorText.append("tileheight tag in map missing");
            return false;
        }
        // Parse values
        return true;
    }

    private boolean checkSubNotes(Element root, StringBuilder errorText) {

        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) instanceof Element) {
                Element node = (Element) nodes.item(i);
                if (node.getNodeName() == "properties") {
                    parseMapProperties((Element) node);
                } else if (node.getNodeName() == "tileset") {
                    mMap.mTilesets.add(new Tileset());
                    if (!mMap.mTilesets.get(mMap.mTilesets.size() - 1).parse((Element) node, errorText))
                        return false;
                } else if (node.getNodeName() == "layer") {
                    mMap.mLayers.add(new Layer());
                    if (!mMap.mLayers.get(mMap.mLayers.size() - 1).parse((Element) node, errorText))
                        return false;
                } else if (node.getNodeName() == "objectgroup") {
                    if (!mMap.parseObjectGroup((Element) node, errorText))
                        return false;
                }
            }
        }

        return true;
    }

    private void parseMapProperties(Element props) {
        NodeList properties = props.getElementsByTagName("property");
        for (int i = 0; i < properties.getLength(); i++) {
            Element ele = (Element) properties.item(i);
            NamedNodeMap attribtutes = ele.getAttributes();
            for (int j = 0; j + 1 < attribtutes.getLength(); j += 2) {
                mMap.mProperties.put(attribtutes.item(j).getNodeValue(), attribtutes.item(j + 1).getNodeValue());
            }
        }
    }
}

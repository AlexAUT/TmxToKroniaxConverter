package com.alex.kroniax.levelparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Tileset {
    public String sourcefile;
    public String firstID;
    public String spacing;
    public String margin;

    public boolean parse(Element node, StringBuilder errorText) {
        boolean found = false;

        if (node.hasAttribute("firstgid")) {
            firstID = node.getAttribute("firstgid");
        } else {
            errorText.append("Tileset ID is missing");
            return false;
        }

        if (node.hasAttribute("margin"))
            margin = node.getAttribute("margin");
        else
            margin = new String("0");

        if (node.hasAttribute("spacing"))
            spacing = node.getAttribute("spacing");
        else
            spacing = new String("0");

        NodeList imgs = node.getElementsByTagName("image");
        if (imgs.getLength() > 0) {
            Element img = (Element) imgs.item(0);
            if (img.hasAttribute("source")) {
                sourcefile = ((Element) imgs.item(0)).getAttribute("source");
                sourcefile = sourcefile.split("/")[sourcefile.split("/").length - 1];
                found = true;
            }
        }
        if (!found) {
            errorText.append("Tileset has no images attached to it!");
            return false;
        }

        return true;
    }
}

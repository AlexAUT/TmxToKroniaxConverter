package com.alex.kroniax.levelparser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Tileset {
    public String sourcefile;
    public String firstID;
    public String width, height;

    public boolean parse(Element node, StringBuilder errorText) {
        boolean found = false;

        if (node.hasAttribute("firstgid")) {
            firstID = node.getAttribute("firstgid");
        } else {
            errorText.append("Tileset ID is missing");
            return false;
        }

        NodeList imgs = node.getElementsByTagName("image");
        if (imgs.getLength() > 0) {
            Element img = (Element) imgs.item(0);
            if (img.hasAttribute("source")) {
                sourcefile = ((Element) imgs.item(0)).getAttribute("source");
                found = true;
            }
            if (img.hasAttribute("width")) {
                width = img.getAttribute("width");
            }
            if (img.hasAttribute("height")) {
                height = img.getAttribute("height");
            }

        }
        if (!found) {
            errorText.append("Tileset has no images attached to it!");
            return false;
        }

        return true;
    }
}

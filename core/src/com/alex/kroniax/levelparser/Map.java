package com.alex.kroniax.levelparser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Map {
    public int width, height, tilewidth, tileheight;

    public HashMap<String, String> mProperties;

    public ArrayList<Tileset> mTilesets;
    public ArrayList<Layer> mLayers;
    public ArrayList<MapObject> mObjects;

    public Map() {
        mTilesets = new ArrayList<Tileset>();
        mLayers = new ArrayList<Layer>();
        mProperties = new HashMap<String, String>();
        mObjects = new ArrayList<MapObject>();
    }

    void save(String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path + ".kroniax");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        printHeader(writer);

        writer.close();
    }

    void printHeader(PrintWriter file) {
        file.println("[width]");
        file.println(width);
        file.println("[height]");
        file.println(height);
        file.println("[tilewidth]");
        file.println(tilewidth);
        file.println("[tileheight]");
        file.println(tileheight);
        file.println("[mapproperties]");
        for (String key : mProperties.keySet())
            file.println(key + " " + mProperties.get(key));
        file.println("[/mapproperties]");

        file.println("[tilesets]");
        for (Tileset tileset : mTilesets) {
            file.println(tileset.sourcefile + " " + tileset.firstID + " " + tileset.margin + " " + tileset.spacing);
        }
        file.println("[/tilesets]");

        for (Layer layer : mLayers) {
            file.println("[layer]");
            file.println(layer.name);
            file.println(layer.width + " " + layer.height);
            file.println("[data]");
            for (String col : layer.data)
                file.println(col);
            file.println("[/data]");
            file.println("[properties]");
            for (String key : layer.properties.keySet())
                file.println(key + " " + (String) layer.properties.get(key));
            file.println("[/properties]");
            file.println("[/layer]");
        }
        
        file.println("[objects]");
        for(MapObject obj : mObjects)
            obj.print(file);
        file.println("[/objects]");
    }

    public boolean parseObjectGroup(Element node, StringBuilder errorText) {
        NodeList objects = node.getChildNodes();

        for (int i = 0; i < objects.getLength(); i++) {
            if(! (objects.item(i) instanceof Element))
                continue;
            Element object = (Element) objects.item(i);
            int x, y, w, h;
            String type;
            if (object.hasAttribute("x"))
                x = Integer.parseInt(object.getAttribute("x"));
            else {
                errorText.append("x attribute from an object is missing");
                return false;
            }
            if (object.hasAttribute("y"))
                y = Integer.parseInt(object.getAttribute("y"));
            else {
                errorText.append("y attribute from an object is missing");
                return false;
            }
            if (object.hasAttribute("width"))
                w = Integer.parseInt(object.getAttribute("width"));
            else {
                errorText.append("width attribute from an object is missing");
                return false;
            }
            if (object.hasAttribute("height"))
                h = Integer.parseInt(object.getAttribute("height"));
            else {
                errorText.append("height attribute from an object is missing");
                return false;
            }
            if (object.hasAttribute("type"))
                type = object.getAttribute("type");
            else {
                errorText.append("Type of object is missing!");
                return false;
            }
            mObjects.add(parseObjectProperties(object, type, x, y, w, h));
        }

        return true;
    }
    
    private MapObject parseObjectProperties(Element object, String type, int x, int y, int w, int h) {
        
        NodeList childs = object.getElementsByTagName("properties");
        
        java.util.Map<String, String> properties = new HashMap<String, String>();
        
        for(int i = 0; i < childs.getLength(); i++) {
            NodeList properties_val = ((Element)childs.item(i)).getElementsByTagName("property");
            for(int j = 0; j < properties_val.getLength(); j++) {
                Element property = (Element) properties_val.item(j);
                String key, value;
                if(property.hasAttribute("name"))
                    key = property.getAttribute("name");
                else
                    key = " ";
                if(property.hasAttribute("value"))
                    value = property.getAttribute("value");
                else
                    value = " ";
                
                properties.put(key, value);
            }
        }
        
        
        return new RectMapObject(type, x, y, w, h, properties);
    }
}

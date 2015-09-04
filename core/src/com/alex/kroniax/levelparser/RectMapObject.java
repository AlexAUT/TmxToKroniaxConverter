package com.alex.kroniax.levelparser;

import java.io.PrintWriter;
import java.util.Map;

public class RectMapObject implements MapObject {
    private String type;
    private int x, y, w, h;
    private Map<String, String> properties;

    public RectMapObject(String type, int x, int y, int w, int h, Map<String, String> properties) {
        super();

        this.type = type;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.properties = properties;
    }

    public void print(PrintWriter writer) {
        writer.println("[rect]");
        writer.println(type + " " + x + " " + y + " " + w + " " + h);
        writer.println("[properties]");
        for(String key : properties.keySet())
            writer.println(key + " " + properties.get(key));
        writer.println("[/properties]");
        writer.println("[/rect]");
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }
}

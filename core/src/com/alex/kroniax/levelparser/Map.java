package com.alex.kroniax.levelparser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Map {
    public int width, height, tilewidth, tileheight;

    public ArrayList<Tileset> mTilesets;
    public ArrayList<Layer> mLayers;

    public Map() {
        mTilesets = new ArrayList<Tileset>();
        mLayers = new ArrayList<Layer>();
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

        file.println("[tilesets]");
        for (Tileset tileset : mTilesets) {
            file.println(tileset.sourcefile + " " + tileset.width + " " + tileset.height);
        }

        for (Layer layer : mLayers) {
            file.println("[layer]");
            file.println(layer.name);
            file.println(layer.width + " " + layer.height);
            file.println("[data]");
            for(String col : layer.data)
                file.println(col);
            file.println("[/data]");
            file.println("[properties]");
            for(String key : layer.properties.keySet())
                file.println(key + " " + (String)layer.properties.get(key));
                
        }
    }
}

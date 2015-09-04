package com.alex.kroniax.levelparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class Layer {
    public String name;
    public String width, height;
    public ArrayList<String> data;

    public Map<String, String> properties;

    public Layer() {
        data = new ArrayList<String>();
        properties = new HashMap<String, String>();
    }

    public boolean parse(Element node, StringBuilder errorText) {

        if (!node.hasAttribute("name")) {
            errorText.append("Layer with no name not supported!");
            return false;
        }
        name = node.getAttribute("name");

        if (!node.hasAttribute("width")) {
            errorText.append("Width attribite of Layer" + name + " is missing!");
            return false;
        }
        width = node.getAttribute("width");

        if (!node.hasAttribute("height")) {
            errorText.append("Height attribite of Layer" + name + " is missing!");
            return false;
        }
        height = node.getAttribute("height");

        // Check for properties
        NodeList propertiesTags = node.getElementsByTagName("properties");
        for (int i = 0; i < propertiesTags.getLength(); i++)
            parseProperties((Element) propertiesTags.item(i));

        // Check for the data of the layer
        NodeList dataTags = node.getElementsByTagName("data");

        if (dataTags.getLength() == 0) {
            errorText.append("Layer \"" + name + "\" with no data not possible!");
            return false;
        }

        Element dataTag = (Element) dataTags.item(0);

        if (!dataTag.hasAttribute("encoding")) {
            errorText.append("Layer \"" + name + "\" data has no encoding tag!");
            return false;
        }

        if (!dataTag.getAttribute("encoding").equalsIgnoreCase("csv")) {
            errorText.append("Layer \"" + name + "\" encoding not supported! (Only CSV is supported!");
            return false;
        }

        String data = dataTag.getTextContent();

        processData(data);

        return true;
    }
    
    final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
    final int FLIPPED_VERTICALLY_FLAG   = 0x40000000;
    final int FLIPPED_DIAGONALLY_FLAG   = 0x20000000;

    private boolean processData(String data) {
        String lines[] = data.split("\\r?\\n");
        if (lines.length < 3)
            return false;

        while (lines[0].length() == 0)
            lines = Arrays.copyOfRange(lines, 1, lines.length);
        ArrayList<String[]> valueGrid = new ArrayList<String[]>();

        for (String row : lines) {
            valueGrid.add(row.split(","));
        }
        //Count the continuous empty lines
        int sinceLastRow = 0;
        
        for (int col = 0; col < valueGrid.get(0).length; col++) {

            boolean foundStart = false;
            
            int sinceLastValue = 0;

            StringBuilder colData = new StringBuilder();

            for (int row = 0; row < valueGrid.size(); row++) {
                long value_long = Long.parseLong(valueGrid.get(row)[col]);
                value_long &= ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);
                int value = (int) value_long;
                if (value != 0) {
                    if (!foundStart) {
                        //Write the empty lines cache
                        if(sinceLastRow > 0) {
                            this.data.add(new String("x " + sinceLastRow));
                            sinceLastRow = 0;
                        }
                        
                        colData.append(row + " " + value);
                        foundStart = true;
                        sinceLastValue = 0;
                    } else {
                        //Write cached 0 values
                        if(sinceLastValue > 0) {
                            colData.append(" x " + sinceLastValue);
                            sinceLastValue = 0;
                        }
                        colData.append(" " + value);
                    }
                } else {
                    sinceLastValue++;
                }
            }
            // Check if we have an empty line
            if(!foundStart)
                sinceLastRow++;
            // Now we have start and end value, write the information into data
            if(colData.length() > 0)
                this.data.add(colData.toString());
        }

        return true;
    }

    void parseProperties(Element props) {
        NodeList properties = props.getElementsByTagName("property");
        for (int i = 0; i < properties.getLength(); i++) {
            Element ele = (Element) properties.item(i);
            NamedNodeMap attribtutes = ele.getAttributes();
            for (int j = 0; j + 1 < attribtutes.getLength(); j += 2) {
                this.properties.put(attribtutes.item(j).getNodeValue(), attribtutes.item(j + 1).getNodeValue());
            }
        }
    }
}

package com.alex.kroniax.levelparser;

import java.util.ArrayList;
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
        
        //Check for properties
        NodeList propertiesTags = node.getElementsByTagName("properties");
        for(int i = 0; i < propertiesTags.getLength(); i++)
            parseProperties((Element)propertiesTags.item(i));
        
        //Check for the data of the layer
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
    
    private boolean processData(String data){
        String lines[] = data.split("\\r?\\n");
        if(lines.length < 3)
            return false;
        int startRow = 0;
        if(lines[0].length() == 0)
            startRow++;
        
        ArrayList<String[]> valueGrid = new ArrayList<String[]>();
        
        for(String row : lines) {
            valueGrid.add(row.split(","));
        }
        System.out.println(valueGrid.get(startRow).length);
        for(int col = 0; col < valueGrid.get(startRow).length; col++) {
            
            int startValue = startRow;
            boolean foundStart = false;
            int endValue = 0;
            
            StringBuilder colData = new StringBuilder();
            
            for(int row = startRow; row < valueGrid.size(); row++) {
                int value = Integer.parseInt(valueGrid.get(row)[col]);
                if(value != 0) {
                    endValue = row;
                    if(!foundStart) {
                        startValue = row;
                        foundStart = true;
                    }
                }
            }
            System.out.println(startValue + " | " + endValue);
            //Now we have start and end value, write the information into data
            colData.append(startValue);
            for(int row = startValue; row <= endValue; row++){
                colData.append(" ");
                colData.append(valueGrid.get(row)[col]);
            }
            this.data.add(colData.toString());
        }
        
        return true;
    }
    
    void parseProperties(Element props) {
        NodeList properties = props.getElementsByTagName("property");
        for(int i = 0; i < properties.getLength(); i++) {
            Element ele = (Element) properties.item(i);
            NamedNodeMap attribtutes = ele.getAttributes();
            for(int j = 0; j+1 < attribtutes.getLength(); j += 2) {
                this.properties.put(attribtutes.item(j).getNodeValue(), attribtutes.item(j+1).getNodeValue());
            }
        }
    }
}

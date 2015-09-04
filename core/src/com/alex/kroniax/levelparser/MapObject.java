package com.alex.kroniax.levelparser;

import java.io.PrintWriter;

public interface MapObject {
    public void print(PrintWriter file);
    public java.util.Map<String, String> getProperties();
}
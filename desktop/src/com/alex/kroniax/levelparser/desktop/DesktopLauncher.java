package com.alex.kroniax.levelparser.desktop;

import com.alex.kroniax.levelparser.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 500;
        config.height = 600;
        new LwjglApplication(new Application(), config);
    }
}

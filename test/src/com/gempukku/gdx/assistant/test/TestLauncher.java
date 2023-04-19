package com.gempukku.gdx.assistant.test;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gempukku.gdx.assistant.GdxAssistant;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class TestLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Test Gdx Assistant");

        TestPluginsProvider pluginsProvider = new TestPluginsProvider();

        new Lwjgl3Application(new GdxAssistant(pluginsProvider), config);
    }
}

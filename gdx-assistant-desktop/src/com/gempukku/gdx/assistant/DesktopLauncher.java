package com.gempukku.gdx.assistant;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPlugin;
import com.gempukku.gdx.plugins.provider.jar.JarsPluginsProvider;

import java.io.File;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setWindowedMode(1440, 810);
        config.setTitle("Gdx Assistant");

        JarsPluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider = new JarsPluginsProvider<>(new File("plugins"), "Gdx-Assistant-Plugin");

        new Lwjgl3Application(new GdxAssistant(pluginsProvider), config);
    }
}

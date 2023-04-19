package com.gempukku.gdx.plugins.provider;

import com.badlogic.gdx.Files;
import com.gempukku.gdx.plugins.Plugin;

/**
 * @param <T> Type of the pluggable application, that allows Plugins to make calls on it.
 * @param <U> Type of the plugin that can communicate with the type of application above.
 */
public interface PluginsProvider<T, U extends Plugin<T>> {
    void loadPlugins();
    Iterable<U> getPlugins();
    Files getPluginFiles();
}

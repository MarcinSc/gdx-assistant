package com.gempukku.gdx.plugins;

public interface Plugin<T> {
    String getId();

    PluginVersion getVersion();

    boolean shouldBeRegistered(PluginEnvironment pluginEnvironment);

    void registerPlugin(T pluggableApplication);

    void deregisterPlugin();
}

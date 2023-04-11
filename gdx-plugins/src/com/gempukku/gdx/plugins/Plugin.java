package com.gempukku.gdx.plugins;

public interface Plugin<T> {
    String getId();

    PluginVersion getVersion();

    boolean shouldBeRegistered(PluginEnvironment pluginEnvironment);

    /**
     * Initial callout to the plugin. It's not guaranteed that all the other plugins have been registered already.
     * If you write a plugin A that extends a functionality of another plugin - B. This is where plugin A should
     * communicate with the plugin B, to pass any information that might be required during initialization of plugin B.
     */
    void registerPlugin();

    /**
     * Callout to the plugin, at this point it is guaranteed that all the plugins have been registered.
     *
     * @param pluggableApplication
     */
    void initializePlugin(T pluggableApplication);

    void deregisterPlugin();
}

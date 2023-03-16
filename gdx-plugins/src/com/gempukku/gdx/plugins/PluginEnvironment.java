package com.gempukku.gdx.plugins;

import java.util.function.Predicate;

public interface PluginEnvironment {
    boolean hasPlugin(String plugin);
    boolean hasPluginVersion(String plugin, Predicate<PluginVersion> versionPredicate);
}

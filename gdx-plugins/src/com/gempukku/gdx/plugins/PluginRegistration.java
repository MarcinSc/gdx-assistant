package com.gempukku.gdx.plugins;

import com.gempukku.gdx.plugins.provider.PluginsProvider;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class PluginRegistration<T, U extends Plugin<T>> {
    private final List<U> registeredPlugins = new ArrayList<>();

    public void registerPlugins(PluginsProvider<T, U> pluginsProvider, Function<U, T> pluginApplicationProvider) {
        Iterable<U> plugins = pluginsProvider.getPlugins();

        Map<String, U> filterLatestPlugins = filterLatestPlugins(plugins);
        Map<String, U> chosenToRegister = new LinkedHashMap<>();

        PluginEnvironment pluginEnvironment = new PluginEnvironment() {
            @Override
            public boolean hasPlugin(String plugin) {
                return chosenToRegister.containsKey(plugin);
            }

            @Override
            public boolean hasPluginVersion(String plugin, Predicate<PluginVersion> versionPredicate) {
                Plugin<T> registeringPlugin = chosenToRegister.get(plugin);
                return registeringPlugin != null && versionPredicate.test(registeringPlugin.getVersion());
            }
        };

        boolean changed;
        do {
            changed = false;
            for (U pluginToCheck : filterLatestPlugins.values()) {
                if (!chosenToRegister.containsKey(pluginToCheck.getId())) {
                    if (pluginToCheck.shouldBeRegistered(pluginEnvironment)) {
                        chosenToRegister.put(pluginToCheck.getId(), pluginToCheck);
                        changed = true;
                    }
                }
            }
        } while (changed);

        for (U value : chosenToRegister.values()) {
            value.registerPlugin();
            registeredPlugins.add(value);
        }

        for (U registeredPlugin : registeredPlugins) {
            T application = pluginApplicationProvider.apply(registeredPlugin);
            registeredPlugin.initializePlugin(application);
        }
    }

    public void deregisterPlugins() {
        for (Plugin<T> registeredPlugin : registeredPlugins) {
            registeredPlugin.deregisterPlugin();
        }
        registeredPlugins.clear();
    }

    private Map<String, U> filterLatestPlugins(Iterable<U> plugins) {
        Map<String, U> result = new HashMap<>();

        for (U plugin : plugins) {
            String id = plugin.getId();
            if (result.containsKey(id)) {
                PluginVersion storedVersion = result.get(id).getVersion();
                PluginVersion version = plugin.getVersion();
                if (isNewer(id, version, storedVersion)) {
                    result.put(id, plugin);
                }
            } else {
                result.put(id, plugin);
            }
        }
        return result;
    }

    public static boolean isNewer(String pluginId, PluginVersion version, PluginVersion storedVersion) {
        if (version.getCommit() != null || storedVersion.getCommit() != null)
            throw new RuntimeException("Unable to resolve best version for two snapshot versions of plugin - " + pluginId);

        if (version.getMajor() > storedVersion.getMajor())
            return true;
        if (version.getMajor() == storedVersion.getMajor()) {
            if (version.getMinor() > storedVersion.getMinor())
                return true;
            if (version.getMinor() == storedVersion.getMinor()) {
                if (version.getBugfix() > storedVersion.getBugfix())
                    return true;
            }
        }
        return false;
    }
}

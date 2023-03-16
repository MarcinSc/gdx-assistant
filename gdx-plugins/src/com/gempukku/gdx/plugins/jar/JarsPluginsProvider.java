package com.gempukku.gdx.plugins.jar;

import com.gempukku.gdx.plugins.Plugin;
import com.gempukku.gdx.plugins.PluginsProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarsPluginsProvider<T, U extends Plugin<T>> implements PluginsProvider<T, U> {
    private List<U> plugins = new ArrayList<>();

    public void initializePluginsAndClassloader(File pluginFolder, String classNameMainAttribute) throws Exception {
        File[] jarFiles = pluginFolder.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jar");
                    }
                });
        if (jarFiles != null) {
            for (File file : jarFiles) {
                try (JarFile jarFile = new JarFile(file)) {
                    Manifest manifest = jarFile.getManifest();
                    if (manifest != null) {
                        String pluginClassName = manifest.getMainAttributes().getValue(classNameMainAttribute);
                        if (pluginClassName != null) {
                            U plugin = (U) Class.forName(pluginClassName).newInstance();
                            plugins.add(plugin);
                        }
                    }
                }
            }

            setupPluginClassLoader(jarFiles);
        }
    }

    @Override
    public Iterable<U> getPlugins() {
        return plugins;
    }

    private void setupPluginClassLoader(File[] jarFiles) throws MalformedURLException {
        List<URL> pluginUrls = new ArrayList<>();
        for (File file : jarFiles) {
            pluginUrls.add(file.toURI().toURL());
        }
        URLClassLoader classLoader = URLClassLoader.newInstance(pluginUrls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
    }
}

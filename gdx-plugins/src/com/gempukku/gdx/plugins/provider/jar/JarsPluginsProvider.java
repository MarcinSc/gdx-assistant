package com.gempukku.gdx.plugins.provider.jar;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gempukku.gdx.plugins.Plugin;
import com.gempukku.gdx.plugins.provider.PluginsProvider;

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
    private File pluginFolder;
    private String classNameMainAttribute;

    private List<U> plugins = new ArrayList<>();
    private Files pluginsFiles;

    public JarsPluginsProvider(File pluginFolder, String classNameMainAttribute) {
        this.pluginFolder = pluginFolder;
        this.classNameMainAttribute = classNameMainAttribute;
    }

    public void addPlugin(U plugin) {
        plugins.add(plugin);
    }

    @Override
    public void loadPlugins() {
        File[] jarFiles = pluginFolder.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jar");
                    }
                });
        if (jarFiles != null) {
            try {
                ClassLoader classLoader = setupPluginClassLoader(jarFiles);

                for (File file : jarFiles) {
                    try (JarFile jarFile = new JarFile(file)) {
                        Manifest manifest = jarFile.getManifest();
                        if (manifest != null) {
                            String pluginClassName = manifest.getMainAttributes().getValue(classNameMainAttribute);
                            if (pluginClassName != null) {
                                U plugin = (U) Class.forName(pluginClassName, true, classLoader).newInstance();
                                plugins.add(plugin);
                            }
                        }
                    }
                }

                pluginsFiles = new JarsFiles(classLoader, Gdx.files);
            } catch (Exception exp) {
                throw new GdxRuntimeException("Unable to load plugins", exp);
            }
        } else {
            pluginsFiles = Gdx.files;
        }
    }

    @Override
    public Files getPluginFiles() {
        return pluginsFiles;
    }

    @Override
    public Iterable<U> getPlugins() {
        return plugins;
    }

    private ClassLoader setupPluginClassLoader(File[] jarFiles) throws MalformedURLException {
        List<URL> pluginUrls = new ArrayList<>();
        for (File file : jarFiles) {
            pluginUrls.add(file.toURI().toURL());
        }
        URLClassLoader classLoader = URLClassLoader.newInstance(pluginUrls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
        return classLoader;
    }
}

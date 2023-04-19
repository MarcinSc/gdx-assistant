package com.gempukku.gdx.assistant.test;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPlugin;
import com.gempukku.gdx.assistant.test.plugin.TestAssistantPlugin;
import com.gempukku.gdx.plugins.provider.PluginsProvider;

public class TestPluginsProvider implements PluginsProvider<AssistantApplication, AssistantPlugin> {
    private Array<AssistantPlugin> plugins = new Array<>();

    public TestPluginsProvider() {
        plugins.add(new TestAssistantPlugin());
    }

    @Override
    public void loadPlugins() {

    }

    @Override
    public Files getPluginFiles() {
        return Gdx.files;
    }

    @Override
    public Iterable<AssistantPlugin> getPlugins() {
        return plugins;
    }
}

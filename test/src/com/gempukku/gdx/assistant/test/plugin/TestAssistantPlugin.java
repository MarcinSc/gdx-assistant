package com.gempukku.gdx.assistant.test.plugin;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPlugin;
import com.gempukku.gdx.assistant.plugin.AssistantPluginProject;
import com.gempukku.gdx.plugins.PluginEnvironment;
import com.gempukku.gdx.plugins.PluginVersion;

public class TestAssistantPlugin implements AssistantPlugin {
    private AssistantApplication application;

    @Override
    public AssistantPluginProject newProjectCreated() {
        return new TestAssistantPluginProject();
    }

    @Override
    public AssistantPluginProject projectOpened(JsonValue pluginData) {
        return new TestAssistantPluginProject(pluginData);
    }

    @Override
    public String getId() {
        return "testPlugin";
    }

    @Override
    public PluginVersion getVersion() {
        return new PluginVersion(0, 0, 1);
    }

    @Override
    public boolean shouldBeRegistered(PluginEnvironment pluginEnvironment) {
        return true;
    }

    @Override
    public void registerPlugin(AssistantApplication pluggableApplication) {
        this.application = pluggableApplication;

        application.addMenu("Test", "Test",
                new Runnable() {
                    public void run() {
                        System.out.println("Test pressed");
                    }
                });
        application.addMenu("Test/Test", "Sub-test",
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sub-test pressed");
                    }
                });
        application.addMenuSeparator("Test/Test");
        application.addMenu("Test/Test", "Sub-test2",
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sub-test2 pressed");
                    }
                });
    }

    @Override
    public void deregisterPlugin() {
        this.application = null;
    }
}

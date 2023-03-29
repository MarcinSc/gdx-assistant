package com.gempukku.gdx.assistant.test.plugin;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPlugin;
import com.gempukku.gdx.assistant.plugin.AssistantPluginProject;
import com.gempukku.gdx.assistant.plugin.AssistantPluginTab;
import com.gempukku.gdx.plugins.PluginEnvironment;
import com.gempukku.gdx.plugins.PluginVersion;
import com.kotcrab.vis.ui.widget.VisTable;

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

        application.addMainMenu("Test");
        application.addMenuItem("Test", null, "Test",
                new Runnable() {
                    public void run() {
                        System.out.println("Test pressed");
                    }
                });
        application.addPopupMenu("Test", null, "Test");
        application.addMenuItem("Test", "Test", "Sub-test",
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sub-test pressed");
                    }
                });
        application.addMenuSeparator("Test", "Test");
        application.addMenuItem("Test", "Test", "Sub-test2",
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sub-test2 pressed");
                    }
                });

        addTestTab("Test1");
        addTestTab("Test2");
        addTestTab("Test3");
    }

    private void addTestTab(String title) {
        application.addTab(title, new VisTable(),
                new AssistantPluginTab() {
                    @Override
                    public void setActive(boolean active) {
                        System.out.println("Tab "+ title +" active: "+active);
                    }

                    @Override
                    public void closed() {
                        System.out.println("Tab "+ title +" closed");
                    }
                });
    }

    @Override
    public void deregisterPlugin() {
        this.application = null;
    }
}

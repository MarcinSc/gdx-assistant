package com.gempukku.gdx.assistant.test.plugin;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.gdx.assistant.plugin.*;
import com.gempukku.gdx.plugins.PluginEnvironment;
import com.gempukku.gdx.plugins.PluginVersion;
import com.kotcrab.vis.ui.widget.VisTable;

public class TestAssistantPlugin implements AssistantPlugin {
    private AssistantApplication application;

    @Override
    public AssistantPluginProject newProjectCreated(AssistantProject assistantProject) {
        return new TestAssistantPluginProject(application);
    }

    @Override
    public AssistantPluginProject projectOpened(AssistantProject assistantProject, JsonValue pluginData) {
        return new TestAssistantPluginProject(application, pluginData);
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
    public void registerPlugin() {

    }

    @Override
    public void initializePlugin(AssistantApplication pluggableApplication) {
        this.application = pluggableApplication;

        MenuManager menuManager = application.getMenuManager();
        menuManager.addMainMenu("Test");
        menuManager.addMenuItem("Test", null, "Test",
                new Runnable() {
                    public void run() {
                        System.out.println("Test pressed");
                    }
                });
        menuManager.addPopupMenu("Test", null, "Test");
        menuManager.addMenuItem("Test", "Test", "Sub-test",
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sub-test pressed");
                    }
                });
        menuManager.addMenuSeparator("Test", "Test");
        menuManager.addMenuItem("Test", "Test", "Sub-test2",
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sub-test2 pressed");
                    }
                });

        addTestTab("Test1", true);
        addTestTab("Test2", false);
        addTestTab("Test3", true);

        application.getStatusManager().addStatus("Test plugin registered");
    }

    private void addTestTab(String title, boolean dirty) {
        application.getTabManager().addTab(title, new VisTable(),
                new AssistantPluginTab() {
                    @Override
                    public boolean isDirty() {
                        return dirty;
                    }

                    @Override
                    public void setActive(boolean active) {
                        System.out.println("Tab " + title + " active: " + active);
                    }

                    @Override
                    public void closed() {
                        System.out.println("Tab " + title + " closed");
                    }
                });
    }

    @Override
    public void deregisterPlugin() {
        this.application = null;
    }
}

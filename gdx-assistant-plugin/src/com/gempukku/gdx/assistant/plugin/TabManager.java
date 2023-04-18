package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public interface TabManager {
    void addTab(String title, Table content, AssistantPluginTab tab);

    boolean isActiveTab(AssistantPluginTab tab);

    void switchToTab(AssistantPluginTab tab);

    void setTabTitle(AssistantPluginTab tab, String title);

    void closeTab(AssistantPluginTab tab);
}

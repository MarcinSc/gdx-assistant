package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public interface AssistantApplication {
    void addMenu(String menuPath, String name, Runnable runnable);

    void addMenuSeparator(String menuPath);

    AssistantTab addTab(String title, Table content, AssistantPluginTab tab);
}

package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public interface AssistantApplication {
    FileHandle getProjectFolder();

    Skin getApplicationSkin();

    void addMenu(String menuPath, String name, Runnable runnable);

    void addMenuSeparator(String menuPath);

    void setMenuDisabled(String menuPath, String name, boolean disabled);

    AssistantTab addTab(String title, Table content, AssistantPluginTab tab);
}

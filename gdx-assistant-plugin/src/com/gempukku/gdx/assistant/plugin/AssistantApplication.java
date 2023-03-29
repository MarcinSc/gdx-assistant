package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public interface AssistantApplication {
    FileHandle getProjectFolder();

    Skin getApplicationSkin();

    boolean addMainMenu(String name);

    boolean addPopupMenu(String mainMenu, String path, String name);

    boolean addMenuItem(String mainMenu, String popupPath, String name, Runnable runnable);

    boolean addMenuSeparator(String mainMenu, String popupPath);

    boolean setMenuItemDisabled(String mainMenu, String popupPath, String name, boolean disabled);

    boolean setPopupMenuDisabled(String mainMenu, String popupPath, String name, boolean disabled);

    AssistantTab addTab(String title, Table content, AssistantPluginTab tab);
}

package com.gempukku.gdx.assistant.plugin;

public interface MenuManager {
    boolean addMainMenu(String name);

    boolean addPopupMenu(String mainMenu, String path, String name);

    boolean addMenuItem(String mainMenu, String popupPath, String name, Runnable runnable);

    boolean addMenuSeparator(String mainMenu, String popupPath);

    boolean setMenuItemDisabled(String mainMenu, String popupPath, String name, boolean disabled);

    boolean setPopupMenuDisabled(String mainMenu, String popupPath, String name, boolean disabled);

    boolean clearPopupMenuContents(String mainMenu, String popupPath, String name);

    boolean updateMenuItemListener(String mainMenu, String popupPath, String name, Runnable runnable);
}

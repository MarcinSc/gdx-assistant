package com.gempukku.gdx.assistant.plugin;

public interface AssistantApplication {
    void addMenu(String menuPath, String name, Runnable runnable);
    void addMenuSeparator(String menuPath);
}

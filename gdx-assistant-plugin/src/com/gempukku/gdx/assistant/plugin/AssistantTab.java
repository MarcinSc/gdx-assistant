package com.gempukku.gdx.assistant.plugin;

public interface AssistantTab {
    void setTitle(String title);
    void setDirty(boolean dirty);
    void closeTab();
}

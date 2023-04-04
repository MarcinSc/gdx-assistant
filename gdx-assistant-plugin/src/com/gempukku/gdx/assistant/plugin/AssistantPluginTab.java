package com.gempukku.gdx.assistant.plugin;

public interface AssistantPluginTab {
    boolean isDirty();
    void setActive(boolean active);
    void closed();
}

package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.utils.JsonValue;

public interface AssistantPluginProject {
    boolean isProjectDirty();

    void processUpdate(float deltaTime);

    JsonValue saveProject();

    void markProjectClean();

    void closeProject();
}

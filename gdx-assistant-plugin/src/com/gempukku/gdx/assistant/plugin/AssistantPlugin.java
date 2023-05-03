package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.gdx.plugins.Plugin;

public interface AssistantPlugin extends Plugin<AssistantApplication> {
    AssistantPluginProject newProjectCreated(AssistantProject assistantProject);
    AssistantPluginProject projectOpened(AssistantProject assistantProject, JsonValue pluginData);
}

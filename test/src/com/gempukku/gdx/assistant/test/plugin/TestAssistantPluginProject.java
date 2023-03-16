package com.gempukku.gdx.assistant.test.plugin;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.gdx.assistant.plugin.AssistantPluginProject;

public class TestAssistantPluginProject implements AssistantPluginProject {
    private boolean dirty = false;

    public TestAssistantPluginProject() {
        this(null);
    }

    public TestAssistantPluginProject(JsonValue projectData) {

    }

    @Override
    public void processUpdate(float deltaTime) {

    }

    @Override
    public boolean isProjectDirty() {
        return dirty;
    }

    @Override
    public JsonValue saveProject() {
        return new JsonValue(JsonValue.ValueType.object);
    }

    @Override
    public void markProjectClean() {
        dirty = false;
    }

    @Override
    public void closeProject() {

    }
}

package com.gempukku.gdx.assistant.test.plugin;

import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPluginProject;

public class TestAssistantPluginProject implements AssistantPluginProject {
    private boolean dirty = false;
    private AssistantApplication application;

    private String statusId;
    private int frame = 0;

    public TestAssistantPluginProject(AssistantApplication application) {
        this(application, null);
    }

    public TestAssistantPluginProject(AssistantApplication application, JsonValue projectData) {
        this.application = application;
    }

    @Override
    public void processUpdate(float deltaTime) {
        if (statusId == null)
            statusId = application.getStatusManager().addStatus("Frame: " + frame++);
        else
            application.getStatusManager().updateStatus(statusId, "Frame: " + frame++);
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

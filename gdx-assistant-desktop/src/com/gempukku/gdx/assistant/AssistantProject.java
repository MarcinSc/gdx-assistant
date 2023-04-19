package com.gempukku.gdx.assistant;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPlugin;
import com.gempukku.gdx.assistant.plugin.AssistantPluginProject;
import com.gempukku.gdx.plugins.provider.PluginsProvider;

public class AssistantProject {
    private ObjectMap<String, AssistantPluginProject> pluginProjects = new ObjectMap<>();

    public void newProjectCreated(PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider) {
        for (AssistantPlugin plugin : pluginsProvider.getPlugins()) {
            AssistantPluginProject pluginProject = plugin.newProjectCreated();
            pluginProjects.put(plugin.getId(), pluginProject);
        }
    }

    public void openProject(FileHandle projectFile, PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider) {
        JsonReader reader = new JsonReader();
        JsonValue parsedProject = reader.parse(projectFile);

        JsonValue pluginsData = parsedProject.get("pluginsData");
        for (AssistantPlugin plugin : pluginsProvider.getPlugins()) {
            String pluginId = plugin.getId();
            pluginProjects.put(pluginId, plugin.projectOpened(pluginsData.get(pluginId)));
        }
    }

    public boolean isDirty() {
        for (AssistantPluginProject pluginProject : pluginProjects.values()) {
            if (pluginProject.isProjectDirty())
                return true;
        }

        return false;
    }

    public JsonValue saveProject() {
        JsonValue projectJson = new JsonValue(JsonValue.ValueType.object);
        JsonValue pluginsData = new JsonValue(JsonValue.ValueType.object);
        for (ObjectMap.Entry<String, AssistantPluginProject> pluginProject : pluginProjects) {
            JsonValue pluginData = pluginProject.value.saveProject();
            pluginsData.addChild(pluginProject.key, pluginData);
        }
        projectJson.addChild("pluginsData", pluginsData);
        return projectJson;
    }

    public void markSaved() {
        for (AssistantPluginProject pluginProject : pluginProjects.values()) {
            pluginProject.markProjectClean();
        }
    }

    public void closeProject() {
        for (AssistantPluginProject pluginProject : pluginProjects.values()) {
            pluginProject.closeProject();
        }
        pluginProjects.clear();
    }

    public void processUpdate(float deltaTime) {
        for (AssistantPluginProject pluginProject : pluginProjects.values()) {
            pluginProject.processUpdate(deltaTime);
        }
    }
}

package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public interface AssistantProject {
    String getProjectName();
    FileHandle getProjectFolder();
    FileHandle getAssetFolder();
    FileHandleResolver getAssetResolver();
}

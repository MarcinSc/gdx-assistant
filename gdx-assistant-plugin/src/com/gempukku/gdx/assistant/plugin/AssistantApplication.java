package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.gempukku.libgdx.common.undo.UndoManager;

public interface AssistantApplication {
    MenuManager getMenuManager();

    TabManager getTabManager();

    StatusManager getStatusManager();

    UndoManager getUndoManager();

    FileHandle getProjectFolder();

    Skin getApplicationSkin();

    void addWindow(Window window);
}

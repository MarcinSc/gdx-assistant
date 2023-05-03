package com.gempukku.gdx.assistant.plugin;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.gempukku.libgdx.undo.UndoManager;

public interface AssistantApplication {
    MenuManager getMenuManager();

    TabManager getTabManager();

    StatusManager getStatusManager();

    UndoManager getUndoManager();

    void addWindow(Window window);
}

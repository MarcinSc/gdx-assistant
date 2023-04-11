package com.gempukku.gdx.assistant.plugin;

public interface UndoableAction {
    void undoAction();
    void redoAction();
    boolean canUndo();
    boolean canRedo();
    String getUndoTextRepresentation();
    String getRedoTextRepresentation();
}

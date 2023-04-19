package com.gempukku.gdx.assistant;

import com.badlogic.gdx.utils.Array;
import com.gempukku.libgdx.common.undo.UndoManager;
import com.gempukku.libgdx.common.undo.UndoableAction;
import com.kotcrab.vis.ui.widget.MenuItem;

public class AssistantUndoManager implements UndoManager {
    private final Array<UndoableAction> undoableActions = new Array<>();
    private final Array<UndoableAction> redoableActions = new Array<>();

    private final MenuItem undoItem;
    private final MenuItem redoItem;

    public AssistantUndoManager(MenuItem undoItem, MenuItem redoItem) {
        this.undoItem = undoItem;
        this.redoItem = redoItem;
    }

    public void update() {
        undoItem.setDisabled(!canUndo());
        redoItem.setDisabled(!canRedo());
        undoItem.setText(getUndoText());
        redoItem.setText(getRedoText());
    }

    @Override
    public void addUndoableAction(UndoableAction undoableAction) {
        undoableActions.add(undoableAction);
        redoableActions.clear();
    }

    public String getUndoText() {
        for (int i = undoableActions.size - 1; i >= 0; i--) {
            UndoableAction undoableAction = undoableActions.get(i);
            if (undoableAction.canUndo()) {
                String undoTextRepresentation = undoableAction.getUndoTextRepresentation();
                if (undoTextRepresentation != null)
                    return undoTextRepresentation;
                else
                    return "Undo";
            }
        }
        return "Undo";
    }

    public String getRedoText() {
        for (int i = redoableActions.size - 1; i >= 0; i--) {
            UndoableAction undoableAction = redoableActions.get(i);
            if (undoableAction.canUndo()) {
                String redoTextRepresentation = undoableAction.getRedoTextRepresentation();
                if (redoTextRepresentation != null)
                    return redoTextRepresentation;
                else
                    return "Redo";
            }
        }
        return "Redo";
    }

    public boolean canUndo() {
        for (UndoableAction undoableAction : undoableActions) {
            if (undoableAction.canUndo())
                return true;
        }
        return false;
    }

    public boolean canRedo() {
        for (UndoableAction redoableAction : redoableActions) {
            if (redoableAction.canRedo())
                return true;
        }
        return false;
    }

    public void undo() {
        while (undoableActions.size > 0) {
            UndoableAction undoableAction = undoableActions.removeIndex(undoableActions.size - 1);
            if (undoableAction.canUndo()) {
                undoableAction.undoAction();
                redoableActions.add(undoableAction);
                return;
            }
        }
    }

    public void redo() {
        while (redoableActions.size > 0) {
            UndoableAction undoableAction = undoableActions.removeIndex(undoableActions.size - 1);
            if (undoableAction.canRedo()) {
                undoableAction.redoAction();
                undoableActions.add(undoableAction);
                return;
            }
        }
    }
}

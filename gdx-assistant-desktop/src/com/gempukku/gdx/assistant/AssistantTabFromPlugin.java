package com.gempukku.gdx.assistant;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPluginTab;
import com.gempukku.libgdx.ui.tabbedpane.GDirtyTab;
import com.gempukku.libgdx.ui.tabbedpane.GDirtyTabLabel;
import com.gempukku.libgdx.ui.tabbedpane.GTabControl;
import com.kotcrab.vis.ui.util.dialog.ConfirmDialogListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

public class AssistantTabFromPlugin implements GDirtyTab {
    private final GDirtyTabLabel<AssistantTabFromPlugin> tabActor;
    private GTabControl<AssistantTabFromPlugin> tabControl;
    private final Table content;
    private final AssistantPluginTab tab;

    public AssistantTabFromPlugin(AssistantApplication assistantApplication, AssistantProject assistantProject,
                                  GTabControl<AssistantTabFromPlugin> tabControl, String title, Table content, AssistantPluginTab tab) {
        this.tabControl = tabControl;
        this.content = content;
        this.tab = tab;
        tabActor = new GDirtyTabLabel<>(tabControl, this, "default", title, true,
                new Runnable() {
                    @Override
                    public void run() {
                        processClose(assistantApplication, assistantProject);
                    }
                });
    }

    private void processClose(AssistantApplication assistantApplication, AssistantProject assistantProject) {
        if (isDirty()) {
            Dialogs.ConfirmDialog<String> confirmDialog = new Dialogs.ConfirmDialog<>(
                    "Confirm close", "Contents of this tab have been modified, would you like to save the project?",
                    new String[]{"Save", "Don't save", "Cancel"}, new String[]{"Save", "Don't save", "Cancel"},
                    new ConfirmDialogListener<String>() {
                        @Override
                        public void result(String result) {
                            if (result.equals("Save")) {
                                assistantProject.saveProject();
                                tabControl.closeTab(AssistantTabFromPlugin.this);
                            } else if (result.equals("Don't save")) {
                                tabControl.closeTab(AssistantTabFromPlugin.this);
                            }
                        }
                    });
            confirmDialog.setModal(true);
            assistantApplication.addWindow(confirmDialog.fadeIn());
        } else {
            tabControl.closeTab(AssistantTabFromPlugin.this);
        }
    }

    @Override
    public Actor getTabActor() {
        return tabActor;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public void tabAdded() {

    }

    @Override
    public void setActive(boolean active) {
        tabActor.setActive(active);
        tab.setActive(active);
    }

    @Override
    public void tabClosed() {
        tab.closed();
    }

    public AssistantPluginTab getTab() {
        return tab;
    }

    public void setTitle(String title) {
        tabActor.setTitle(title);
    }

    @Override
    public boolean isDirty() {
        return tab.isDirty();
    }
}

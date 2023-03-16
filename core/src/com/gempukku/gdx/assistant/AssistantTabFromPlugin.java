package com.gempukku.gdx.assistant;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.gdx.assistant.plugin.AssistantPluginTab;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class AssistantTabFromPlugin extends Tab {
    private String title;
    private final Table content;
    private final AssistantPluginTab tab;

    private boolean dirty;

    public AssistantTabFromPlugin(String title, Table content, AssistantPluginTab tab) {
        super(true, true);

        this.title = title;
        this.content = content;
        this.tab = tab;
    }

    public AssistantPluginTab getTab() {
        return tab;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public Table getContentTable() {
        return content;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

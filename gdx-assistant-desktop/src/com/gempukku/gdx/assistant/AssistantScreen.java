package com.gempukku.gdx.assistant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.*;
import com.gempukku.gdx.assistant.plugin.*;
import com.gempukku.gdx.plugins.provider.PluginsProvider;
import com.gempukku.libgdx.ui.input.KeyCombination;
import com.gempukku.libgdx.ui.tabbedpane.GTabbedPane;
import com.gempukku.libgdx.undo.DefaultUndoManager;
import com.gempukku.libgdx.undo.UndoManager;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class AssistantScreen extends VisTable {
    private static final String projectFileName = "assistant-project.assp";

    private final AssistantPreferences assistantPreferences;
    private final DefaultUndoManager assistantUndoManager;

    private final PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider;
    private final Skin skin;

    private final MenuBar menuBar;
    private final GTabbedPane<AssistantTabFromPlugin> tabbedPane;
    private final VisLabel statusBar;

    private ObjectMap<KeyCombination, Runnable> shortcuts = new ObjectMap<>();

    private DefaultAssistantProject currentProject;

    // File menu
    private MenuItem newMenuItem;
    private MenuItem openMenuItem;
    private MenuItem recentProjectsMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem closeMenuItem;
    private MenuItem exitMenuItem;

    // Edit menu
    private MenuItem undoMenuItem;
    private MenuItem redoMenuItem;

    public AssistantScreen(PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider, Skin skin) {
        assistantPreferences = new AssistantPreferences(Gdx.app.getPreferences("gdx-assistant.preferences"));

        this.pluginsProvider = pluginsProvider;
        this.skin = skin;

        Image image = new Image(skin.getDrawable("gempukku-logo"), Scaling.none);
        tabbedPane = new GTabbedPane<>(image);

        menuBar = createMenuBar();

        statusBar = new VisLabel("", "status-bar");
        statusBar.setEllipsis("...");

        add(menuBar.getTable()).growX().row();
        add(tabbedPane).grow().row();
        add(statusBar).growX().row();

        addListener(
                new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        for (ObjectMap.Entry<KeyCombination, Runnable> shortcut : shortcuts) {
                            if (shortcut.key.isActivated(Gdx.input, keycode)) {
                                shortcut.value.run();
                                return true;
                            }
                        }
                        return false;
                    }
                });

        assistantUndoManager = new DefaultUndoManager();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        undoMenuItem.setDisabled(!assistantUndoManager.canUndo());
        redoMenuItem.setDisabled(!assistantUndoManager.canRedo());
        undoMenuItem.setText(assistantUndoManager.getUndoText());
        redoMenuItem.setText(assistantUndoManager.getRedoText());
    }

    private void undo() {
        assistantUndoManager.undo();
    }

    private void redo() {
        assistantUndoManager.redo();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addMenu(createFileMenu());
        menuBar.addMenu(createEditMenu());

        return menuBar;
    }

    private Menu createEditMenu() {
        Menu editMenu = new Menu("Edit");

        undoMenuItem = new MenuItem("Undo");
        ChangeListener undoListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                undo();
            }
        };
        undoMenuItem.addListener(undoListener);
        setMenuShortcut(undoMenuItem, new KeyCombination(true, false, false, Input.Keys.Z),
                new Runnable() {
                    @Override
                    public void run() {
                        undo();
                    }
                });
        editMenu.addItem(undoMenuItem);

        redoMenuItem = new MenuItem("Redo");
        ChangeListener redoListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                redo();
            }
        };
        redoMenuItem.addListener(redoListener);
        setMenuShortcut(redoMenuItem, new KeyCombination(true, true, false, Input.Keys.Z),
                new Runnable() {
                    @Override
                    public void run() {
                        redo();
                    }
                });
        editMenu.addItem(redoMenuItem);

        return editMenu;
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");

        newMenuItem = new MenuItem("New project");
        newMenuItem.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        newProject();
                    }
                });
        fileMenu.addItem(newMenuItem);

        openMenuItem = new MenuItem("Open project");
        openMenuItem.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        openProject();
                    }
                });
        fileMenu.addItem(openMenuItem);

        recentProjectsMenuItem = new MenuItem("Recent projects");
        PopupMenu recentProjectPopup = new PopupMenu();
        recentProjectsMenuItem.setSubMenu(recentProjectPopup);
        rebuildRecentProjectsMenu();
        fileMenu.addItem(recentProjectsMenuItem);

        saveMenuItem = new MenuItem("Save project");
        ChangeListener saveListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveProject();
            }
        };
        saveMenuItem.setDisabled(true);
        setMenuShortcut(saveMenuItem, new KeyCombination(true, false, false, Input.Keys.S),
                new Runnable() {
                    @Override
                    public void run() {
                        saveProject();
                    }
                });
        saveMenuItem.addListener(saveListener);
        fileMenu.addItem(saveMenuItem);

        fileMenu.addSeparator();

        closeMenuItem = new MenuItem("Close project");
        closeMenuItem.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        closeProject();
                    }
                });
        closeMenuItem.setDisabled(true);
        fileMenu.addItem(closeMenuItem);

        fileMenu.addSeparator();

        exitMenuItem = new MenuItem("Exit");
        exitMenuItem.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        closeApplication();
                    }
                });
        fileMenu.addItem(exitMenuItem);

        return fileMenu;
    }

    private void rebuildRecentProjectsMenu() {
        PopupMenu popupMenu = recentProjectsMenuItem.getSubMenu();
        popupMenu.clearChildren();

        List<FileHandle> recentProjectFolders = assistantPreferences.getRecentProjectFolders();
        List<String> recentProjectNames = assistantPreferences.getRecentProjectNames();
        if (recentProjectFolders.isEmpty()) {
            recentProjectsMenuItem.setDisabled(true);
        } else {
            for (int i=0; i<recentProjectFolders.size(); i++) {
                FileHandle projectFolder = recentProjectFolders.get(i);
                String projectName = recentProjectNames.get(i);
                MenuItem recentProject = new MenuItem(projectName);
                recentProject.addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                openProject(projectFolder);
                            }
                        });
                popupMenu.addItem(recentProject);
            }
            recentProjectsMenuItem.setDisabled(false);
        }
    }

    private void setMenuShortcut(MenuItem menuItem, KeyCombination keyCombination, Runnable listener) {
        menuItem.setShortcut(keyCombination.getShortCutRepresentation());
        shortcuts.put(keyCombination,
                new Runnable() {
                    @Override
                    public void run() {
                        if (!menuItem.isDisabled() && listener != null)
                            listener.run();
                    }
                });
    }

    private void newProject() {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showErrorDialog(getStage(), "Current project has been modified, close it or save it");
        } else {
            closeProject();

            CreateNewProjectDialog dialog = new CreateNewProjectDialog(projectFileName);
            dialog.setProjectListener(
                    new CreateNewProjectDialog.ProjectListener() {
                        public void projectCreated(String projectName, FileHandle projectFolder, String assetsPath) {
                            FileHandle projectFile = projectFolder.child(projectFileName);

                            currentProject = new DefaultAssistantProject();
                            currentProject.newProjectCreated(projectFile, projectName, assetsPath, pluginsProvider);

                            saveMenuItem.setDisabled(false);
                            closeMenuItem.setDisabled(false);

                            saveProjectToFile();
                            setProjectFile();
                        }
                    });

            getStage().addActor(dialog.fadeIn());
        }
    }

    private void openProject() {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showErrorDialog(getStage(), "Current project has been modified, close it or save it");
        } else {
            closeProject();

            FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
            fileChooser.setModal(true);
            fileChooser.setSelectionMode(FileChooser.SelectionMode.DIRECTORIES);
            fileChooser.setListener(new FileChooserAdapter() {
                @Override
                public void selected(Array<FileHandle> file) {
                    FileHandle projectFolder = file.get(0);
                    FileHandle projectFile = projectFolder.child(projectFileName);

                    currentProject = new DefaultAssistantProject();
                    currentProject.openProject(projectFile, pluginsProvider);

                    setProjectFile();

                    saveMenuItem.setDisabled(false);
                    closeMenuItem.setDisabled(false);
                }
            });
            getStage().addActor(fileChooser.fadeIn());
        }
    }

    private void openProject(FileHandle projectFolder) {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showErrorDialog(getStage(), "Current project has been modified, close it or save it");
        } else {
            closeProject();

            currentProject = new DefaultAssistantProject();
            currentProject.openProject(projectFolder.child(projectFileName), pluginsProvider);

            setProjectFile();

            saveMenuItem.setDisabled(false);
            closeMenuItem.setDisabled(false);
        }
    }

    private void saveProject() {
        if (currentProject != null) {
            saveProjectToFile();
        }
    }

    private void saveProjectToFile() {
        JsonValue result = currentProject.saveProject();
        try {
            writeJson(currentProject.getProjectFile(), result);
            currentProject.markSaved();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    private void addTab(AssistantApplication assistantApplication, String title, Drawable icon, Table content, AssistantPluginTab tab) {
        tabbedPane.addTab(new AssistantTabFromPlugin(assistantApplication, currentProject, tabbedPane, title, icon, content, tab));
    }

    private AssistantPluginTab getActiveTab() {
        AssistantTabFromPlugin activeTab = tabbedPane.getActiveTab();
        if (activeTab == null)
            return null;
        return activeTab.getTab();
    }

    private boolean isActiveTab(AssistantPluginTab tab) {
        return getActiveTab() == tab;
    }

    private void switchToTab(AssistantPluginTab tab) {
        for (AssistantTabFromPlugin assistantTab : tabbedPane.getTabs()) {
            if (assistantTab.getTab() == tab) {
                tabbedPane.setTabActive(assistantTab);
                break;
            }
        }
    }

    public void processUpdate(float deltaTime) {
        if (currentProject != null) {
            currentProject.processUpdate(deltaTime);
        }
    }

    private void writeJson(FileHandle editedFile, JsonValue json) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(editedFile.write(false));
        try {
            json.prettyPrint(JsonWriter.OutputType.json, out);
            out.flush();
        } finally {
            out.close();
        }
    }

    private void closeProject() {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showOptionDialog(getStage(), "Project modified",
                    "Current project has been modified, would you like to save it first?",
                    Dialogs.OptionDialogType.YES_NO, new OptionDialogListener() {
                        @Override
                        public void yes() {
                            saveProject();
                            doCloseTheProject();
                        }

                        @Override
                        public void no() {
                            doCloseTheProject();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
        } else {
            doCloseTheProject();
        }
    }

    private void closeApplication() {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showOptionDialog(getStage(), "Project modified",
                    "Current project has been modified, would you like to save it first?",
                    Dialogs.OptionDialogType.YES_NO, new OptionDialogListener() {
                        @Override
                        public void yes() {
                            saveProject();
                            Gdx.app.exit();
                        }

                        @Override
                        public void no() {
                            Gdx.app.exit();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
        } else {
            Gdx.app.exit();
        }
    }

    private void setProjectFile() {
        assistantPreferences.addRecentProject(currentProject.getProjectName(), currentProject.getProjectFolder());
        assistantPreferences.setOpenedProject(currentProject.getProjectName(), currentProject.getProjectFolder());
        rebuildRecentProjectsMenu();
        Gdx.graphics.setTitle("Gdx Assistant - " + currentProject.getProjectName());
    }

    private void doCloseTheProject() {
        if (currentProject != null) {
            currentProject.closeProject();
        }

        currentProject = null;
        assistantPreferences.setOpenedProject(null, null);
        Gdx.graphics.setTitle("Gdx Assistant");
    }

    public AssistantApplication createApplicationForPlugin(AssistantPlugin assistantPlugin) {
        return new PluginAssistantApplication(assistantPlugin);
    }

    private class PluginAssistantApplication implements AssistantApplication {
        private final AssistantPlugin plugin;
        private final TabManager tabManager;
        private final MenuManager menuManager;
        private final StatusManager statusManager;

        public PluginAssistantApplication(AssistantPlugin plugin) {
            this.plugin = plugin;
            this.tabManager = new PluginTabManager(this, plugin);
            this.menuManager = new PluginMenuManager(plugin);
            this.statusManager = new PluginStatusManager(plugin);
        }

        @Override
        public TabManager getTabManager() {
            return tabManager;
        }

        @Override
        public MenuManager getMenuManager() {
            return menuManager;
        }

        @Override
        public StatusManager getStatusManager() {
            return statusManager;
        }

        @Override
        public UndoManager getUndoManager() {
            return assistantUndoManager;
        }

        @Override
        public void addWindow(Window window) {
            AssistantScreen.this.getStage().addActor(window);
        }
    }

    private class PluginTabManager implements TabManager {
        private final AssistantApplication application;
        private final AssistantPlugin plugin;

        public PluginTabManager(AssistantApplication application, AssistantPlugin plugin) {
            this.application = application;
            this.plugin = plugin;
        }

        @Override
        public void addTab(String title, Table content, AssistantPluginTab tab) {
            addTab(title, null, content, tab);
        }

        @Override
        public void addTab(String title, Drawable icon, Table content, AssistantPluginTab tab) {
            AssistantScreen.this.addTab(application, title, icon, content, tab);
        }

        @Override
        public AssistantPluginTab getActiveTab() {
            return AssistantScreen.this.getActiveTab();
        }

        @Override
        public boolean isActiveTab(AssistantPluginTab tab) {
            return AssistantScreen.this.isActiveTab(tab);
        }

        @Override
        public void switchToTab(AssistantPluginTab tab) {
            AssistantScreen.this.switchToTab(tab);
        }

        @Override
        public void setTabTitle(AssistantPluginTab tab, String title) {
            for (AssistantTabFromPlugin tabbedPaneTab : tabbedPane.getTabs()) {
                if (tabbedPaneTab.getTab() == tab) {
                    tabbedPaneTab.setTitle(title);
                    break;
                }
            }
        }

        @Override
        public void closeTab(AssistantPluginTab tab) {
            for (AssistantTabFromPlugin tabbedPaneTab : tabbedPane.getTabs()) {
                if (tabbedPaneTab.getTab() == tab) {
                    tabbedPane.closeTab(tabbedPaneTab);
                    break;
                }
            }
        }
    }

    private class PluginMenuManager implements MenuManager {
        private final AssistantPlugin plugin;

        private final ObjectMap<String, Menu> pluginMenus = new ObjectMap<>();
        private final ObjectMap<String, MenuItem> popupMenuItems = new ObjectMap<>();
        private final ObjectMap<String, MenuItem> menuItems = new ObjectMap<>();
        private final ObjectMap<String, Runnable> listeners = new ObjectMap<>();

        public PluginMenuManager(AssistantPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean addMainMenu(String name) {
            if (pluginMenus.containsKey(name))
                return false;
            Menu menu = new Menu(name);
            menuBar.addMenu(menu);
            pluginMenus.put(name, menu);
            return true;
        }

        @Override
        public boolean addPopupMenu(String mainMenu, String path, String name) {
            Menu menu = pluginMenus.get(mainMenu);
            if (menu == null)
                return false;

            String key = mainMenu + "/" + ((path != null) ? path + "/" : "") + name;
            if (popupMenuItems.containsKey(key))
                return false;

            MenuItem menuItem = new MenuItem(name);
            PopupMenu popupMenu = new PopupMenu();
            menuItem.setSubMenu(popupMenu);

            if (path == null) {
                menu.addItem(menuItem);
            } else {
                MenuItem parentMenuItem = popupMenuItems.get(mainMenu + "/" + path);
                if (parentMenuItem == null)
                    return false;
                parentMenuItem.getSubMenu().addItem(menuItem);
            }
            popupMenuItems.put(key, menuItem);
            return true;
        }

        @Override
        public boolean addMenuItem(String mainMenu, String popupPath, String name, Runnable runnable) {
            return addMenuItem(mainMenu, popupPath, name, null, runnable);
        }

        @Override
        public boolean addMenuItem(String mainMenu, String popupPath, String name, KeyCombination keyCombination, Runnable runnable) {
            Menu menu = pluginMenus.get(mainMenu);
            if (menu == null)
                return false;

            String key = mainMenu + "/" + ((popupPath != null) ? popupPath + "/" : "") + name;
            if (menuItems.containsKey(key))
                return false;

            MenuItem menuItem = new MenuItem(name);
            if (popupPath == null) {
                menu.addItem(menuItem);
            } else {
                MenuItem parentMenuItem = popupMenuItems.get(mainMenu + "/" + popupPath);
                if (parentMenuItem == null)
                    return false;
                parentMenuItem.getSubMenu().addItem(menuItem);
            }
            menuItem.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            Runnable listener = listeners.get(key);
                            if (listener != null)
                                listener.run();
                        }
                    });
            if (keyCombination != null) {
                shortcuts.put(keyCombination,
                        new Runnable() {
                            @Override
                            public void run() {
                                Runnable listener = listeners.get(key);
                                if (!menuItem.isDisabled() && listener != null)
                                    listener.run();
                            }
                        });
                menuItem.setShortcut(keyCombination.getShortCutRepresentation());
            }
            menuItems.put(key, menuItem);
            listeners.put(key, runnable);
            return true;
        }

        @Override
        public boolean updateMenuItemListener(String mainMenu, String popupPath, String name, Runnable runnable) {
            Menu menu = pluginMenus.get(mainMenu);
            if (menu == null)
                return false;

            String key = mainMenu + "/" + ((popupPath != null) ? popupPath + "/" : "") + name;
            if (!menuItems.containsKey(key))
                return false;

            listeners.put(key, runnable);
            return true;
        }

        @Override
        public boolean addMenuSeparator(String mainMenu, String popupPath) {
            Menu menu = pluginMenus.get(mainMenu);
            if (menu == null)
                return false;

            if (popupPath == null) {
                menu.addSeparator();
            } else {
                String parentKey = mainMenu + "/" + popupPath;
                MenuItem parentPopup = popupMenuItems.get(parentKey);
                if (parentPopup == null)
                    return false;
                parentPopup.getSubMenu().addSeparator();
            }
            return true;
        }

        @Override
        public boolean setMenuItemDisabled(String mainMenu, String popupPath, String name, boolean disabled) {
            return setMenuDisabled(mainMenu, popupPath, name, disabled, menuItems);
        }

        @Override
        public boolean setPopupMenuDisabled(String mainMenu, String popupPath, String name, boolean disabled) {
            return setMenuDisabled(mainMenu, popupPath, name, disabled, popupMenuItems);
        }

        @Override
        public boolean clearPopupMenuContents(String mainMenu, String popupPath, String name) {
            Menu menu = pluginMenus.get(mainMenu);
            if (menu == null)
                return false;

            String key = mainMenu + "/" + ((popupPath != null) ? popupPath + "/" : "") + name;
            MenuItem popupMenuItem = popupMenuItems.get(key);
            if (popupMenuItem == null)
                return false;
            popupMenuItem.getSubMenu().clearChildren();

            ObjectMap.Entries<String, MenuItem> popupsIterator = popupMenuItems.entries().iterator();
            while (popupsIterator.hasNext()) {
                ObjectMap.Entry<String, MenuItem> popupEntry = popupsIterator.next();
                if (popupEntry.key.startsWith(key + "/"))
                    popupsIterator.remove();
            }
            ObjectMap.Entries<String, MenuItem> menuItemsIterator = menuItems.entries().iterator();
            while (menuItemsIterator.hasNext()) {
                ObjectMap.Entry<String, MenuItem> menuItemEntry = menuItemsIterator.next();
                if (menuItemEntry.key.startsWith(key + "/"))
                    menuItemsIterator.remove();
            }

            return true;
        }

        private boolean setMenuDisabled(String mainMenu, String popupPath, String name, boolean disabled, ObjectMap<String, MenuItem> items) {
            String key = mainMenu + "/" + ((popupPath != null) ? popupPath + "/" : "") + name;
            MenuItem menuItem = items.get(key);
            if (menuItem == null)
                return false;
            menuItem.setDisabled(disabled);
            return true;
        }
    }

    private class PluginStatusManager implements StatusManager {
        private AssistantPlugin plugin;

        public PluginStatusManager(AssistantPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public String addStatus(String status) {
            statusBar.setText(" " + status);
            return "statusId";
        }

        @Override
        public void updateStatus(String statusId, String status) {
            statusBar.setText(" " + status);
        }
    }
}

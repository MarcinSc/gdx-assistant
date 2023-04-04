package com.gempukku.gdx.assistant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;
import com.gempukku.gdx.assistant.plugin.AssistantApplication;
import com.gempukku.gdx.assistant.plugin.AssistantPlugin;
import com.gempukku.gdx.assistant.plugin.AssistantPluginTab;
import com.gempukku.gdx.assistant.plugin.AssistantTab;
import com.gempukku.gdx.plugins.PluginsProvider;
import com.gempukku.libgdx.ui.tabbedpane.GTabbedPane;
import com.kotcrab.vis.ui.util.OsUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class AssistantScreen extends VisTable {
    private static final String projectFileExtension = "assp";

    private AssistantPreferences assistantPreferences;

    private PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider;
    private Skin skin;

    private GTabbedPane<AssistantTabFromPlugin> tabbedPane;
    private IntMap<Runnable> shortcuts = new IntMap<>();

    private AssistantProject currentProject;
    private FileHandle projectFile;

    private FileTypeFilter assistantProjectsFilter;

    private MenuItem recentProjects;
    private MenuItem saveMenu;
    private MenuItem saveAsMenu;
    private MenuItem closeMenu;
    private MenuBar menuBar;

    private AssistantTabFromPlugin lastTab = null;

    public AssistantScreen(PluginsProvider<AssistantApplication, AssistantPlugin> pluginsProvider, Skin skin) {
        assistantPreferences = new AssistantPreferences(Gdx.app.getPreferences("gdx-assistant.preferences"));

        this.pluginsProvider = pluginsProvider;
        this.skin = skin;

        assistantProjectsFilter = new FileTypeFilter(true);
        assistantProjectsFilter.addRule("Gdx assistant project (*.assp)", projectFileExtension);

        tabbedPane = new GTabbedPane<>();

        MenuBar menuBar = createMenuBar();
        add(menuBar.getTable()).growX().row();
        add(tabbedPane).grow().row();

        addListener(
                new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        boolean ctrlPressed = OsUtils.isMac() ?
                                Gdx.input.isKeyPressed(Input.Keys.SYM) :
                                Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
                        if (ctrlPressed) {
                            Runnable runnable = shortcuts.get(keycode);
                            if (runnable != null) {
                                runnable.run();
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    private FileHandle getProjectFolder() {
        return new LocalFileHandleResolver().resolve(".").parent();
    }

    private Skin getApplicationSkin() {
        return skin;
    }

    private MenuBar createMenuBar() {
        menuBar = new MenuBar();
        menuBar.addMenu(createFileMenu());

        return menuBar;
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New project");
        newItem.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        newProject();
                    }
                });
        fileMenu.addItem(newItem);

        MenuItem open = new MenuItem("Open project");
        open.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        openProject();
                    }
                });
        fileMenu.addItem(open);

        recentProjects = new MenuItem("Recent projects");
        PopupMenu recentProjectPopup = new PopupMenu();
        recentProjects.setSubMenu(recentProjectPopup);
        rebuildRecentProjectsMenu();
        fileMenu.addItem(recentProjects);

        saveMenu = new MenuItem("Save project");
        ChangeListener saveListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveProject();
            }
        };
        saveMenu.setDisabled(true);
        addControlShortcut(Input.Keys.S, saveMenu, saveListener);
        saveMenu.addListener(saveListener);
        fileMenu.addItem(saveMenu);

        saveAsMenu = new MenuItem("Save As");
        saveAsMenu.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        saveProjectAs();
                    }
                });
        saveAsMenu.setDisabled(true);
        fileMenu.addItem(saveAsMenu);

        fileMenu.addSeparator();

        closeMenu = new MenuItem("Close project");
        closeMenu.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        closeProject();
                    }
                });
        closeMenu.setDisabled(true);
        fileMenu.addItem(closeMenu);

        fileMenu.addSeparator();

        MenuItem exit = new MenuItem("Exit");
        exit.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        closeApplication();
                    }
                });
        fileMenu.addItem(exit);

        return fileMenu;
    }

    private void rebuildRecentProjectsMenu() {
        PopupMenu popupMenu = recentProjects.getSubMenu();
        popupMenu.clearChildren();

        List<FileHandle> recentProjectList = assistantPreferences.getRecentProjects();
        if (recentProjectList.isEmpty()) {
            recentProjects.setDisabled(true);
        } else {
            for (FileHandle fileHandle : recentProjectList) {
                MenuItem recentProject = new MenuItem(fileHandle.name());
                recentProject.addListener(
                        new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                openProject(fileHandle);
                            }
                        });
                popupMenu.addItem(recentProject);
            }
            recentProjects.setDisabled(false);
        }
    }

    private void addControlShortcut(int key, final MenuItem menuItem, final ChangeListener listener) {
        menuItem.setShortcut(Input.Keys.CONTROL_LEFT, key);
        shortcuts.put(key, new Runnable() {
            @Override
            public void run() {
                if (!menuItem.isDisabled())
                    listener.changed(null, null);
            }
        });
    }

    private void newProject() {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showErrorDialog(getStage(), "Current pipeline has been modified, close it or save it");
        } else {
            closeProject();

            currentProject = new AssistantProject();
            currentProject.newProjectCreated(pluginsProvider);
            Gdx.graphics.setTitle("Gdx Assistant - (new project)");

            saveMenu.setDisabled(false);
            saveAsMenu.setDisabled(false);
            closeMenu.setDisabled(false);
        }
    }

    private void openProject() {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showErrorDialog(getStage(), "Current pipeline has been modified, close it or save it");
        } else {
            closeProject();

            FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
            fileChooser.setFileTypeFilter(assistantProjectsFilter);
            fileChooser.setModal(true);
            fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
            fileChooser.setListener(new FileChooserAdapter() {
                @Override
                public void selected(Array<FileHandle> file) {
                    FileHandle selectedFile = file.get(0);

                    currentProject = new AssistantProject();
                    currentProject.openProject(selectedFile, pluginsProvider);

                    setProjectFile(selectedFile);

                    saveMenu.setDisabled(false);
                    saveAsMenu.setDisabled(false);
                    closeMenu.setDisabled(false);
                }
            });
            getStage().addActor(fileChooser.fadeIn());
        }
    }

    private void openProject(FileHandle project) {
        if (currentProject != null && currentProject.isDirty()) {
            Dialogs.showErrorDialog(getStage(), "Current pipeline has been modified, close it or save it");
        } else {
            closeProject();

            currentProject = new AssistantProject();
            currentProject.openProject(project, pluginsProvider);

            setProjectFile(project);

            saveMenu.setDisabled(false);
            saveAsMenu.setDisabled(false);
            closeMenu.setDisabled(false);
        }
    }

    private void saveProject() {
        if (currentProject != null) {
            if (projectFile != null) {
                saveProjectToFile(projectFile);
            } else {
                saveProjectAs();
            }
        }
    }

    private void saveProjectToFile(FileHandle file) {
        JsonValue result = currentProject.saveProject();
        try {
            writeJson(file, result);
            currentProject.markSaved();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    private void saveProjectAs() {
        FileChooser fileChooser = new FileChooser(FileChooser.Mode.SAVE);
        fileChooser.setFileTypeFilter(assistantProjectsFilter);
        fileChooser.setModal(true);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> file) {
                FileHandle selectedFile = file.get(0);
                if (!selectedFile.name().toLowerCase().endsWith("." + projectFileExtension)) {
                    selectedFile = selectedFile.parent().child(selectedFile.name() + "." + projectFileExtension);
                }
                saveProjectToFile(selectedFile);
                setProjectFile(selectedFile);
            }
        });
        getStage().addActor(fileChooser.fadeIn());
    }

    private AssistantTab addTab(AssistantApplication assistantApplication, String title, Table content, AssistantPluginTab tab) {
        AssistantTabFromPlugin resultTab = new AssistantTabFromPlugin(assistantApplication, currentProject, tabbedPane, title, content, tab);
        tabbedPane.addTab(resultTab);
        return new AssistantTab() {
            @Override
            public void setTitle(String title) {
                resultTab.setTitle(title);
            }

            @Override
            public void closeTab() {
                if (lastTab == resultTab)
                    lastTab = null;
                tabbedPane.closeTab(resultTab);
            }
        };
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

    private void setProjectFile(FileHandle file) {
        projectFile = file;
        assistantPreferences.addRecentProject(file);
        assistantPreferences.setOpenedProject(file);
        rebuildRecentProjectsMenu();
        Gdx.graphics.setTitle("Gdx Assistant - " + file.name());
    }

    private void doCloseTheProject() {
        if (currentProject != null) {
            currentProject.closeProject();
        }

        currentProject = null;
        projectFile = null;
        assistantPreferences.setOpenedProject(null);
        Gdx.graphics.setTitle("Gdx Assistant");
    }

    public AssistantApplication createApplicationForPlugin(AssistantPlugin assistantPlugin) {
        return new AssistantApplication() {
            private ObjectMap<String, Menu> pluginMenus = new ObjectMap<>();
            private ObjectMap<String, MenuItem> popupMenuItems = new ObjectMap<>();
            private ObjectMap<String, MenuItem> menuItems = new ObjectMap<>();
            private ObjectMap<String, Runnable> listeners = new ObjectMap<>();

            @Override
            public FileHandle getProjectFolder() {
                return AssistantScreen.this.getProjectFolder();
            }

            @Override
            public Skin getApplicationSkin() {
                return AssistantScreen.this.getApplicationSkin();
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

            @Override
            public AssistantTab addTab(String title, Table content, AssistantPluginTab tab) {
                return AssistantScreen.this.addTab(this, title, content, tab);
            }

            @Override
            public void switchToTab(AssistantPluginTab tab) {
                AssistantScreen.this.switchToTab(tab);
            }

            @Override
            public void addWindow(Window window) {
                AssistantScreen.this.getStage().addActor(window);
            }

            @Override
            public FileHandle getInternalResource(String name) {
                return Gdx.files.internal(name);
            }
        };
    }
}

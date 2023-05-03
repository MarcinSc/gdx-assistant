package com.gempukku.gdx.assistant;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class CreateNewProjectDialog extends VisDialog {
    private final String projectFileName;
    private ProjectListener projectListener;

    private final VisTextButton createButton;
    private final VisTextField projectName;
    private final VisTextField projectPath;
    private final VisTextField assetsPath;
    private final VisLabel errorLabel;

    public CreateNewProjectDialog(String projectFileName) {
        super("Create new project");
        this.projectFileName = projectFileName;

        projectName = new VisTextField("");
        projectName.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        validateProject();
                    }
                });

        projectPath = new VisTextField("");
        projectPath.setDisabled(true);
        projectPath.setTouchable(Touchable.disabled);

        assetsPath = new VisTextField("core/assets");
        assetsPath.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        validateProject();
                    }
                });

        errorLabel = new VisLabel("", "small-error");

        VisImageButton createFile = new VisImageButton(VisUI.getSkin().getDrawable("icon-file-text"));
        createFile.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
                        fileChooser.setModal(true);
                        fileChooser.setSelectionMode(FileChooser.SelectionMode.DIRECTORIES);
                        fileChooser.setListener(new FileChooserAdapter() {
                            @Override
                            public void selected(Array<FileHandle> file) {
                                FileHandle selectedFolder = file.get(0);
                                projectPath.setText(selectedFolder.path());
                                if (projectName.getText().equals("")) {
                                    projectName.setText(selectedFolder.name());
                                }
                                validateProject();
                            }
                        });

                        getStage().addActor(fileChooser.fadeIn());
                    }
                });

        Table contentTable = getContentTable();
        contentTable.add("Project name:").left();
        contentTable.add(projectName).colspan(2).growX().row();

        contentTable.add("Project folder:").left();
        contentTable.add(projectPath).growX();
        contentTable.add(createFile).row();

        contentTable.add("Assets path:").left();
        contentTable.add(assetsPath).colspan(2).growX().row();

        contentTable.add(errorLabel).colspan(3).growX().row();

        VisTextButton cancelButton = new VisTextButton("Cancel");
        cancelButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        close();
                    }
                });
        createButton = new VisTextButton("Create");
        createButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        projectListener.projectCreated(projectName.getText(), Gdx.files.absolute(projectPath.getText()), assetsPath.getText());
                        close();
                    }
                });

        getButtonsTable().add(cancelButton);
        getButtonsTable().add(createButton);

        addCloseButton();
        setCenterOnAdd(true);
        pack();
        setWidth(500);

        validateProject();
    }

    public void setProjectListener(ProjectListener projectListener) {
        this.projectListener = projectListener;
    }

    private void validateProject() {
        boolean valid = true;
        if (valid) {
            FileHandle projectFolder = Gdx.files.absolute(projectPath.getText());
            if (!projectFolder.exists() || !projectFolder.isDirectory()) {
                valid = false;
                errorLabel.setText("Please specify project folder");
            }
            if (projectFolder.child(projectFileName).exists()) {
                valid = false;
                errorLabel.setText("There already exists a GDX Assistant project in this folder");
            }
        }
        if (valid) {
            String projectName = this.projectName.getText().trim();
            if (projectName.length() == 0) {
                valid = false;
                errorLabel.setText("Please provide project name");
            }
        }
        if (valid) {
            String assetsPath = this.assetsPath.getText();
            FileHandle assetsFolder = Gdx.files.absolute(projectPath.getText()).child(assetsPath);
            if (!assetsFolder.exists() || !assetsFolder.isDirectory()) {
                valid = false;
                errorLabel.setText("Please specify an existing assets path within the project folder");
            }
        }
        if (valid) {
            errorLabel.setText("");
        }

        createButton.setDisabled(!valid);
        createButton.setTouchable(valid ? Touchable.enabled : Touchable.disabled);
    }

    public interface ProjectListener {
        void projectCreated(String projectName, FileHandle projectFolder, String assetsPath);
    }
}

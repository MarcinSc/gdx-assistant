package com.gempukku.gdx.assistant;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class AssistantPreferences {
    private static final int maxRecentProjectsCount = 10;
    private static final String recentProjectNameKey = "recentProjectName";
    private static final String recentProjectPathKey = "recentProjectPath";
    private static final String openedProjectNameKey = "openedProjectName";
    private static final String openedProjectPathKey = "openedProjectPath";

    private Preferences preferences;

    public AssistantPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void setOpenedProject(String projectName, FileHandle projectFolder) {
        if (projectName != null && projectFolder != null) {
            preferences.putString(openedProjectPathKey, projectFolder.path());
            preferences.putString(openedProjectNameKey, projectName);
        } else {
            preferences.remove(openedProjectPathKey);
            preferences.remove(openedProjectNameKey);
        }
        preferences.flush();
    }

    public List<FileHandle> getRecentProjectFolders() {
        List<FileHandle> result = new ArrayList<>(maxRecentProjectsCount);
        for (int i = 0; i < maxRecentProjectsCount; i++) {
            String recentProject = preferences.getString(recentProjectPathKey + "[" + i + "]", null);
            if (recentProject == null)
                break;
            result.add(Gdx.files.absolute(recentProject));
        }
        return result;
    }

    public List<String> getRecentProjectNames() {
        List<String> result = new ArrayList<>(maxRecentProjectsCount);
        for (int i = 0; i < maxRecentProjectsCount; i++) {
            String recentProjectName = preferences.getString(recentProjectNameKey + "[" + i + "]", null);
            if (recentProjectName == null)
                break;
            result.add(recentProjectName);
        }
        return result;
    }

    public void addRecentProject(String projectName, FileHandle projectFolder) {
        if (projectFolder.type() != Files.FileType.Absolute)
            throw new IllegalArgumentException();

        List<FileHandle> projectFolders = getRecentProjectFolders();
        List<String> projectNames = getRecentProjectNames();

        int index = projectFolders.indexOf(projectFolder);
        if (index > -1) {
            projectFolders.remove(index);
            projectNames.remove(index);
        }

        projectFolders.add(0, projectFolder);
        projectNames.add(0, projectName);

        if (projectFolders.size() > maxRecentProjectsCount) {
            projectFolders.remove(projectFolders.size() - 1);
            projectNames.remove(projectNames.size() - 1);
        }

        for (int i = 0; i < projectFolders.size(); i++) {
            preferences.putString(recentProjectPathKey + "[" + i + "]", projectFolders.get(i).path());
            preferences.putString(recentProjectNameKey + "[" + i + "]", projectNames.get(i));
        }
        preferences.flush();
    }
}

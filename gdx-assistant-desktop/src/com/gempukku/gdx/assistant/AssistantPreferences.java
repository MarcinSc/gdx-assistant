package com.gempukku.gdx.assistant;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class AssistantPreferences {
    private Preferences preferences;
    private int maxRecentProjectsCount = 10;
    private String recentProjectPathKey;

    public AssistantPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public List<FileHandle> getRecentProjects() {
        List<FileHandle> result = new ArrayList<>(maxRecentProjectsCount);
        for (int i = 0; i < maxRecentProjectsCount; i++) {
            recentProjectPathKey = "recentProjectPath";
            String recentProject = preferences.getString(recentProjectPathKey + "[" + i + "]", null);
            if (recentProject == null)
                break;
            result.add(Gdx.files.absolute(recentProject));
        }
        return result;
    }

    public void addRecentProject(FileHandle fileHandle) {
        if (fileHandle.type() != Files.FileType.Absolute)
            throw new IllegalArgumentException();
        List<FileHandle> recentProjects = getRecentProjects();
        recentProjects.remove(fileHandle);
        recentProjects.add(0, fileHandle);
        if (recentProjects.size() > maxRecentProjectsCount)
            recentProjects.remove(recentProjects.size() - 1);

        for (int i = 0; i < recentProjects.size(); i++) {
            preferences.putString(recentProjectPathKey + "[" + i + "]", recentProjects.get(i).path());
        }
        preferences.flush();
    }
}

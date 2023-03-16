package com.gempukku.gdx.plugins;

public class PluginVersion {
    private int major;
    private int minor;
    private int bugfix;
    private String commit;

    public PluginVersion(int major, int minor, int bugfix) {
        this.major = major;
        this.minor = minor;
        this.bugfix = bugfix;
    }

    public PluginVersion(String commit) {
        this.commit = commit;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getBugfix() {
        return bugfix;
    }

    public String getCommit() {
        return commit;
    }
}

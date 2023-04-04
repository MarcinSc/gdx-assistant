package com.gempukku.gdx.assistant;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;

public class WrappingGdxFiles implements Files {
    static public final String externalPath = System.getProperty("user.home") + File.separator;
    static public final String localPath = new File("").getAbsolutePath() + File.separator;

    private ClassLoader classLoader;
    private Files wrapped;

    public WrappingGdxFiles(ClassLoader classLoader, Files wrapped) {
        this.classLoader = classLoader;
        this.wrapped = wrapped;
    }

    @Override
    public FileHandle getFileHandle (String fileName, FileType type) {
        return new Lwjgl3FileHandle(fileName, type);
    }

    @Override
    public FileHandle classpath (String path) {
        return new Lwjgl3FileHandle(path, FileType.Classpath);
    }

    @Override
    public FileHandle internal (String path) {
        return new Lwjgl3FileHandle(path, FileType.Internal);
    }

    @Override
    public FileHandle external (String path) {
        return new Lwjgl3FileHandle(path, FileType.External);
    }

    @Override
    public FileHandle absolute (String path) {
        return new Lwjgl3FileHandle(path, FileType.Absolute);
    }

    @Override
    public FileHandle local (String path) {
        return new Lwjgl3FileHandle(path, FileType.Local);
    }

    @Override
    public String getExternalStoragePath () {
        return externalPath;
    }

    @Override
    public boolean isExternalStorageAvailable () {
        return true;
    }

    @Override
    public String getLocalStoragePath () {
        return localPath;
    }

    @Override
    public boolean isLocalStorageAvailable () {
        return true;
    }

}

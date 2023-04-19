package com.gempukku.gdx.plugins.provider.jar;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;

import java.net.URL;

public class JarsFiles implements Files {
    private ClassLoader classLoader;
    private Files delegate;

    public JarsFiles(ClassLoader classLoader, Files delegate) {
        this.classLoader = classLoader;
        this.delegate = delegate;
    }

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        switch (type) {
            case Classpath:
                return classpath(path);
            case Internal:
                return internal(path);
            case External:
                return external(path);
            case Absolute:
                return absolute(path);
            case Local:
                return local(path);
        }
        return null;
    }

    @Override
    public FileHandle classpath(String path) {
        FileHandle realHandle = delegate.classpath(path);
        URL resource = classLoader.getResource(path);
        if (resource != null) {
            return new URLFileHandle(classLoader, realHandle, resource);
        } else {
            return realHandle;
        }
    }

    @Override
    public FileHandle internal(String path) {
        FileHandle realHandle = delegate.internal(path);
        URL resource = classLoader.getResource(path);
        if (resource != null) {
            return new URLFileHandle(classLoader, realHandle, resource);
        } else {
            return realHandle;
        }
    }

    @Override
    public FileHandle external(String path) {
        return delegate.external(path);
    }

    @Override
    public FileHandle absolute(String path) {
        return delegate.absolute(path);
    }

    @Override
    public FileHandle local(String path) {
        return delegate.local(path);
    }

    @Override
    public String getExternalStoragePath() {
        return delegate.getExternalStoragePath();
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return delegate.isExternalStorageAvailable();
    }

    @Override
    public String getLocalStoragePath() {
        return delegate.getLocalStoragePath();
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return delegate.isLocalStorageAvailable();
    }
}

package com.gempukku.gdx.plugins.provider.jar;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class URLFileHandle extends FileHandle {
    private ClassLoader classLoader;
    private FileHandle delegate;
    private URL url;

    public URLFileHandle(ClassLoader classLoader, FileHandle delegate, URL url) {
        super(delegate.name(), delegate.type());
        this.classLoader = classLoader;
        this.delegate = delegate;
        this.url = url;
    }

    @Override
    public InputStream read() {
        if (url != null) {
            try {
                return url.openStream();
            } catch (IOException exp) {
                throw new GdxRuntimeException("Error opening resource from plugin", exp);
            }
        } else {
            return delegate.read();
        }
    }

    @Override
    public ByteBuffer map() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuffer map(FileChannel.MapMode mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream write(boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream write(boolean append, int bufferSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(InputStream input, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer writer(boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer writer(boolean append, String charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeString(String string, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeString(String string, boolean append, String charset) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list(FileFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list(FilenameFilter filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle[] list(String suffix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileHandle child(String name) {
        FileHandle delegate = this.delegate.child(name);
        URL url = classLoader.getResource(delegate.path());
        return new URLFileHandle(classLoader, delegate, url);
    }

    @Override
    public FileHandle sibling(String name) {
        FileHandle delegate = this.delegate.sibling(name);
        URL url = classLoader.getResource(delegate.path());
        return new URLFileHandle(classLoader, delegate, url);
    }

    @Override
    public FileHandle parent() {
        FileHandle delegate = this.delegate.parent();
        URL url = classLoader.getResource(delegate.path());
        return new URLFileHandle(classLoader, delegate, url);
    }

    @Override
    public void mkdirs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists() {
        if (url != null)
            return true;
        return delegate.exists();
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void emptyDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void emptyDirectory(boolean preserveTree) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void copyTo(FileHandle dest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveTo(FileHandle dest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public long lastModified() {
        throw new UnsupportedOperationException();
    }
}

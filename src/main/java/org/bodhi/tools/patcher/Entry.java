package org.bodhi.tools.patcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class Entry {
    private final JarEntry m_jarEntry;
    private final byte[] m_bytes;

    public static Entry create(JarFile jarFile, JarEntry jarEntry) throws Exception {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        IoUtil.copy(jarFile.getInputStream(jarEntry), byteStream);
        byteStream.close();

        return new Entry(jarEntry, byteStream.toByteArray());
    }


    private Entry(JarEntry jarEntry, byte[] bytes) {
        m_jarEntry = jarEntry;
        m_bytes = bytes;
    }

    public JarEntry getJarEntry() {
        return m_jarEntry;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(m_bytes);
    }

    public String toString() {
        return String.format("%s size %d", m_jarEntry.getName(), m_bytes.length);
    }

}

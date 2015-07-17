package org.bodhi.tools.patcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class Extractor {
    private final Pattern m_pattern;

    public Extractor(String pattern) {
        m_pattern = Pattern.compile(pattern);
    }

    /**
     * Compare the two given files and return the results
     * @param jarFile1 first file
     * @param jarFile2 second file
     * @param md5 true to also check md5 sums
     * @return results of comparison
     */
    public List<Entry> getChanges(Set<String>oldFilenames, Set<String> newFilenames) throws Exception {

        List<Entry> result = new ArrayList<Entry>();

        // Get the maps of <Hashes to Entries>
        Map<String, Entry> e1 = getEntries(oldFilenames);
        Map<String, Entry> e2 = getEntries(newFilenames);

        // Entries only in jarFile2
        for (String hash : SetUtil.difference(e2.keySet(), e1.keySet())) {
            result.add(e2.get(hash));
        }

        return result;
    }


    // Returns a map of Hash -> Entry
    // Jar files are expanded and each JarEntry (that matches a pattern)
    // is added to the map.
    // JarEntry's are usually class files.

    private Map<String, Entry> getEntries(Set<String>filenames) throws Exception {
        Map<String, Entry> result = new HashMap<String, Entry>();

        for (String filename : filenames) {
            
            System.out.println("Extracting: " + filename);

            JarFile jarFile = new JarFile(filename);

            Enumeration<? extends JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String name = jarEntry.getName();
                System.err.println("getEntries: " + name);
                if (m_pattern.matcher(name).matches()) {
                    String hash = Sha1.calculateHash(jarFile.getInputStream(jarEntry));
                    result.put(hash, Entry.create(jarFile, jarEntry));
                }
            }
            
            jarFile.close();
        }
        
        return result;
    }


}

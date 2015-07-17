package ebs.tools.patcher;

import static java.util.jar.Attributes.Name.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CreatePatchFile {

    private static final Name IMPLEMENTATION_PATCH = new Name("Implementation-Patch");

    public static void main(String[] args) {
        Set<String> oldFilenames = new HashSet<String>();
        Set<String> newFilenames = new HashSet<String>();
        String patchFilename = "";
        String product = "";
        String v1 = "";
        String v2 = "";

        // create the command line parser
        CommandLineParser parser = new PosixParser();

        // create the Options
        Options options = new Options();

        options.addOption( OptionBuilder.withArgName("string")
                           .withLongOpt("version1")
                           .hasArgs()
                           .withValueSeparator(' ')
                           .withDescription("old version")
                           .create("1"));

        options.addOption( OptionBuilder.withArgName("string")
                           .withLongOpt("version2")
                           .hasArgs()
                           .withValueSeparator(' ')
                           .withDescription("new version")
                           .create("2"));

        options.addOption( OptionBuilder.withArgName("patchfile")
                           .withLongOpt("file")
                           .hasArgs()
                           .withValueSeparator(' ')
                           .withDescription("patch jar filename")
                           .create("f"));

        options.addOption( OptionBuilder.withArgName("list of files")
                           .withLongOpt("new")
                           .hasArgs()
                           .withValueSeparator(' ')
                           .withDescription("new jar files")
                           .create("n"));

        options.addOption( OptionBuilder.withArgName("list of files")
                           .withLongOpt("old")
                           .hasArgs()
                           .withValueSeparator(' ')
                           .withDescription("old jar files")
                           .create("o"));

        options.addOption( OptionBuilder.withArgName("string")
                           .withLongOpt("product")
                           .hasArgs()
                           .withValueSeparator(' ')
                           .withDescription("product name for manifest")
                           .create("p"));

        options.addOption( "h", "help", false, "print this message.");


        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                new HelpFormatter().printHelp("patch_maker -o oldfiles+ -n newfiles+ -f patchfile -p product -1 [oldversion] -2 [newversion]", options);
                System.exit(-1);
                    
            }

            for (String filename : line.getOptionValues("old")) {
                oldFilenames.add(filename);
            }

            for (String filename : line.getOptionValues("new")) {
                newFilenames.add(filename);
            }
            patchFilename = line.getOptionValue("file");
            product = line.getOptionValue("product");
            v1 = line.getOptionValue("version1");
            v2 = line.getOptionValue("version2");

            System.out.format("v1: %s\n", v1);


        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }

        
        File patchFile = new File(patchFilename);


        Extractor extractor = new Extractor(".*\\.class$");
        try {
            createPatch(patchFile, 
                        createManifest(product, v1, v2),
                        extractor.getChanges(oldFilenames, newFilenames));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Manifest createManifest(String name, String baseVersion, String patchVersion) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM dd yyyy");
        String today = fmt.print(new DateTime());

        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();

        attributes.put(MANIFEST_VERSION, "1.0");

        // BrokerNet Entries

        attributes = new Attributes();
        attributes.put(IMPLEMENTATION_PATCH, patchVersion);
        attributes.put(IMPLEMENTATION_TITLE, name);
        attributes.put(IMPLEMENTATION_VENDOR, "ICAP Development");
        attributes.put(IMPLEMENTATION_VERSION, name + " " + baseVersion + " " + today);
        attributes.put(SPECIFICATION_TITLE, name + " Patch");
        attributes.put(SPECIFICATION_VENDOR, "ICAP");
        attributes.put(SPECIFICATION_VERSION, baseVersion);

        manifest.getEntries().put("BrokerNet", attributes);

        return manifest;
    }

    public static void createPatch(File filename, Manifest manifest, List<Entry> entries) throws Exception {

        JarOutputStream newJar = new JarOutputStream(new FileOutputStream(filename), manifest);

        for (Entry entry : entries) {
            System.out.println("Entry " + entry);

            newJar.putNextEntry(entry.getJarEntry());
            IoUtil.copy(entry.getInputStream(), newJar);
            newJar.closeEntry();
        }

        newJar.close();
    }


}
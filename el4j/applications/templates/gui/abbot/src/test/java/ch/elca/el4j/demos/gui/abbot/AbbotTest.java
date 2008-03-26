package ch.elca.el4j.demos.gui.abbot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.extensions.abbot.ScriptFixture;
import junit.extensions.abbot.ScriptTestSuite;
import junit.framework.Test;
import abbot.script.Script;

public class AbbotTest extends ScriptFixture {
    
    /**
     * <p>
     * The constructor.
     * </p>
     * 
     * @param name
     */
    public AbbotTest(String name) {
        super(name);
    }

    /**
     * <p>
     * Create test suite which each test case coresponds to a script file.
     * </p>
     * @return
     */
    public static Test suite() {
        
        return new ScriptTestSuite(
            AbbotTest.class, findFilenames("target\\test-classes\\abbot", true)) {
            
            public boolean accept(File file) {
                String test = System.getProperty("abbot.runTests", "all");
                if (test.equals("none")) return false;
                String name = file.getName();
                if (!test.equals("all") && !test.contains(name)) return false;
                return name.endsWith(".xml");
            }
        };
    }

    
    /** Add all test scripts in the given directory, optionally recursing to
      * subdirectories. 
      */
    protected static List<String> findTestScripts(
         File dir, List<String> files, boolean recurse) {
         File[] flist = dir.listFiles();
         for (int i=0;flist != null && i < flist.length;i++) {
             if (flist[i].isDirectory()) {
                 if (recurse) {
                     findTestScripts(flist[i], files, recurse);
                 }
             } else if (Script.isScript(flist[i])) {
                 String filename = flist[i].getAbsolutePath();
                 if (!files.contains(filename)) {
                     files.add(filename);
                 }
             }
         }
         return files;
    }
     
    /** 
     * Scan for test scripts and return an array of filenames for all scripts found.
     */
    protected static String[] findFilenames(String dirname, boolean recurse) {
        File dir = new File(dirname);
        List<String> list = new ArrayList<String>();
        if (dir.exists() && dir.isDirectory()) {
            findTestScripts(dir, list, recurse);
        }
        //sort by alphanumeric
        Collections.sort(list);
        return (String[])list.toArray(new String[list.size()]);
    }

}

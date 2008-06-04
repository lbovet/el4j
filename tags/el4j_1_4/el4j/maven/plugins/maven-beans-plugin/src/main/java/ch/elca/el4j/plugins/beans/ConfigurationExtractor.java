/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.plugins.beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts configuration information (inclusive and exclusive configuration
 * locations) from a java or xml file.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhaed (DBD)
 */
public class ConfigurationExtractor {

    /** Marker for the beginning of a configuration include entry. */
    private static final String MARKER_INCLUDE = "$$ BEANS INCLUDE $$";
    
    /** Marker for the beginning of a configuration exclude entry. */
    private static final String MARKER_EXCLUDE = "$$ BEANS EXCLUDE $$";
    
    /** Inclusive conf locations. This is set by a method. */
    private String[] m_inclusive;
    
    /** Exclusive conf locations. This is set by a method. */
    private String[] m_exclusive;
    
    /** The current state. */
    private LineReadingState m_state;
    
    /**
     * Extract configuration from a source file.
     * @param source The file to read from.
     */
    public ConfigurationExtractor(String source) {
        
        File sourceFile = new File(source);
        if (!sourceFile.exists() || !sourceFile.canRead()) {
            throw new RuntimeException("Reading from source file impossible.");
        }
        
        BufferedReader r = getReader(sourceFile);
        
        if (source.endsWith(".java")) {
            try {
                readJava(r);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new Error("Not yet implemented.");
        }
    }
    
    /**
     * Get a reader for the file, handle exceptions.
     * @param sourceFile The file.
     * @return A BufferedReader for the file.
     */
    private BufferedReader getReader(File sourceFile) {
        BufferedReader r;
        try {
            r = new BufferedReader(new FileReader(sourceFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } 
        return r;
    }
    
    /**
     * Read configuration information from a .java file. 
     * @param r A reader for the file.
     */
    private void readJava(BufferedReader r) throws IOException {
        
        String line;
        m_state = new SearchingState();
        while ((line = r.readLine()) != null) {
            m_state = m_state.processLine(line);
        }
    }

    /**
     * State in the line reading state machine.
     */
    interface LineReadingState {
        /**
         * Process a line.
         * @param line The line to process.
         * @return the new state. Can be <code>this</code>.
         */
        LineReadingState processLine(String line);
    }
    
    /**
     * STATE : We are searching for a marker to begin reading at.
     */
    class SearchingState implements LineReadingState {
        
        /** {@inheritDoc} */
        public LineReadingState processLine(String line) {
            if (line.contains(MARKER_INCLUDE)) {
                return new ReadingState(true);
            } else if (line.contains(MARKER_EXCLUDE)) {
                return new ReadingState(false);
            } else {
                return this;
            }
        }
    }
    
    /**
     * STATE : We saw a "begin" marker. Now we read until the end.
     */
    class ReadingState implements LineReadingState {
        
        /** Whether we are collecting inclusive or exclusive data. */
        private boolean m_include;
        
        /** The data we collect. */
        private List<String> m_data;
        
        /** 
         * @param inclusive 
         * Whether we are collecting inclusive or exclusive data.
         */
        public ReadingState(boolean inclusive) {
            m_include = inclusive;
            m_data = new LinkedList<String>();
        }
        
        /** {@inheritDoc} */
        public LineReadingState processLine(String line) {
            final String regex = ".*\"(.*)\".*";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(line);
            if (m.matches()) {
                m_data.add(m.group(1));
            }
            if (line.contains("}")) {
                String[] result = m_data.toArray(new String[0]);
                if (m_include) {
                    m_inclusive = result;
                } else {
                    m_exclusive = result;
                }
                return new SearchingState();
            } else {
                return this;
            }
        }
    }
    
    /**
     * @return Returns the inclusive configuration.
     */
    public String[] getInclusive() {
        return m_inclusive;
    }

    /**
     * @return Returns the exclusive configuration.
     */
    public String[] getExclusive() {
        return m_exclusive;
    }
    
    
}

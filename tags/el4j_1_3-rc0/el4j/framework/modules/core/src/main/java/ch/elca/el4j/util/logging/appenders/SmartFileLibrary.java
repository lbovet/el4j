/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.logging.appenders;

import java.io.File;
import java.io.IOException;

import org.springframework.util.StringUtils;

/**
 * 
 * This provides methods to check and construct a valid
 * logfile path. Setting an absolute log file path that works in all 
 * environments is typically hard. This Appender-wrapper creates such
 * an absolute log file path.
 *  
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 */
public class SmartFileLibrary {
    
    /**
     * Hide the default constructor as this is a utility class.
     */
    protected SmartFileLibrary() { }
    
    /**
     * This method tries to create a valid logfile path from the given input.
     * @param fileName The fileName.
     * @return A valid filePath.
     * @throws IOException Throws IOException, if any input invalid.
     */
    public static String createSmartLogPath(String fileName) 
        throws IOException {
        
        String finalFileName = fileName;
        
        // if fileName is empty throw IOException
        if (!StringUtils.hasText(finalFileName)) {
            throw new IOException("The log-fileName can not be empty!");
        }
        
        File logFile = new File(finalFileName);
        
        // if the fileName does not contain an absolute path,
        // we need to assign a directory to that file.
        if (!logFile.isAbsolute()) {
            String logDir;
            logDir = System.getProperty("el4j.log.dir");
            
            // if the 'el4j.log.dir' system property exist, try
            // to use it as the directory else try to use the tempdir
            // of JRE
            if (logDir != null) {
                finalFileName = constructPath(logDir , finalFileName);
            } else {
                logDir = System.getProperty("java.io.tmpdir");
                finalFileName = constructPath(logDir , finalFileName);
            }
        }
        return finalFileName;
    }
    
    /**
     * Tries to construct a valid filePath, for the given fileName 
     * and direcotry.
     * @param directoryPath The directoryPath.
     * @param fileName The fileName.
     * @return An absolute filePath.
     * @throws IOException
     */
    private static String constructPath(String directoryPath, String fileName)
        throws IOException {
        
        // if directoryPath is empty throw IOException
        if (!StringUtils.hasText(directoryPath)) {
            throw new IOException("The directoryPath can not be empty!");
        }
        
        // if fileName is empty throw IOException
        if (!StringUtils.hasText(fileName)) {
            throw new IOException("The fileName can not be empty!");
        }
        
        String d = directoryPath.replace('\\', '/');
        String f = extractRelativePath(fileName.replace('\\', '/'));
        
        File dFile = new File(d);
        // if the directory does not exist, throw IOException
        if (!dFile.isDirectory()) {
            throw new IOException("The directory [" 
                + directoryPath + "] does not exist!");
        }
        
        // if directoryPath does not already contain
        // any pathSeperator at the end, add a pathSeperator
        if (directoryPath.endsWith("/")) {
            return d + f;
        } else {
            return d + '/' + f;
        }
    }
    
    /**
     * Trim fileName and remove fileSeperator symbols
     * at the begining of the fileName and check, 
     * if the resulting fileName is not empty.
     * The resulted fileName is given back.
     * @param fileName The input filename.
     * @return A valid filename.
     * @throws IOException Throws IOException, if fileName is not valid.
     */
    private static String extractRelativePath(String fileName) 
        throws IOException {
        String result = fileName;
        
        // remove all pathSeperator symbols infont of the 
        // fileName/relative-path
        while (result.startsWith("/")) {
            result = result.substring(1 , result.length());
        }
        
        result = result.trim();
        
        // if the fileName only contained whitespace characters and 
        // pathSeperator symbols, then the fileName is not valid...
        if (!StringUtils.hasLength(result)) {
            throw new IOException("The fileName is not valid!");
        }
        
        return result;
    }
}

/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.plugins.filecollector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * This class defines some supporting operations for file and directory
 * modification (copy, move, delete, rename, read and write operations). 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class FileModification {
    
    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(FileModification.class);
    
    /**
     *  Default charset to use for both reading and writing files.
     */
    private static final String CHARSET = "ISO-8859-1";
    
    /**
     * The size of the buffer to use in <code>copyFile()</code>.
     */
    private static final int COPY_FILE_BUFFER_SIZE = 1024;
    
    /**
     * The number of loops until a heartbeat should be sent to the
     * <code>CopyHearbeatListener</code>.
     */
    private static final int MAX_HEARTBEAT_COUNTER = 1024;
    
    /**
     * Hide default constructor.
     */
    protected FileModification() { };
    
    /**
     * Convenience method which closes an <code>OutputStream</code> and
     * catches a potential exception. <br>
     * The method is useful in finally-blocks where more than one reader, writer
     * or stream has to be closed in order to avoid memory leaks.
     * 
     * @param outputStream
     *            The <code>OutputStream</code> to be closed.
     */
    private static final void closeOutputStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            } // if
        } catch (IOException ioe) {
            // The exception is ignored.
            s_logger.warn("Cannot close output stream", ioe);
        } // try
    } // closeOutputStream()
    
    /**
     * Convenience method which closes an <code>InputStream</code> and catches
     * a potential exception. <br>
     * The method is useful in finally-blocks where more than one reader, writer
     * or stream has to be closed in order to avoid memory leaks.
     * 
     * @param inputStream
     *            The <code>InputStream</code> to be closed (may even be
     *            'null')
     */
    private static final void closeInputStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            } // if
        } catch (IOException ioe) {
            // The exception is ignored.
            s_logger.warn("Cannot close input stream", ioe);
        } // try
    } // closeInputStream()
    
    /**
     * Convenience method which flushes an <code>OutputStream</code> and
     * catches a potential exception. <br>
     * The method is useful in finally-blocks where more than one reader, writer
     * or stream has to be flushed in order to avoid memory leaks.
     * 
     * @param outputStream
     *            the <code>OutputStream</code> to be closed.
     */
    private static void flushOutputStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.flush();
            } // if
        } catch (IOException ioe) {
            // The exception is ignored.
            s_logger.warn("Cannot flush output stream", ioe);
        } // try
    } // flushOutputStream()
    
    /**
     * Convenience method which closes a Reader and catches a potential
     * Exception. <br>
     * The method is useful in finally-blocks where more than one reader,
     * writer or stream has to be closed in order to avoid memory leaks.
     * 
     * @param reader The reader to be closed (may even be 'null')
     */
    private static final void closeReader(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            } // if
        } catch (IOException ioe) {
            // The exception is ignored.
            s_logger.warn("Cannot close reader", ioe);
        } // try
    } // closeReader()
    
    /**
     * Convenience method which closes a Writer and catches a potential
     * Exception. <br>
     * The method is useful in finally-blocks where more than one reader or
     * writer has to be closed in order to avoid memory leaks.
     * 
     * @param writer
     *            the writer to be closed.
     */
    private static void closeWriter(Writer writer) {
        try {
            if (writer != null) {
                writer.close();
            } // if
        } catch (IOException exception) {
            // The exception is ignored.
            s_logger.warn("Cannot close writer", exception);
        } // try
    } // closeWriter()

    /**
     * Convenience method which flushes a Writer and catches a potential
     * Exception. <br>
     * The method is useful in finally-blocks where more than one reader or
     * writer has to be flushed in order to avoid memory leaks.
     * 
     * @param writer
     *            the writer to be closed.
     */
    private static void flushWriter(Writer writer) {
        try {
            if (writer != null) {
                writer.flush();
            } // if
        } catch (IOException exception) {
            // The exception is ignored.
            s_logger.warn("Cannot flush writer", exception);
        } // try
    } // flushWriter()
    
    /**
     * This method copies a file to the specified location.
     * 
     * @param from
     *            the file to copy
     * @param to
     *            the location where to copy the file (may be a file or a
     *            directory)
     * @param overwrite
     *            if the target file exists, should it be overwritten? If the
     *            target file exists and overwriteFiles is false, no file is
     *            copied
     * @throws FileException
     *             if a problem occurs when copying the directory.
     */
    public static void copyFile(File from, File to, boolean overwrite) 
        throws FileException {
        copyFile(from, to, overwrite, null);
    }
    
    /**
     * This method copies a file to the specified location. To support huge data
     * in a supervised environment, the method accepts a passed listener which
     * is periodically invoked during the copy-process. The listener is invoked
     * after each copied MegaByte. This way, a supervised thread which has to
     * send heartbeats itself, may register such a listener and send a heartbeat
     * anytime this listener is invoked.
     * 
     * @param from
     *            the file to copy
     * @param to
     *            the location where to copy the file (may be a file or a 
     *            directory)
     * @param overwrite
     *            if the target file exists, should it be overwritten? If the
     *            target file exists and overwriteFiles is false, no file is
     *            copied
     * @param listener
     *            the listener which is periodically called during the
     *            copy-process
     * @throws FileException
     *             if a problem occurs when copying the directory.
     */
    public static void copyFile(File from, File to, boolean overwrite,
        CopyHeartbeatListener listener) throws FileException {

        if (s_logger.isDebugEnabled()) {
            s_logger.debug("copyFile method invoked with from:" + from
                + " / to:" + to);
        }

        // make sure from is a directory
        if (!from.isFile()) {

            if (s_logger.isWarnEnabled()) {
                s_logger.warn("From-file " + from.getAbsolutePath()
                    + " is not a file");
            }

            throw new FileException("From-file {0} is not a file",
                new Object[] {from.getAbsolutePath()});

        } else {

            File targetDir = null;
            File targetFile = null;
            FileInputStream fis = null;
            FileOutputStream fos = null;

            try {
                if (from.exists()) {
                    if (!to.isDirectory()) {
                        targetDir = to.getParentFile();
                        targetFile = to;
                    } else {
                        targetDir = to;
                        targetFile = new File(targetDir, from.getName());
                    }
                    // check if the target directory exists
                    if (!targetDir.exists()) {

                        if (s_logger.isDebugEnabled()) {
                            s_logger.debug("TargetDir does not exist, "
                                + "create it");
                        }

                        targetDir.mkdirs();
                    }

                    fis = new FileInputStream(from);
                    if (overwrite || !targetFile.exists()) {

                        File tempTargetFile = new File(targetFile
                            .getAbsolutePath() + ".tmp");
                        fos = new FileOutputStream(tempTargetFile);
                        byte[] buffer = new byte[COPY_FILE_BUFFER_SIZE];
                        int nbrOfBytesRead = fis.read(buffer);
                        int heartbeatCounter = 0;
                        while (nbrOfBytesRead > 0) {
                            fos.write(buffer, 0, nbrOfBytesRead);
                            nbrOfBytesRead = fis.read(buffer);
                            // increment counter to determine if I should notify
                            // the hearbeat-listener
                            heartbeatCounter++;
                            if (heartbeatCounter > MAX_HEARTBEAT_COUNTER) {
                                // notify the listener
                                if (listener != null) {
                                    listener.receiveHeartbeat();
                                }
                                heartbeatCounter = 0;
                            }
                        }
                        // release the file-handles so we can rename the file
                        flushOutputStream(fos);
                        closeOutputStream(fos);
                        fos = null;
                        closeInputStream(fis);
                        fis = null;

                        // rename the file from ...xyz.tmp to ...xyz
                        // make sure the targetFile doesn't exist. If so, we
                        // delete it
                        targetFile.delete();
                        tempTargetFile.renameTo(targetFile);
                        // make sure the temp-file doesn't exist (if the
                        // renameTo failed)
                        tempTargetFile.delete();
                        if (s_logger.isDebugEnabled()) {
                            s_logger.debug("File copied");
                        }
                    } else {
                        if (s_logger.isDebugEnabled()) {
                            s_logger
                                .debug("Target exists and overwrite is false, "
                                    + "no files copied");
                        }
                    }
                } else {
                    if (s_logger.isWarnEnabled()) {
                        s_logger.warn("File " + from + " does not exist");
                    }
                    throw new FileException("File {0} does not exist",
                        new Object[] {from});
                }
            } catch (FileNotFoundException fnfe) {
                throw new FileException(fnfe);
            } catch (IOException ioe) {
                throw new FileException(ioe);
            } finally {
                flushOutputStream(fos);
                closeOutputStream(fos);
                closeInputStream(fis);
                // make sure that there is at least one
            }
        }
    }
    
    /**
     * This method copies the whole directory to the specified location. It also
     * recursively copies any subdirectories.
     * 
     * @param from
     *            the directory to copy
     * @param to
     *            the location where to copy the directory (must be a directory)
     *            - this directory will be the parent directory of the copied
     *            directory
     * @param overwriteFiles
     *            if a target file exists, should it be overwritten? If the
     *            target file exists and overwriteFiles is false, no file is
     *            copied
     * @throws FileException
     *             if a problem occurs when copying the directory.
     */
    public static void copyDir(File from, File to, boolean overwriteFiles)
        throws FileException {
        copyDir(from, to, overwriteFiles, null);
    }
    
    /**
     * This method copies the whole directory to the specified location. It also
     * recursively copies any subdirectories. To support huge data in a
     * supervised environment, the method accepts a passed listener which is
     * periodically invoked during the copy-process. The listener is invoked
     * after each copied file and during copying a file: after each copied
     * MegaByte. This way, a supervised thread which has to send heartbeats
     * itself, may register such a listener and send a heartbeat anytime this
     * listener is invoked.
     * 
     * @param from
     *            the directory to copy
     * @param to
     *            the location where to copy the directory (must be a directory)
     *            - this directory will be the parent directory of the copied
     *            directory
     * @param overwriteFiles
     *            if a target file exists, should it be overwritten? If the
     *            target file exists and overwriteFiles is false, no file is
     *            copied
     * @param listener
     *            the listener which is periodically called during the
     *            copy-process
     * @throws FileException
     *             if a problem occurs when copying the directory.
     */
    public static void copyDir(File from, File to, boolean overwriteFiles,
        CopyHeartbeatListener listener) throws FileException {

        File destination = new File(to, from.getName());
        
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("copyDir method invoked with from:" + from
                + " / to:" + to);
        }

        // make sure from is a directory
        if (!from.isDirectory()) {

            if (s_logger.isWarnEnabled()) {
                s_logger.warn("From-directory " + from.getAbsolutePath()
                    + " is not a directory");
            }

            throw new FileException("From-directory {0} is not a directory",
                new Object[] {from.getAbsolutePath()});

        } else if (!to.isDirectory()) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("To-directory " + to.getAbsolutePath()
                    + " is not a directory");
            }

            throw new FileException("To-directory {0} is not a directory",
                new Object[] {to.getAbsolutePath()});
        } else {
            File[] fromFiles = from.listFiles();
            if (!to.exists()) {
                to.mkdir();
            }
            
            if (!destination.exists()) {
                destination.mkdir();
            }
            
            // file.listFiles() may return null if file doesn't point to a
            // directory
            // or an IO-error occurs
            if (fromFiles == null) {

                if (s_logger.isWarnEnabled()) {
                    s_logger.warn("Could not list directory "
                        + from.getAbsolutePath());
                }
                
                throw new FileException("Could not list directory {0}",
                    new Object[] {from.getAbsolutePath()});

            } else {
                for (int i = 0; i < fromFiles.length; i++) {
                    if (fromFiles[i].isDirectory()) {
                        copyDir(fromFiles[i], destination, overwriteFiles,
                            listener);
                    } else {
                        copyFile(fromFiles[i], destination, overwriteFiles,
                            listener);
                    }
                    // notify the listener
                    if (listener != null) {
                        listener.receiveHeartbeat();
                    }
                }
            }
        }
    }
   
    /**
     * This method moves a file to the specified location. It first copies the
     * file to the destination, then deletes it from its initial location.
     * 
     * @param from
     *            the file to move
     * @param to
     *            the location where to move the file (may be a file or a
     *            directory)
     * @param overwrite
     *            if the target file exists, should it be overwritten? If the
     *            target file exists and overwriteFiles is false, no file is
     *            moved
     * @throws FileException
     *             if a problem occurs when copying the directory.
     */
    public static void moveFile(File from, File to, boolean overwrite)
        throws FileException {
        moveFile(from, to, overwrite, null);
    }
    
    /**
     * This method moves a file to the specified location. It first copies the
     * file to the destination, then deletes it from its initial location. To
     * support huge data in a supervised environment, the method accepts a
     * passed listener which is periodically invoked during the copy-process.
     * The listener is invoked after each copied MegaByte. This way, a
     * supervised thread which has to send heartbeats itself, may register such
     * a listener and send a heartbeat anytime this listener is invoked.
     * 
     * @param from
     *            the file to move
     * @param to
     *            the location where to move the file (may be a file or a
     *            directory)
     * @param overwrite
     *            if the target file exists, should it be overwritten? If the
     *            target file exists and overwriteFiles is false, no file is
     *            moved
     * @param listener
     *            the listener which is periodically called during the
     *            copy-process
     * @throws FileException
     *             if a problem occurs when copying the directory.
     */
    public static void moveFile(File from, File to, boolean overwrite,
        CopyHeartbeatListener listener) throws FileException {
        try {
            copyFile(from, to, overwrite, listener);
            from.delete();
        } catch (FileException fe) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not move file " + from.getAbsolutePath()
                    + " to " + to.getAbsolutePath());
            }

            throw new FileException("Could not move file {0} to {1}",
                new Object[] {from.getAbsolutePath(), to.getAbsolutePath()},
                    fe);
        }
    }
    
    /**
     * This method moves a directory to the specified location. It first copies
     * the directory to the destination, then deletes it from its initial
     * location.
     * 
     * @param from
     *            the directory to move
     * @param to
     *            the location where to copy the directory (must be a directory)
     *            - this directory will be the parent directory of the copied
     *            directory
     * @param overwrite
     *            if the target directory exists, should it be overwritten? If
     *            the target file exists and overwriteFiles is false, no file is
     *            moved
     * @throws FileException
     *             if a problem occurs when moving the directory.
     */
    public static void moveDir(File from, File to, boolean overwrite) 
        throws FileException {
        moveDir(from, to, overwrite, null);
    }
    
    /**
     * This method moves a directory to the specified location. It first copies
     * the directory to the destination, then deletes it from its initial
     * location. To support huge data in a supervised environment, the method
     * accepts a passed listener which is periodically invoked during the
     * copy-process. The listener is invoked after each copied MegaByte. This
     * way, a supervised thread which has to send heartbeats itself, may
     * register such a listener and send a heartbeat anytime this listener is
     * invoked.
     * 
     * @param from
     *            the directory to move
     * @param to
     *            the location where to copy the directory (must be a directory)
     *            - this directory will be the parent directory of the copied
     *            directory
     * @param overwrite
     *            if the target directory exists, should it be overwritten? If
     *            the target file exists and overwriteFiles is false, no file is
     *            moved
     * @param listener
     *            the listener which is periodically called during the
     *            copy-process
     * @throws FileException
     *             if a problem occurs when moving the directory.
     */
    public static void moveDir(File from, File to, boolean overwrite,
        CopyHeartbeatListener listener) throws FileException {
        try {
            copyDir(from, to, overwrite, listener);
            deleteDir(from);
        } catch (FileException fe) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not move directory "
                    + from.getAbsolutePath() + " to " + to.getAbsolutePath());
            }

            throw new FileException("Could not move file {0} to {1}",
                new Object[] {from.getAbsolutePath(), to.getAbsolutePath()},
                fe);
        }
    }
    
    /**
     * Method <code>deleteFile</code> deletes a file in a
     * much better way than <code>File.delete()</code> does.
     *
     * @param file the <code>File</code> to delete
     * @exception FileException if an error occurs
     */
    public static void deleteFile(File file) throws FileException {
        
        // Does file exist and is it a file ...
        if (file != null && !file.exists()) {
            // Source file for deletion does not exist
          
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not delete file "
                    + file + " because it does not exist");
            }
            
            throw new FileException("Could not delete file {0} because it "
                + "does not exist", new Object[] {file});
            
        } else if (file != null && !file.isFile()) {
            // Source file is no file    
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not delete file "
                    + file + " because it is not a valid file");
            }
            
            throw new FileException("Could not delete file {0} because it "
                + "is not a valid file", new Object[] {file});
            
        }

        // Try to delete the file ...
        if (file != null && !file.delete()) {
            
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not delete file " + file);
            }
            
            throw new FileException("Could not delete file {0}",
                new Object[] {file});
        }
    }
     
    /**
     * Method <code>deleteDir</code> deletes a directory including
     * subdirectories and files.
     * 
     * @param directory
     *            the directory to delete
     * @exception FileException
     *                if an error occurs
     */
    public static void deleteDir(File directory) throws FileException {
    // Does directory exist and is it a directory ...
        if (directory != null && !directory.exists()) {
            // Source file for deletion does not exist
          
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not delete directory "
                    + directory + " because it does not exist");
            }
            
            throw new FileException("Could not delete directory {0} because it "
                + "does not exist", new Object[] {directory});
            
        } else if (directory != null && !directory.isDirectory()) {
            // Source file is no file    
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not delete directory "
                    + directory + " because it is not a valid directory");
            }
            
            throw new FileException("Could not delete directory {0} because it "
                + "is not a valid directory", new Object[] {directory});
            
        }
        
        //Try to delete all files and subdirectories in the directory to delete
        File[] files = directory.listFiles();
        // listFiles returns null if directory is not accessible ...
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i]);
                } else {
                    if (!files[i].delete()) {
                        if (s_logger.isWarnEnabled()) {
                            s_logger.warn("Could not delete file " + files[i]);
                        }
                        
                        throw new FileException("Could not delete file {0}",
                            new Object[] {files[i]});
                    }
                }
            }
        }
        //directory is empty now, delete it
        directory.delete();
    }
    
    /**
     * <p>
     * Renames a file.
     * </p>
     * <p>
     * This will remove <code>to</code> (if it exists) and move
     * <code>from</code>, which involves deleting <code>from</code> as
     * well. Whether or not this method can move a file from one filesystem to
     * another is platform-dependent.
     * </p>
     * 
     * @param from
     *            The file to rename.
     * @param to
     *            The file with the new file name.
     * @param overwrite
     *            Wether the destination file should be overwritten if it
     *            already exists (attention: this is platform-dependent - on
     *            some platforms, a file will always be overwritten!)
     * @throws FileException
     *             if source file could not be renamed.
     */
    public static final void renameFile(File from, File to, boolean overwrite)
        throws FileException {

        if (overwrite && to.exists() && to.isFile()) {
            if (!to.delete()) {

                if (s_logger.isWarnEnabled()) {
                    s_logger.warn("Could not delete file "
                        + to.getAbsolutePath());
                }
                throw new FileException("Could not delete file {0}",
                    new Object[] {to.getAbsolutePath()});

            }
        }

        if (!from.renameTo(to)) {

            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not rename file " + from.getAbsolutePath()
                    + " to " + to.getAbsolutePath());
            }

            throw new FileException("Could not rename file {0} to {1}",
                new Object[] {from.getAbsolutePath(), to.getAbsolutePath()});
        }
        
    }
    
    /**
     * <p>
     * Renames a directory.
     * </p>
     * <p>
     * This will remove <code>to</code> (if it exists) and move
     * <code>from</code>, which involves deleting <code>from</code> as
     * well. Whether or not this method can move a directory from one filesystem
     * to another is platform-dependent.
     * </p>
     * 
     * @param from
     *            The directory to rename.
     * @param to
     *            The directory with the new directory name.
     * @param overwrite
     *            Wether the destination directory should be overwritten if it
     *            already exists.
     * @throws FileException
     *             if source directory could not be renamed.
     */
    public static final void renameDir(File from, File to, boolean overwrite)
        throws FileException {
        if (overwrite && to.exists() && to.isDirectory()) {
            deleteDir(to);
        }
        if (!from.renameTo(to)) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not rename directory "
                    + from.getAbsolutePath() + " to "
                    + to.getAbsolutePath());
            }

            throw new FileException("Could not rename directory {0} to {1}",
                new Object[] {from.getAbsolutePath(), to.getAbsolutePath()});
        }   
    }
    
    /**
     * Method <code>readFileAsString</code> reads the content of a file and
     * returns the content as String. The content is encoded with the default
     * encoding of this system.
     * 
     * @param file
     *            the file to read from
     * @return the content of this file as String
     * @exception FileException
     *                if an exception occurs
     */
    public static String readFileAsString(File file) throws FileException {
        return readFileAsString(file, null);
    }

    /**
     * Method <code>readFileAsString</code> reads the content of a file
     * and returns the content as String. The content is encoded with the given
     * encoding of this system.
     * 
     * @param file
     *            the file to read from
     * @param charset
     *            the encoding to use
     * @return the content of this file as String
     * @exception FileException
     *                if an exception occurs
     */
    public static String readFileAsString(File file, String charset)
        throws FileException {
        byte[] data = readFile(file);
        String result = null;
        try {
            if (charset == null) {
                result = new String(data);
            } else {
                result = new String(data, charset);
            }
        } catch (UnsupportedEncodingException uee) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("There was an error reading the file " + file);
            }
            
            throw new FileException("There was an error reading the file {0}",
                new Object[] {file});
        }
        return result;
    }

    /**
     * Method <code>readFile</code> reads the content of a file and returns
     * the content as byteArray.<br>
     * <b>Warning: This method should not be used with too large files! All the
     * content is read into the memory which might cause problems!</b>
     * 
     * @param file
     *            the file to read from
     * @return the content of this file as byte[]
     * @exception FileException
     *                if an exception occurs
     */
    public static byte[] readFile(File file) throws FileException {
        byte[] buffer = null;

        if (!file.exists()) {
            // File does not exist
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("The file " + file.getAbsolutePath()
                    + " does not" + "exist");
            }

            throw new FileException("The file {0} does not exist",
                new Object[] {file.getAbsolutePath()});

        } else if (file.isDirectory()) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("The file " + file.getAbsolutePath()
                    + " exists, " + "but it is a directory");
            }

            throw new FileException("The file {0} exists, but it is a"
                + " directory", new Object[] {file.getAbsolutePath()});

        } else {
            FileInputStream input = null;
            try {
                input = new FileInputStream(file);
                // create a byte array which is large enough ...
                // hope there will be no files with .. 2GB size!!!
                buffer = new byte[(int) file.length()];
                int readBytes = 0;
                int pos = 0;

                // Do only read if the file is not empty ... else
                // it will give a infinite loop!!!!
                if (buffer.length != 0) {
                    while (true) {
                        readBytes = input
                            .read(buffer, pos, buffer.length - pos);
                        if (readBytes == -1) {
                            // End of stream reached
                            break;
                        } else {
                            pos = readBytes;
                        }
                    }
                }
            } catch (IOException exception) {
                // Error reading the file
                if (s_logger.isWarnEnabled()) {
                    s_logger.warn("Error occurred while reading file "
                        + file.getAbsolutePath(), exception);
                }

                throw new FileException(
                    "Error occurred while reading file {0}", new Object[] {file
                        .getAbsolutePath()}, exception);

            } finally {
                // Close all readers and writers to avoid memory leaks
                closeInputStream(input);
            }
        }
        return buffer;
    }
    
    /**
     * Write the specified String content to the specified file.
     * 
     * @param file
     *            the file to write into.
     * @param content
     *            the text to write.
     * @exception FileException
     *                if an exception occurs
     */
    public static void writeToFile(File file, String content) throws
        FileException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
        } catch (IOException ioe) {

            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Error occurred while writing file "
                    + file.getAbsolutePath(), ioe);
            }

            throw new FileException("Error occurred while writing file {0}",
                new Object[] {file.getAbsolutePath()}, ioe);

        } finally {
            flushWriter(fileWriter);
            closeWriter(fileWriter);
        }
    }
    
    /**
     * This method writes the content of a <code>Reader</code> to a file.
     * 
     * @param reader
     *            The Reader.
     * @param file
     *            The file to write source Reader to.
     * @param outputCharset
     *            the character set of the file to write
     * @throws FileException
     *             if file could not be written.
     */
    public static final void writeToFile(Reader reader, File file, 
                                   String outputCharset) throws FileException {
        
        final int BUFFER_SIZE = 4096;
        final int EOS = -1;
        
        File temporaryFile = null;
        BufferedWriter writer = null;
        
        try {
            
            temporaryFile = File.createTempFile(getPrefix(file),
                "." + getExtension(file));
            
            // Write to a temporary file
            FileOutputStream fos = new FileOutputStream(temporaryFile);
            OutputStreamWriter osw;
            osw = new OutputStreamWriter(fos, Charset.forName(outputCharset));

            writer = new BufferedWriter(osw);
    
            int charCount = BUFFER_SIZE;
            char[] buffer = new char[charCount];
            
            while ((charCount = reader.read(buffer, 0, charCount)) != EOS) {
                writer.write(buffer, 0, charCount);
            } // while
            
        } catch (Exception e) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Error occurred while writing file "
                    + file.getAbsolutePath(), e);
            }

            throw new FileException("Error occurred while writing file {0}",
                new Object[] {file.getAbsolutePath()}, e);
            
        } finally {
            closeReader(reader);
            closeWriter(writer);
        } // try
        
        // Rename temporary filename
        renameFile(temporaryFile, file, true);
    } // writeToFile()
     
    /**
     * This method writes the content of a <code>Reader</code> to a file with
     * the default encoding (ISO-8859-1).
     * 
     * @param reader
     *            The Reader.
     * @param file
     *            The file to write source Reader to.
     * @throws FileException
     *             if file could not be written.
     */
    public static final void writeToFile(Reader reader, File file)
        throws FileException {
        writeToFile(reader, file, CHARSET);
    }
    
    /**
     * This method writes the content of an <code>InputStream</code> to a
     * file.
     * 
     * @param is
     *            The source InputStream.
     * @param file
     *            The file to write source InputStream to.
     * @param inputCharset
     *            the character set of the input stream
     * @param outputCharset
     *            the character set of the file to write
     * @throws FileException
     *             if file could not be written.
     */
    public static final void writeToFile(InputStream is, File file,
        String inputCharset, String outputCharset) throws FileException {

        InputStreamReader isr;
        isr = new InputStreamReader(is, Charset.forName(inputCharset));
        Reader reader = new BufferedReader(isr);

        try {
            writeToFile(reader, file, outputCharset);
        } catch (Exception e) {
            
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Error occurred while writing file "
                    + file.getAbsolutePath(), e);
            }

            throw new FileException("Error occurred while writing file {0}",
                new Object[] {file.getAbsolutePath()}, e);
            
        } // try
    } // writeToFile()
    
    /**
     * This method returns the extension of a file's filename (for example
     * ".xml" of "C:\xyz\test.xml").
     * 
     * @param file
     *            The file to get the filename extension from
     * @return The filename extension of the given file
     */
    public static String getExtension(File file) {
        String filename = file.getName();
        String result = "";
        int index = filename.lastIndexOf(".");
        if (index != -1) {
            result = filename.substring(index);
        }
        return result;
    }

    /**
     * This method returns the prefix of a file's filename, i.e. the filename
     * without its extension (for example "test" of "C:\xyz\test.xml").
     * 
     * @param file
     *            The file to get the filename prefix from
     * @return The prefix of the filename the given file
     */
    public static String getPrefix(File file) {
        String filename = file.getName();
        if (filename != null) {
            int index = filename.lastIndexOf(".");
            if (index != -1) {
                return filename.substring(0, index);
            }
        }
        return filename;
    }
    
    /**
     * This method changes the extension of a file's filename (renames for
     * example "C:\xyz\test.xml" to "C:\xyz\test.txt").
     * 
     * @param file
     *            The file for which the filename extension has to be changed.
     * @param newExtension
     *            The file's new filename extension.
     * @return The file with the new extension
     * @throws FileException
     *             if file extension could not be changed.
     */
    public static File changeExtension(File file, String newExtension) throws
        FileException {
        String filename = file.getAbsolutePath();
        File renamedFile = null;
        if (filename != null) {
            int index = filename.lastIndexOf(".");
            if (index != -1) {
                renamedFile = new File(filename.substring(0, index)
                    + newExtension);
                renameFile(file, renamedFile, true); 
            }
        }
        if (renamedFile == null) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Could not rename file "
                    + file.getAbsolutePath());
            }

            throw new FileException("Could not rename file {0}",
                new Object[] {file.getAbsolutePath()});
        }
        return renamedFile;
    }
    
    /**
     * This method replaces all the invalid characters in a filename by a '_'.
     * 
     * @param fileName
     *            The filename for which the invalid characters will be removed
     * @return the filename where every invalid character has been replaced by a
     *         '_'
     */
    public static String removeInvalidCharactersFromFilename(String fileName) {
        String result = null;
        // Replace caracters that we would't in a filename by _
        String validFileName = fileName;
        validFileName = validFileName.replace('\u0020', '_');
        validFileName = validFileName.replace('\u0010', '_');
        validFileName = validFileName.replace('\u0013', '_');
        validFileName = validFileName.replace('\u0016', '_');
        validFileName = validFileName.replace('\"', '_');
        validFileName = validFileName.replace('\'', '_');
        validFileName = validFileName.replace(':', '_');
        validFileName = validFileName.replace(';', '_');
        validFileName = validFileName.replace(',', '_');
        validFileName = validFileName.replace('/', '_');
        validFileName = validFileName.replace('?', '_');
        validFileName = validFileName.replace('\\', '_');
        validFileName = validFileName.replace('!', '_');
        validFileName = validFileName.replace(' ', '_');
        validFileName = validFileName.replace('*', '_');
        validFileName = validFileName.replace('<', '_');
        validFileName = validFileName.replace('>', '_');
        validFileName = validFileName.replace('¦', '_');
        validFileName = validFileName.replace('|', '_');
        validFileName = validFileName.replace('\n', '_');
        validFileName = validFileName.replace('\t', '_');
        validFileName = validFileName.replace('\r', '_');
        validFileName = validFileName.replace('\f', '_');
        validFileName = validFileName.replace('\b', '_');
        result = validFileName;

        return result;
    }
    
    /**
     * Read all lines of a file into a string.
     * 
     * @param file
     *            The file whose lines are being read
     * @return a String containing all the lines of the input file
     * @throws FileException
     *             if an exception occurs
     */
    public static String readLines(File file) throws FileException {
        BufferedReader input = null;
        try {
            StringBuffer fileContent = new StringBuffer();
            input = new BufferedReader(new FileReader(file));
            String line;
            while ((line = input.readLine()) != null) {
                fileContent.append(line);
                fileContent.append('\n');
            }
            return fileContent.toString();
        } catch (IOException ex) {
            if (s_logger.isWarnEnabled()) {
                s_logger.warn("Error reading the lines of file "
                    + file.getAbsolutePath());
            }

            throw new FileException("Error reading the lines of file ",
                new Object[] {file.getAbsolutePath()});
        } finally {
            closeReader(input);
        }
    }
    
    /**
     * This method checks whether two files are equal.
     * 
     * @param f1
     *            the first file to compare (must be a file, not a directory)
     * @param f2
     *            the second file to compare (must be a file, not a directory)
     * @return true if the two files are equal           
     * @throws IOException
     *             if an error occurred during file comparison
     */
    public static boolean checkIfFilesAreEqual(File f1, File f2) 
        throws IOException {
        boolean result = false;        
        if ((!f1.exists()) || (!f2.exists())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("One of the two files to compare does not exist."
                    + " File 1: " + f1.getAbsolutePath()
                    + " / File 2: " + f2.getAbsolutePath());
            }
            result = false;
        } else if ((!f1.isFile()) || (!f2.isFile())) {
            
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("One of the two files is not a file (maybe a "
                    + "directory)."
                    + " File 1: " + f1.getAbsolutePath()
                    + " / File 2: " + f2.getAbsolutePath());
            }
            result = false;
        } else if (f1.length() != f2.length()) {
            
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("The length of the two files is not equal."
                    + " File 1: " + f1.getAbsolutePath()
                    + " / File 2: " + f2.getAbsolutePath());
            }
            
            result = false;
        } else {
            long length = f1.length();
            FileInputStream fis1 = new FileInputStream(f1);
            FileInputStream fis2 = new FileInputStream(f2);
    
            int data1;
            int data2;
            try {
                for (int i = 0; i < length; i++) {
                    data1 = fis1.read();
                    data2 = fis2.read();
                    if (data1 != data2) {
                        if (s_logger.isWarnEnabled()) {
                            s_logger.warn(
                                "The content of the files is not equal."
                                + " File 1: " + f1.getAbsolutePath()
                                + " / File 2: " + f2.getAbsolutePath());
                        }
                        return false;
                    }
                }
                result = true;
            } finally {
                closeInputStream(fis1);
                closeInputStream(fis2);
            }
        }
        return result;
    }
    
    /**
     * This interface can be used to be notified while a copy-process is
     * performed. It may become useful when copying huge files which may take
     * long time, resulting in a LivelinessMonitor-Kill. An instance of this
     * interface may be passed to the <code>copyFile()</code> and
     * <code>copyDir()</code> methods. The <code>receiveHeartbeat()</code>
     * method is periodically invoked while the <code>copyDir()</code> and
     * <code>copyFile()</code> methods perform. An implementation of this
     * interface may call its heartbeat-method to keep the thread alive.
     */
    public interface CopyHeartbeatListener {
        
        /**
         * This interface is used by the <code>copyFile()</code> and
         * <code>copyDir()</code> methods to perform heartbeats while copying
         * very large files.
         */
        void receiveHeartbeat();
    }
    
}

package ch.elca.el4j.plugins.version;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Stack;

/**
 * 
 * This class is a writer that allows some kind of transactions.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Philippe Jacot (PJA)
 */
class WriteBuffer {
    /**
     * The newline.
     */
    private static final String NEWLINE = System.getProperty("line.separator");
               
    /**
     * Where on commit is written to.
     */
    private OutputStreamWriter m_out;
    
    /**
     * The output buffer.
     */
    private StringBuffer m_outputBuffer = new StringBuffer();
    
    /**
     * The buffer.
     */
    private WriteBufferElement m_buffer;
    
    /**
     * The buffer stack.
     */
    private Stack<WriteBufferElement> m_bufferStack 
        = new Stack<WriteBufferElement>();
    
    /**
     * Are we on a new line?
     */
    private boolean m_newLine = true;
        
    /**
     * Create a new buffer that writes to out.
     * @param out Stream to write to.
     */
    public WriteBuffer(OutputStream out) {
        m_out = new OutputStreamWriter(out);
    }
    
    
    /**
     * Commit these changes.
     */
    public void commit() {
        if (m_buffer == null) {
            throw new IllegalStateException
            ("Cannot commit if no transactions are running");
        }
        
        if (!m_newLine) {
            m_buffer.m_buffer.append(NEWLINE);
            m_newLine = true;
        }
        
        if (m_bufferStack.isEmpty()) {
            // This is a root element, as this is commited, we can write.
            m_outputBuffer.append(m_buffer.m_buffer);
            
        } else {
            // Add this to commit to his parent
            m_bufferStack.peek().absorb(m_buffer);
        }
 
        clearTransaction();
    }
    
    /**
     * Start a new "transaction".
     */
    public void start() {
        if (!m_newLine) {
            m_buffer.m_buffer.append(NEWLINE);
            m_newLine = true;
        }
        String oldIndent = "";
        if (m_buffer != null) {
            m_bufferStack.push(m_buffer);
            oldIndent = m_buffer.m_indent;
        }
        m_buffer = new WriteBufferElement(oldIndent);
    }
    
    /**
     * Write the buffer to the output stream.
     * @throws IOException 
     */
    public void flush() throws IOException {
        m_out.write(m_outputBuffer.toString());
        m_out.flush();   
    }
    
    /**
     * Clear the last transaction.
     */
    private void clearTransaction() {
        m_newLine = true;
        if (!m_bufferStack.isEmpty()) {
            m_buffer = m_bufferStack.pop();
        } else {
            m_buffer = null;
        }
    }
    
    /**
     * Undo everything.
     *
     */
    public void rollback() {
        if (m_buffer == null) {
            throw new IllegalStateException
            ("Cannot rollback if no transactions are running");
        }
        clearTransaction();
    } 
    

    
    /**
     * Write this text and append a new line.
     * @param text String to write
     */
    public void writeLine(String text) {
        write(text);
        write(NEWLINE);
        m_newLine = true;
    }
    
    /**
     * Write a given text.
     * @param text Text to write
     */
    public void write(String text) {
        if (m_newLine) {
            m_buffer.m_buffer.append(m_buffer.m_indent);
            m_newLine = false;
        }
        m_buffer.m_buffer.append(text);
        
    }
    
    /**
     * Increase the indent.
     *
     */
    public void indent() {
        m_buffer.m_indent += "    ";
    }
    
    /**
     * Decrease the indent.
     *
     */
    public void unindent() {
        // Checkstyle: MagicNumber off
        m_buffer.m_indent = (m_buffer.m_indent.length() < 4) 
            ? "" : m_buffer.m_indent.substring
            (0, m_buffer.m_indent.length() - 4);
        // Checkstyle: MagicNumber on
    }
    
    /**
     * A buffer element.
     */
    private class WriteBufferElement {      
        /**
         * The indent.
         */
        String m_indent = "";
        
        /**
         * The buffer.
         */
        StringBuffer m_buffer = new StringBuffer();
                
         /**
          * Create a new Buffer Element.
          * @param startIndent The indent at the start
          */
        public WriteBufferElement(String startIndent) {
            m_indent = startIndent;
        }
        
        /**
         * Accept all text from another element.
         * @param element Element to absorb
         */
        public void absorb(WriteBufferElement element) {
            m_buffer.append(element.m_buffer);
        }
    }
}
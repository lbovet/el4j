
package ch.elca.el4j.services.debug;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import org.springframework.context.ApplicationContext;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.InterpreterError;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.TargetError;


/**
 * The evaluator can evaluate script expressions.
 * 
 * For now it uses bsh. Later it should also be able to support other shells (via bean scripting or the jdk 1.6 mechanism).
 *
 * Features: <br> 
 *   bsh.show=true shows more eval output <br>
 *   Variables available in the shell script: <ul>
 *   	<li> {@code ac} the current application context
 *      <li> {@code $_} the last return result
 *    </ul>
 *   
 *   
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source: /cvsroot/leafproject/leaf/debug/src/dist/ch/elca/leaf/services/debug/AbstractEvaluator.java,v $",
 *   "$Revision: 1.6 $", "$Date: 2004/03/26 12:47:14 $", "$Author: csc $"
 * );</script>
 **/
public class ShellExecutorImpl implements ShellExecutor {

    private ByteArrayOutputStream out;
    
    private ByteArrayOutputStream err;

    private PrintStream m_outPStream;
    
    private PrintStream m_errPStream;

    private Interpreter interpreter;
    
    protected ApplicationContext m_ac;

    public ShellExecutorImpl() {
        startup();
    }

    public ShellExecutorImpl(ApplicationContext ac) {
    	this();
        m_ac = ac;
    }

    
    
    protected synchronized void startup () {

        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        
        interpreter = new Interpreter
            (new StringReader (""),
             m_outPStream = new PrintStream (out),
             m_errPStream = new PrintStream (err),
             false);
    }
    
   
    // inherited comment
    public synchronized ResultHolder eval (String stmt)  {
        Object retVal = null;
        
        try {
            retVal = interpreter.eval (stmt);
            if (retVal != Primitive.VOID) {
                interpreter.set ("$_", retVal);
            }
            if (m_ac != null){
            	interpreter.set ("ac", m_ac);
            }
            
            Object show = interpreter.get ("bsh.show");
            if ( (retVal != null) && show instanceof Boolean &&
                ((Boolean) show).booleanValue() == true){
                m_outPStream.println ("<"+retVal+">");
            }
        } catch (InterpreterError e) {
            m_errPStream.println ("Internal Error: " + e.getMessage());
            e.printStackTrace (m_errPStream);
        } catch (TargetError te) {
            m_errPStream.println ("// Uncaught Exception: " + te);
            if (te.inNativeCode()){
                te.printStackTrace (m_errPStream);
            }
        } catch (EvalError ee) {
            m_errPStream.println (ee.toString());
        } catch (Exception e) {
            m_errPStream.println ("Unknown error: " + e);
            e.printStackTrace (m_errPStream);
        }
        
        String outStr = out.toString();
        out.reset();

        String errStr = err.toString();
        err.reset();
        
        return new ResultHolder (retVal,outStr,errStr);
    }

    // inherited comment
    public NameSpace getNameSpace () {
        return interpreter.getNameSpace();
    }
    
    // inherited comment
    public void setNameSpace (NameSpace ns){
        interpreter.setNameSpace (ns);
    }


    public Interpreter getInterpreter() {
        return interpreter;
    }


    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

}
        
package ch.elca.el4j.services.debug;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;


/**
 * Class to evaluate a bsh string in a sub-shell of this JVM.
 * 
 * Sample uses:
 *   java -Del4j.bsh.launchstr="server(2000);"   anyClass    makes the JVM accessible from
 *                                                           remote (std bean shell)
 *    * bsh.show=true; // shows output
 *    * debug();
 *    * see directory bsh.commands in the bsh.jar file for more ideas
 *  
 *  You can add scriptets in the directory /resources/mandatory: they are directly available as command.
 *  
 * Scripts that could be added (ideas):
 *    * dump all cfg of spring app ctxt
 *    * regularly print mem and thread status (e.g. all 30'' to track issues)
 *    * status info about the JVM
 *    * save an object serialized to disk/ load it again
 *    * start a new spring bean
 *  
 *  <br> <br>
 *  How to demo this module: 
 *   (1) Add the following to your MAVEN_OPTS environment variable:
 *         <code> -Del4j.bsh.launchstr=server(2000);</code> <br>
 *   (2) Add a dependency to this module. <br>
 *   (3) Launch your application <br>
 *   (4) Open in a browser the following URL: http://localhost:2000/remote/jconsole.html <br>
 *   (5) type   threadInfo();    ENTER       <br>
 *    
 * Limitation: cut and paste (with external info) is currently not supported.
 * 
 * @author Philipp H. Oser (POS)
 *
 */
public class BshCommandLineLauncher {

    public static final String BSH_LAUNCH_STRING = "el4j.bsh.launchstr";
    public static final String BSH_SCRIPLET_CLASSPATH = "/bsh_scriptlets";
    
    private static Log s_logger 
         = LogFactory.getLog(BshCommandLineLauncher.class);
    
    protected ShellExecutorImpl shell;
    
    public BshCommandLineLauncher() {
        String toLaunch;
        ResultHolder result; 
        if (StringUtils.hasText(toLaunch = System.getProperty(BSH_LAUNCH_STRING))) {
            shell = new ShellExecutorImpl();
            addScriptletClasspath(BSH_SCRIPLET_CLASSPATH);
            result = shell.eval(toLaunch);
            s_logger.warn("result of launching launchstr: "+result);            
        } else {
            s_logger.warn("No bsh launch string defined.");
        }
    }
    
    public ShellExecutor getShellExecutor () {
        return shell;
    }
    
    public void addScriptletClasspath (String cp) {
        shell.getInterpreter().getNameSpace().importCommands(cp);
    }
    
}

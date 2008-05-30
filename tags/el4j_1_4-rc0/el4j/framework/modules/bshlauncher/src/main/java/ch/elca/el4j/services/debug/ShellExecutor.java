package ch.elca.el4j.services.debug;

public interface ShellExecutor {

    /**
     * Invoke a method on the shell
     * @param expr
     * @return
     */
    public ResultHolder eval(String expr);
    
}

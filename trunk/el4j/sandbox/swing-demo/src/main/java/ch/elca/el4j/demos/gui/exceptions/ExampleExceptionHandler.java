package ch.elca.el4j.demos.gui.exceptions;

import ch.elca.el4j.gui.swing.exceptions.Handler;

public class ExampleExceptionHandler implements Handler {
    public boolean recognize(Exception e) {
        return (e instanceof RuntimeException);
    }
    
    public void handle(Exception e) {
        System.err.println("Runtime Exception!");
        e.printStackTrace(System.err);
    }
}

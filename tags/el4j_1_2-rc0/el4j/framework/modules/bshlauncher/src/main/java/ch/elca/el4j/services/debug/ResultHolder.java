package ch.elca.el4j.services.debug;

import java.io.Serializable;

public class ResultHolder implements Serializable {

    Object returnValue; 
    String stdout; 
    String stderr;
    
    public ResultHolder (Object returnValue, String stdout, String stderr) {
        this.returnValue = returnValue;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }
    
    public String toString() {
        return "ResultHolder {"+returnValue+"/"+stdout+"/"+stderr+"}";
    }
    
}

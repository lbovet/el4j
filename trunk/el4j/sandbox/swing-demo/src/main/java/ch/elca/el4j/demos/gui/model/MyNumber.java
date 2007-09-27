package ch.elca.el4j.demos.gui.model;

public class MyNumber {
    private int value;
    
    public MyNumber(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
}

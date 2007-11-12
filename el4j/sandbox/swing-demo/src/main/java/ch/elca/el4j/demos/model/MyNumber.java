package ch.elca.el4j.demos.model;

import org.hibernate.validator.Min;

public class MyNumber {
    private int m_value;
    
    public MyNumber() {}
    
    public MyNumber(int value) {
        m_value = value;
    }
    
    @Min(0)
    public int getValue() {
        return m_value;
    }
    
    public void setValue(int value) {
        m_value = value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(m_value);
    }
}

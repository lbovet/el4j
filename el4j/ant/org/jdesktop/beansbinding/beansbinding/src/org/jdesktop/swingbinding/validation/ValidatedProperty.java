package org.jdesktop.swingbinding.validation;

public class ValidatedProperty implements Comparable<ValidatedProperty> {
    private Object value;
    private Object parent;
    private String property;
    private boolean valid;
    
    public ValidatedProperty(String value) {
        // this constructor is necessary for
        // table.getDefaultEditor(...).getTableCellEditorComponent(...)
    }
    
    public ValidatedProperty(Object parent, Object value, String property, boolean valid) {
        this.value = value;
        this.valid = valid;
        this.parent = parent;
        this.property = property;
    }
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
    
    @SuppressWarnings("unchecked")
    public int compareTo(ValidatedProperty other) {
        if (value instanceof Comparable) {
            Comparable comparable = (Comparable) value;
            return comparable.compareTo(other.getValue());
        } else {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}

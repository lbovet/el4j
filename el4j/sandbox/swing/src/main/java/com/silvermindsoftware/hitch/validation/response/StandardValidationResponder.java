package com.silvermindsoftware.hitch.validation.response;

import java.awt.Color;

import javax.swing.JComponent;

public class StandardValidationResponder implements ValidationResponder {
    public void setValid(JComponent component) {
        if (component != null) {
            component.setBackground(new Color(255,255,255));
        }
    }
    public void setInvalid(JComponent component, String message) {
        if (component != null) {
            component.setBackground(new Color(255,128,128));
        }
    }
}

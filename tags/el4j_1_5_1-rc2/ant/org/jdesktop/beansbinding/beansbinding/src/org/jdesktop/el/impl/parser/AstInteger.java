/*
 * Copyright (C) 2007 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 *
 *//* Generated By:JJTree: Do not edit this line. AstInteger.java */

package org.jdesktop.el.impl.parser;

import java.math.BigInteger;

import org.jdesktop.el.ELException;

import org.jdesktop.el.impl.lang.EvaluationContext;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author$
 */
public final class AstInteger extends SimpleNode {
    public AstInteger(int id) {
        super(id);
    }

    private Number number;

    protected Number getInteger() {
        if (this.number == null) {
            try {
                this.number = new Long(this.image);
            } catch (ArithmeticException e1) {
                this.number = new BigInteger(this.image);
            }
        }
        return number;
    }

    public Class getType(EvaluationContext ctx)
            throws ELException {
        return this.getInteger().getClass();
    }

    public Object getValue(EvaluationContext ctx)
            throws ELException {
        return this.getInteger();
    }
}
package com.silvermindsoftware.hitch.handlers.type;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * User: brandongoodin
 * Date: May 14, 2007
 * Time: 11:18:45 AM
 */
public class BigDecimalTypeHandler implements TypeHandler {

    public Object convert(Object value) {
        // if null just return
        if (value == null) return value;

        BigDecimal retVal = null;

        if (value instanceof BigDecimal) {
            retVal = (BigDecimal) value;
        } else if (value instanceof String) {
            if(((String)value).trim().equals("")) {
                retVal = null;
            } else {
                retVal = new BigDecimal((String) value);
            }
        } else if (value instanceof Double) {
            retVal = new BigDecimal((Double) value);
        } else  if ( value instanceof Integer ) {
            retVal = new BigDecimal((Integer) value);
        } else  if ( value instanceof Long ) {
            retVal = new BigDecimal((Long) value);
        } else if (value instanceof BigInteger) {
            retVal = new BigDecimal((BigInteger)value);
        } else {
            throw new IllegalArgumentException(
                    "Could not convert object of type " +
                            value.getClass().getName() +
                            " to BigDecimal.");
        }
        return retVal;
    }
}

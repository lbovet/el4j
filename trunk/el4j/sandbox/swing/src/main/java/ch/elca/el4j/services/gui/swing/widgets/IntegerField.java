package ch.elca.el4j.services.gui.swing.widgets;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Swing text field for inputting integer values only.
 *
 * @author HUN
 */

public class IntegerField extends JTextField {
    /**
     * Comment for <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    private Integer m_maxValue = null;
    private Integer m_minValue = null;
    /**
     * The document associated with this field.
     */
    private IntegerDoc m_integerDoc;

    /**
     * Construct a text field for input integer only. The length of the field is limited as well.
     * 
     * @param lengthOfDigits the limitation in length for the digit numbers.
     */
    public IntegerField(int lengthOfDigits) {
        super();
        m_integerDoc = new IntegerDoc(lengthOfDigits);
        setDocument(m_integerDoc);
        addFocusListener(new FocusTextSelection(this));
        setHorizontalAlignment(JTextField.RIGHT);
        setDisabledTextColor(Color.black);
    }

    /**
     * <p>
     * The constructor.
     * </p>
     * 
     * @param lengthOfDigits Max number of digits input.
     * @param negativeable Indicates if this field will accept negative values.
     */
    public IntegerField(int lengthOfDigits, boolean negativeable) {
        super();
        m_integerDoc = new IntegerDoc(lengthOfDigits, negativeable);
        setDocument(m_integerDoc);
        addFocusListener(new FocusTextSelection(this));
        setHorizontalAlignment(JTextField.RIGHT);
        setDisabledTextColor(Color.black);
    }

    /**
     * <p>
     * Sets the upper limit for integer field.
     * </p>
     * 
     * @param maxValue limit for max value. Null means no limitation.
     */
    public void setMaxValue(Integer maxValue) {
        m_maxValue = maxValue;
    }
    /**
     * <p>
     * Sets the lower limit for integer field.
     * </p>
     * 
     * @param minValue limit for min value. Null means no limitation.
     */
    public void setMinValue(Integer minValue) {
        m_minValue = minValue;
    }

    /**
     * <p>
     * Checks if an integer value is acceptable or not
     * </p>
     * 
     * @param value
     *            integer value to be checked.
     * @return true if the value is acceptable.
     */
    private boolean acceptInteger(Integer value) {
        return (value != null && (m_maxValue == null || value.intValue() <= m_maxValue.intValue()) 
                && (m_minValue == null || value.intValue() >= m_minValue.intValue()));
    }

    /**
     * The default constructor.
     */
    public IntegerField() {
        super();
        m_integerDoc = new IntegerDoc();
        setDocument(m_integerDoc);
        addFocusListener(new FocusTextSelection(this));
        setHorizontalAlignment(JTextField.RIGHT);
        setDisabledTextColor(Color.black);
    }

    /**
     * <p>
     * Sets the limitation for this text field. <strong> Pay attention that after we set limit for
     * the text field, the document object of the text field will be changed. </strong>
     * </p>
     * 
     * @param limit the limitation for the text field.
     */
    public void setLimit(int limit) {
        m_integerDoc = new IntegerDoc(limit);
        setDocument(m_integerDoc);
    }

    /**
     * Requires checking number of digits user can input into the text field.
     * 
     * @param numberDigits number of digits
     */
    public void checkNumberDigits(int numberDigits) {
        m_integerDoc.checkNumberDigits(numberDigits);
    } // end checkNumberDigits

    /**
     * Requires canceling checking number of digits.
     */
    public void unCheckNumberDigits() {
        m_integerDoc.unCheckNumberDigits();
    } // end unCheckNumberDigits

    /**
     * Sets the integer value.
     * 
     * @param intNumber number of int type
     */
    public void setText(int intNumber) {
        super.setText(String.valueOf(intNumber));
    } // end setText

    /**
     * Sets the integer value in shape of integer object.
     * 
     * @param intObject number of Integer type
     */
    public void setText(Integer intObject) {
        if (intObject != null) {
            super.setText(intObject.toString());
        } // end if
        else {
            super.setText("");
        } // end else
    } // end setText

    /**
     * Gets long number existing in the Integer field
     * 
     * @return number of long type
     */
    public long getLong() {
        String str = getText().trim();
        if (!str.equals("")) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
            }
        } // end if
        return 0;
    } // end getLong

    /**
     * Gets int number existing in the Integer field
     * 
     * @return number of int type
     */
    public int getInt() {
        String str = getText().trim();
        if (!str.equals("")) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException numberFormatException) {
                // Get the max value is case overflow.
                if (isLong(str)) {
                    return Integer.MAX_VALUE;
                } else {
                    numberFormatException.printStackTrace();
                } // end else

            } // end catch.
        } // end if
        return 0;
    } // end getInt

    /**
     * <p>
     * Checks if the given number in long format or not.
     * </p>
     * 
     * @param str the text of the number
     * @return true if the text can be parsed to long value.
     */
    private static boolean isLong(String str) {
        boolean result = false;

        try {
            Long.parseLong(str);
            result = true;
        } catch (NumberFormatException ex) {
        } // ignore then result = false;

        return result;
    }

    /**
     * Gets int number existing in the Integer field
     * 
     * @return number of Integer type
     */
    public Integer getIntObject() {
        String str = getText().trim();
        if (!str.equals("")) {
            try {
                return Integer.valueOf(str);
            } catch (NumberFormatException numberFormatException) {
                // Get the max value is case overflow.
                if (isLong(str)) {
                    return new Integer(Integer.MAX_VALUE);
                } else {
                    numberFormatException.printStackTrace();
                }
            } // end catch
        } // end if
        return null;
    } // end getIntObject

    private static class FocusTextSelection extends FocusAdapter {
        private JTextField m_textField;

        public FocusTextSelection(JTextField textField) {
            super();
            m_textField = textField;
        } // end constructor

        public void focusGained(FocusEvent e) {
            String selectedText = m_textField.getText();
            if (selectedText != null && selectedText.length() > 0) {
                m_textField.setSelectionStart(0);
                m_textField.setSelectionEnd(selectedText.length());
            }
        } // end focusGained
    }
}

/**
 * The integer document object for the text field.
 * 
 * <script type="text/javascript">printFileStatus ("$Source:
 * /cvsroot/spos-backend/dev/src/guifw/java/ch/elca/guifw/widget/ElcaIntegerField.java,v $",
 * "$Revision: 1.3 $", "$Date: 2007/01/31 09:05:44 $", "$Author: ctt $" );</script>
 * 
 * @author HUN
 * @version 1.0
 */
class IntegerDoc extends PlainDocument {

    /**
     * Comment for <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Indicates if user can input a negative integer.
     */
    private boolean m_negativeable = false;

    /**
     * Number of digit numbers.
     */
    private int m_numberDigits = 0;

    /**
     * Indicates if the input length checking must be performed.
     */
    private boolean m_isCheckNumberDigits = false;

    /**
     * Constructor
     * 
     * @param numberOfDigits maximum number of digits user can input into the text field.
     */
    public IntegerDoc(int numberDigits) {
        m_numberDigits = numberDigits;
        m_isCheckNumberDigits = true;
    }

    /**
     * <p>
     * The constructor.
     * </p>
     * 
     * @param numberDigits Maximum number of digits user can input into the text field.
     * @param negativeable Indicates if user can input a negative integer.
     */
    public IntegerDoc(int numberDigits, boolean negativeable) {
        m_numberDigits = numberDigits;
        m_isCheckNumberDigits = true;
        m_negativeable = negativeable;
    }

    /**
     * <p>
     * The constructor.
     * </p>
     * 
     */
    public IntegerDoc() {

    }

    /**
     * Requires checking number of digits user can input into the text field
     * 
     * @param numberDigits number of digits
     */
    public void checkNumberDigits(int numberDigits) {
        m_numberDigits = numberDigits;
        m_isCheckNumberDigits = true;
    }

    /**
     * Requires canceling checking number of digits
     */
    public void unCheckNumberDigits() {
        m_isCheckNumberDigits = false;
    }

    /**
     * Checks input before shows it on screen.
     * 
     * @param offs Starting offset.
     * @param str String to be checked.
     * @param a attribute of JComponent.
     * 
     * @exception BabLocationException
     */
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

        // Gets current content string.
        String currentString = getContent().getString(0, getLength());

        // Gets content string after insert new string.
        String futureString = null;
        if (offs == 0) {
            futureString = str + currentString;
        } else {
            futureString = currentString.substring(0, offs) + str + currentString.substring(offs);
        }

        // Checks for number format.
        char[] futureChars = futureString.toCharArray();
        if (!m_negativeable) {

            // If the number is none-negative, the checking includes:
            // - The input length must not be greater than the max length allowed.
            if (m_isCheckNumberDigits && futureString.length() > m_numberDigits) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            // - All character must be digit.
            for (int i = 0; i < futureChars.length; i++) {
                if (!Character.isDigit(futureChars[i])) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
        } else {

            // If the number if negativeable, the checking includes:
            int digitNumbers = 0;

            // - The number format.
            for (int i = 0; i < futureChars.length; i++) {

                // The first character must be digit or sign character.
                if (i == 0 && !Character.isDigit(futureChars[i]) && futureChars[i] != '-') {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                // The remain characters must be digit.
                if (i != 0 && !Character.isDigit(futureChars[i])) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }

                // Counts the number of digit character.
                if (Character.isDigit(futureChars[i])) {
                    digitNumbers++;
                }
            }

            // - Input digit numbers.
            if (m_isCheckNumberDigits && digitNumbers > m_numberDigits) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
        }

        // No error found.
        super.insertString(offs, str, a);
    }
}
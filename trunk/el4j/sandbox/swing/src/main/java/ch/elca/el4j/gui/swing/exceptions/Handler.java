package ch.elca.el4j.gui.swing.exceptions;

/**
 * The interface Exception handlers must implement. It has two methods that
 * contain the logic for each of its 2 concerns: (1) recognizing Exceptions
 * it can handle and (2) handling those Exceptions appropriately.
 */
public interface Handler {
    /**
     * Return <tt>true</tt> if this Handler can handle the given
     * Exception; <tt>false</tt> otherwise.
     */
    public boolean recognize(Exception e);

    /**
     * React to the given Exception in any way seen fit. This may include
     * notifying the user, writing to a log, or any other valid logic.
     */
    public void handle(Exception e);
}
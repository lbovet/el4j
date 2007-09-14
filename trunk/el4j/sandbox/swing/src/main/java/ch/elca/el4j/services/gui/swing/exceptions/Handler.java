package ch.elca.el4j.services.gui.swing.exceptions;

/**
 * The interface Exception handlers must implement. It has two methods to
 * containing the logic for each of its concerns: recognizing Exceptions
 * it can handle and handling those Exceptions appropriately.
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
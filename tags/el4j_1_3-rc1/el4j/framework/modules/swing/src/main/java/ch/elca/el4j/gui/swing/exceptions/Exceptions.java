package ch.elca.el4j.gui.swing.exceptions;

/* Adapted from a demo by:
/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains a repository of strategies for dealing with Exceptions
 * which occur anywhere in the application. Clients may register Exception
 * handlers to deal with these exception in any way they see fit. If no
 * registered Exception handler recognizes an exception that has been raised
 * it is printed to {@link System#err} but otherwise ignored.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author James Lemieux
 * @author Stefan Wismer (SWI)
 */
public final class Exceptions implements Thread.UncaughtExceptionHandler {

    /** The one instance of the Exceptions class that is allowed. */
    private static final Exceptions singleton = new Exceptions();

    /**
     * Returns a handle to the single instance of the Exceptions class which is
     * allowed to exist.
     */
    public static Exceptions getInstance() {
        return singleton;
    }

    /**
     * A list of handlers which handle Exceptions of a global nature.
     */
    private final List<Handler> handlers = new ArrayList<Handler>();

    /**
     * Add <code>h</code> to the collection of {@link Handler}s consulted when
     * an Exception is raised from within the application.
     */
    public void addHandler(Handler h) {
        handlers.add(h);
    }

    /**
     * Remove <code>h</code> from the collection of {@link Handler}s consulted when
     * an Exception is raised from within the application.
     */
    public void removeHandler(Handler h) {
        handlers.remove(h);
    }

    /**
     * Attempt to locate a {@link Handler} which
     * {@link Handler#recognize recognizes} the given Exception and give it a
     * chance to {@link Handler#handle handle} it. If no appropriate
     * {@link Handler} can be found, the Exception is printed to
     * {@link System#err}.
     */
    public void handle(Exception e) {
        for (Iterator<Handler> i = handlers.iterator(); i.hasNext();) {
            Handler handler = i.next();
            if (handler.recognize(e)) {
                handler.handle(e);
                return;
            }
        }

        System.err.println("Exception was not recognized by any Exception Handler: " + e);
        e.printStackTrace(System.err);
    }
    
    /** {@inheritDoc} */
    public void uncaughtException(Thread t, Throwable e) {
        handle(new Exception(e));
    }
}
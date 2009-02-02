/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.gui.swing.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * This Exception Handler Manager allows to register Exception Handlers. If an uncaught exception occurs,
 * the handlers get executed in the order of their priority until a handler signals that no further handler
 * should be called (handle() return <code>true</code>). In case that no handlers get executed, a stack
 * trace will be printed to std err.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public final class Exceptions implements Thread.UncaughtExceptionHandler {
	/**
	 * The singleton instance.
	 */
	private static final Exceptions INSTANCE = new Exceptions();
	
	/**
	 * The Handler comparator that uses the priority to sort.
	 */
	private static final Comparator<Handler> ORDER_BY_PRIORITY = new Comparator<Handler>() {
		public int compare(Handler handler1, Handler handler2) {
			return -Integer.valueOf(handler1.getPriority()).compareTo(handler2.getPriority());
		}
	};
	
	/**
	 * The list of exception handlers order by priority.
	 */
	PriorityQueue<Handler> m_handlers = new PriorityQueue<Handler>(5, ORDER_BY_PRIORITY);
	
	
	/**
	 * @return    the singleton instance
	 */
	public static Exceptions getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Add an exception handler.
	 * @param handler    the handler to add
	 */
	public void addHandler(Handler handler) {
		m_handlers.add(handler);
	}
	
	/**
	 * Remove an exception handler.
	 * @param handler    the handler to add
	 */
	public void removeHandler(Handler handler) {
		m_handlers.remove(handler);
	}
	
	/**
	 * Execute all Handlers that recognize the given Exception (in the order of their priority)
	 * until a handler signals to stop. In case that no handlers get executed, a stack
	 * trace will be printed to std err.
	 * @param e    the exception to handle
	 */
	public void handle(Exception e) {
		boolean handled = false;
		
		for (Handler handler : m_handlers) {
			if (handler.recognize(e)) {
				boolean stop = handler.handle(e);
				handled = true;
				if (stop) {
					break;
				}
			}
		}
		
		if (!handled) {
			// print stacktrace to syserr
			System.err.println("Exception was not recognized by any Exception Handler: " + e);
			e.printStackTrace(System.err);
			
			// open exception dialog
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			new ExceptionDialog(sw.toString()).setVisible(true);
		}
	}
	
	/**
	 * Handler for AWT exceptions.
	 * @param t    a Throwable
	 */
	public void handle(Throwable t) {
		if (t instanceof Exception) {
			Exceptions.getInstance().handle((Exception) t);
		} else if (t instanceof Error) {
			// unwrap Error
			handle(((Error) t).getCause());
		}
	}
	
	/** {@inheritDoc} */
	public void uncaughtException(Thread t, Throwable e) {
		handle(new Exception(e));
	}
}
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
package ch.elca.el4j.gui.swing.eventbus;

import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.SwingEventService;

/**
 * A {@link SwingEventService} that really throws occurring exceptions (in addition to logging them).
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
public class ExceptionThrowingEventService extends SwingEventService {
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleException(Object event, Throwable e, StackTraceElement[] callingStack,
		EventSubscriber eventSubscriber) {
		super.handleException(event, e, callingStack, eventSubscriber);
		throwRuntimeException(e);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void handleException(String action, Object event, String topic, Object eventObj, Throwable e,
		StackTraceElement[] callingStack, String sourceString) {
		super.handleException(action, event, topic, eventObj, e, callingStack, sourceString);
		throwRuntimeException(e);
	}
	
	/**
	 * Throw a runtime exception.
	 * @param e    the exception to throw
	 */
	private void throwRuntimeException(Throwable e) {
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		} else {
			throw new RuntimeException(e);
		}
	}
}

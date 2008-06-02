/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.observer;

/**
 * An InquisitiveValueObserver is a ValueObserver that inquires about the state
 * of other ObservableValue objects. Such an observer needs to make sure not to
 * access these objects in a potentially outdated state (that may be 
 * inconsistent with newer knowledge this observer may have). Therefore, 
 * InquisisitiveObservers receive a signal when the other observers have had a 
 * chance to update their state, i.e. once all observers have been notified.
 *
 * @param <T> {@inheritDoc}
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public interface InquisitiveValueObserver<T> extends ValueObserver<T> {
    /** invoked once all observers have received their change notifications. */
    void notified();
}

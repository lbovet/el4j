/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.gui.richclient.executors.action;


/**
 * The executor action is the interface to connect the gui parts with the
 * service layer. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface ExecutorAction {
    /**
     * Lifecycle method invoked right before the executor component is to become
     * visible.
     */
    public void onAboutToShow();

    /**
     * Will be invoked to finish or confirm the current action of executor.
     * 
     * @return Returns <code>true</code> if the finish/confirm action could be
     *         completed successfully and corresponding gui elements can be
     *         closed.
     * @throws Exception
     *             Will be thrown if any exception occured while finishing
     *             action.
     */
    public boolean onFinishOrConfirm() throws Exception;
    
    /**
     * Will be invoked if an exception occured while finishing or confirming
     * the current action.
     * 
     * @param e
     *            Is exception that occured.
     * @return Returns <code>true</code> if the finish/confirm action could be
     *         completed successfully and corresponding gui elements can be
     *         closed.
     */
    public boolean onFinishOrConfirmException(Exception e);
    
    /**
     * Handle an executor component cancellation request.
     */
    public void onCancel();
}

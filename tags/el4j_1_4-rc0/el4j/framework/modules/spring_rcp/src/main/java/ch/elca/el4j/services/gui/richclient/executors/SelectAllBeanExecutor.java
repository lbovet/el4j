/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.executors;

import org.springframework.util.StringUtils;


/**
 * Executor to select all visible beans.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class SelectAllBeanExecutor extends AbstractBeanExecutor {
    /**
     * {@inheritDoc}
     */
    public void execute() {
        getBeanPresenter().selectAllBeans();
    }
    
    /**
     * {@inheritDoc}
     */
    public String getCommandId() {
        String commandId = super.getCommandId();
        if (!StringUtils.hasText(commandId)) {
            commandId = "selectAllBeansCommand";
            setCommandId(commandId);
        }
        return commandId;
    }

    /**
     * {@inheritDoc}
     */
    public void updateState() {
        setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    public String getSchema() {
        return null;
    }
}

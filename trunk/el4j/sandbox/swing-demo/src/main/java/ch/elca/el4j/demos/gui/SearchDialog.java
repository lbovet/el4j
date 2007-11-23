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
package ch.elca.el4j.demos.gui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import ch.elca.el4j.demos.gui.events.SearchProgressEvent;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.dialog.search.AbstractSearchDialog;

/**
 * This class represents a simple search dialog.
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
public class SearchDialog extends AbstractSearchDialog {
    /**
     * The appFramework application.
     */
    private GUIApplication m_application;
    
    /**
     * The resource map.
     */
    private ResourceMap m_resourceMap;
    
    /**
     * The background search task.
     */
    @SuppressWarnings("unchecked")
    private Task m_currentSearch = null;
    
    /**
     * The constructor.
     */
    public SearchDialog() {
        super();
        
        m_application = GUIApplication.getInstance();
        m_resourceMap = m_application.getContext()
            .getResourceMap(SearchDialog.class);
        m_searchButton.setAction(m_application.getAction(this, "search"));
    }
    
    /** {@inheritDoc} */
    @Override
    protected void createOptionalComponents() {
        super.createOptionalComponents();
        
        // Checkstyle: MagicNumber off
        m_options = new JComponent[4];
        m_options[0] = new JCheckBox();
        m_options[0].setName("option0");
        m_options[1] = new JCheckBox();
        m_options[1].setName("option1");
        m_options[2] = new JLabel();
        m_options[2].setName("info0");
        m_options[3] = new JLabel();
        m_options[3].setName("info1");
        // Checkstyle: MagicNumber on
    }
    
    
    /**
     * Progress is interdeterminate for the first 150ms, then run for another
     * 1500, marking progress every 150ms.
     */
    private class BackgroundSearch extends Task<Void, Void> {
        /**
         * The constructor.
         */
        BackgroundSearch() {
            super(m_application);
            m_searchButton.setText(getRes("cancel"));
        }

        /** {@inheritDoc} */
        @Override
        protected Void doInBackground() throws InterruptedException {
            // Checkstyle: MagicNumber off
            for (int i = 0; i < 10; i++) {
                sendEvent(String.format(getRes("progress"), i));
                Thread.sleep(150L);
                setProgress(i, 0, 9);
            }
            Thread.sleep(150L);
            // Checkstyle: MagicNumber on
            return null;
        }

        /** {@inheritDoc} */
        @Override
        protected void succeeded(Void ignored) {
            sendEvent(getRes("done"));
            m_searchField.setText(m_searchField.getText() + " found!");
        }

        /** {@inheritDoc} */
        @Override
        protected void cancelled() {
            sendEvent(getRes("canceled"));
        }
        
        /** {@inheritDoc} */
        @Override
        protected void finished() {
            super.finished();
            
            m_searchButton.setText(getRes("search"));
            m_currentSearch = null;
        }
        
        /**
         * Sends a progess event via eventbus.
         * 
         * @param message    the message to send
         */
        private void sendEvent(String message) {
            EventBus.publish(new SearchProgressEvent(message));
        }

    }
    
    /**
     * Perform the search.
     * 
     * @return    the task to run
     */
    @SuppressWarnings("unchecked")
    @Action
    public Task search() {
        if (m_currentSearch == null) {
            m_currentSearch = new BackgroundSearch();
            return m_currentSearch;
        } else {
            m_currentSearch.cancel(true);
            return null;
        }
    }
    
    /**
     * @param id    the resource ID
     * @return      the String associated with the given resource ID
     */
    protected String getRes(String id) {
        return m_resourceMap.getString(id);
    }
}

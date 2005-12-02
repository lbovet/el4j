/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.apps.refdb.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swing.utils.UIManagerUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.enums.LetterCodedLabeledEnum;
import org.springframework.core.enums.ShortCodedLabeledEnum;
import org.springframework.core.enums.StringCodedLabeledEnum;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.table.TableUtils;
import org.springframework.richclient.table.renderer.BeanTableCellRenderer;
import org.springframework.richclient.table.renderer.BooleanTableCellRenderer;
import org.springframework.richclient.table.renderer.DateTimeTableCellRenderer;
import org.springframework.richclient.table.renderer.LabeledEnumTableCellRenderer;
import org.springframework.richclient.table.renderer.OptimizedTableCellRenderer;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.gui.forms.KeywordPropertiesForm;
import ch.elca.el4j.apps.refdb.gui.table.KeywordTableModel;

/**
 * Keyword service view.
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
public class KeywordView extends AbstractReferenceServiceView {
    /**
     * Flag to indicate if the control has been created.
     */
    private boolean m_controlCreated = false;
    
    /**
     * Keyword table model.
     */
    private KeywordTableModel m_keywordTableModel = null;
    
    /**
     * Keyword table.
     */
    private JTable m_keywordTable = null;
    
    /**
     * Executor for the keyword properties.
     */
    private KeywordPropertiesExecutor m_keywordPropertiesExecutor
        = new KeywordPropertiesExecutor();
    
    /**
     * {@inheritDoc}
     * 
     * Returns the root component for this view.
     */
    protected JComponent createControl() {
        JPanel p = new JPanel(new BorderLayout());
        
        MessageSource messageSource 
            = (MessageSource) getApplicationContext().getBean("messageSource");

        m_keywordTableModel = new KeywordTableModel(messageSource);
        m_keywordTableModel.setRowNumbers(false);
        m_keywordTableModel.setRows(getReferenceService().getAllKeywords());

        
        
        
        m_keywordTable = new JTable(m_keywordTableModel);
        
        TableUtils.attachSorter(m_keywordTable);
        //TableUtils.installDefaultRenderers(m_keywordTable);
        OptimizedTableCellRenderer defaultRenderer = new OptimizedTableCellRenderer();
        BeanTableCellRenderer beanRenderer = new BeanTableCellRenderer();
        LabeledEnumTableCellRenderer er = new LabeledEnumTableCellRenderer();
        m_keywordTable.setDefaultRenderer(Object.class, beanRenderer);
        //m_keywordTable.setDefaultRenderer(String.class, defaultRenderer);
        m_keywordTable.setDefaultRenderer(ShortCodedLabeledEnum.class, er);
        m_keywordTable.setDefaultRenderer(StringCodedLabeledEnum.class, er);
        m_keywordTable.setDefaultRenderer(LetterCodedLabeledEnum.class, er);
        m_keywordTable.setDefaultRenderer(Date.class, new DateTimeTableCellRenderer());
        m_keywordTable.setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer());

        TableUtils.sizeColumnsToFitRowData(m_keywordTable);

        
        m_keywordTable.setRowSelectionAllowed(true);
        m_keywordTable.setColumnSelectionAllowed(false);
        m_keywordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Register listener for table row selection.
        ListSelectionModel rowSelectionModel 
            = m_keywordTable.getSelectionModel();
        rowSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                } else {
                    updateCommands();
                }
            }
        });
        
        // Register listener for double-click on a table row.
        m_keywordTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 
                    && e.getButton() == MouseEvent.BUTTON1 
                    && m_keywordPropertiesExecutor.isEnabled()) {
                    m_keywordPropertiesExecutor.execute();
                }
            }
        });
        
        
        p.add(new JScrollPane(m_keywordTable), BorderLayout.CENTER);
        
        m_controlCreated = true;
        return p;
    }
    
    /**
     * {@inheritDoc}
     * 
     * Registers the properties executor.
     */
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register(GlobalCommandIds.PROPERTIES, 
            m_keywordPropertiesExecutor);
    }
    
    /**
     * @return Returns the selected keyword dto or <code>null</code>.
     */
    protected KeywordDto getSelectedKeyword() {
        KeywordDto result = null;
        if (m_controlCreated) {
            int rowIndex = m_keywordTable.getSelectedRow();
            if (rowIndex >= 0) {
                result = (KeywordDto) m_keywordTableModel.getRow(rowIndex);
            }
        }
        return result;
    }
    
    /**
     * Will be invoked if commands should be update.
     */
    protected void updateCommands() {
        // Enables the keyword properties executer if a keyword is selected.
        m_keywordPropertiesExecutor.setEnabled(getSelectedKeyword() != null);
    }

    
    /**
     * Properties executor for keywords.
     *
     * @author Martin Zeltner (MZE)
     */
    public class KeywordPropertiesExecutor 
        extends AbstractActionCommandExecutor {
        
        /**
         * {@inheritDoc}
         */
        public void execute() {
            // Get keyword dto to edit.
            final KeywordDto KEYWORD = getSelectedKeyword();
            
            // Return if no keyword is selected.
            if (KEYWORD == null) {
                return;
            }
            
            // Create a form that wraps the given keyword dto.
            final KeywordPropertiesForm KEYWORD_FORM 
                = new KeywordPropertiesForm(
                    FormModelHelper.createFormModel(KEYWORD));
            
            // Back a dialog page out of the form.
            final FormBackedDialogPage DIALOG_PAGE 
                = new FormBackedDialogPage(KEYWORD_FORM);

            // Create a window dialog for the dialog page.
            TitledPageApplicationDialog dialog 
                = new TitledPageApplicationDialog(
                    DIALOG_PAGE, getWindowControl()) {
                
                /**
                 * {@inheritDoc}
                 */
                protected void onAboutToShow() {
                    KEYWORD_FORM.focusFirstComponent();
                    setEnabled(DIALOG_PAGE.isPageComplete());
                }

                /**
                 * {@inheritDoc}
                 */
                protected boolean onFinish() {
                    KEYWORD_FORM.commit();
                    getReferenceService().saveKeyword(KEYWORD);
                    m_keywordTableModel.fireTableDataChanged();
                    return true;
                }
            };
            dialog.showDialog();
        }
    }
}

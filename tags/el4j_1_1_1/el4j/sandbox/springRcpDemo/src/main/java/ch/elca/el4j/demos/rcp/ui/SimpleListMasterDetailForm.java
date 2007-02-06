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
package ch.elca.el4j.demos.rcp.ui;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.form.AbstractDetailForm;
import org.springframework.richclient.form.AbstractMasterForm;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
import org.springframework.richclient.util.GuiStandardUtils;

/**
 * A master/detail form that presents the master data as a simple list 
 * (using the toString method of each object or a specified cell renderer 
 * for rendering) and the edit form is rendered beside the master list. 
 * The control buttons are layed out under the master and detail forms.
 * <p>
 * Implementing classes must override
 * {@link AbstractMasterForm#createDetailForm(NestingFormModel, ValueModel)}
 * 
 * @author lstreepy
 * @see AbstractMasterForm#createDetailForm(NestingFormModel, ValueModel)
 * 
 */
public abstract class SimpleListMasterDetailForm extends AbstractMasterForm {

    /**
     * Construct this master/detail form using the data provided. List elements
     * will be rendered using the toString method.
     * 
     * @param parentFormModel
     *            Parent form model
     * @param property
     *            containing this forms data (must be a collection or an array)
     * @param formId .
     * @param detailType .
     */
    public SimpleListMasterDetailForm(HierarchicalFormModel parentFormModel,
        String property, String formId, Class detailType) {
        super(parentFormModel, property, formId, detailType);
    }

    /**
     * Construct this master/detail form using the data provided. List elements
     * will be rendered using the <code>renderProperty</code> specified.
     * 
     * @param parentFormModel
     *            Parent form model
     * @param property
     *            containing this forms data (must be a collection or an array)
     * @param detailType .
     * @param renderProperty
     *            Property to use for rendering list elements (instead of
     *            calling toString)
     */
    public SimpleListMasterDetailForm(HierarchicalFormModel parentFormModel,
        String property, String formId, Class detailType, 
        String renderProperty) {
        super(parentFormModel, property, formId, detailType);
        setRenderProperty(renderProperty);
    }

    /**
     * Set the property to use to render list items. This will construct an internal cell
     * renderer.
     * 
     * @param renderProperty Name of property to use to render list elements.
     */
    public void setRenderProperty(String renderProperty) {
        _renderProperty = renderProperty;
        setCellRenderer(new BeanPropertyValueListRenderer(renderProperty));
    }

    /**
     * Get the render property name.
     * 
     * @return render property name
     */
    public String getRenderProperty() {
        return _renderProperty;
    }

    /**
     * Set the cell renderer to use for items in the list.
     * 
     * @param renderer
     */
    public void setCellRenderer(ListCellRenderer renderer) {
        _renderer = renderer;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.richclient.form.AbstractMasterForm#getSelectionModel()
     */
    @Override
    protected ListSelectionModel getSelectionModel() {
        return _masterList.getSelectionModel();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.richclient.form.AbstractForm#createFormControl()
     */
    @Override
    protected JComponent createFormControl() {
        configure();
        final BindingFactory bf = getBindingFactory();
        TableFormBuilder formBuilder = new TableFormBuilder(bf);

        _masterList = getComponentFactory().createList(); 
        _masterList.setModel(getMasterEventList());
        _masterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // TODO fix me ;)
        _masterList.setFixedCellWidth(120);

        // Configure the list to be the size desired
        _masterList.setVisibleRowCount(getVisibleRowCount());

        // Setup our selection listener so that it controls the detail form
        installSelectionHandler();

        // Install the renderer, if configured
        if (_renderer != null) {
            _masterList.setCellRenderer(_renderer);
        }
        JScrollPane sp = new JScrollPane(_masterList);

        formBuilder.getLayoutBuilder().cell(sp, "colSpec=min(250dlu;p)");
        formBuilder.getLayoutBuilder().unrelatedGapCol();
        formBuilder.getLayoutBuilder().cell(getDetailForm().getControl());
        formBuilder.row();

        // Layout the two button bars with 0 distance between them so toggling the
        // visibility of either one will not visibly show a difference in Y offset.

        _buttonBar = createButtonBar();
        formBuilder.getLayoutBuilder().cell(_buttonBar, "colSpan=2");
        formBuilder.getLayoutBuilder().row("0dlu");
        _createButtonBar = createCreateButtonBar();
        formBuilder.getLayoutBuilder().cell(_createButtonBar, "colSpan=2");

        updateControlsForState();

        
        return formBuilder.getForm();
    }

    /**
     * Get the number of visible rows to display on the list.
     * 
     * @return number of rows to show
     */
    public int getVisibleRowCount() {
        return _masterList.getVisibleRowCount();
    }

    /**
     * Update our controls based on our state. We need to switch between two different
     * button bars depending on our state. We simply control the visibility of two button
     * bars that have already been placed on the form.
     */
    protected void updateControlsForState() {
        super.updateControlsForState();

        if (getDetailForm().getEditState() == AbstractDetailForm.STATE_CREATE) {
            _createButtonBar.setVisible(true);
            _buttonBar.setVisible(false);
        } else {
            _createButtonBar.setVisible(false);
            _buttonBar.setVisible(true);
        }
    }

    /**
     * Return the group of command buttons to control this master/detail form. In this
     * case all the buttons are together since we present a "simplified" view in which the
     * master and detail forms are right next to each other with the control buttons below
     * the pair of forms.
     */
    protected JComponent createButtonBar() {
        CommandGroup formCommandGroup = CommandGroup.createCommandGroup(null,
            new AbstractCommand[] {getNewFormObjectCommand(),
                getDeleteCommand(), getDetailForm().getRevertCommand(),
                getDetailForm().getCommitCommand()});
        JComponent buttonBar = formCommandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder(buttonBar);
        return buttonBar;
    }

    /**
     * Return the group of command buttons to control this master/detail form when we are
     * in the "CREATE" edit mode. This will display the "Cancel" button instead of the
     * Revert button.
     */
    protected JComponent createCreateButtonBar() {
        CommandGroup formCommandGroup = CommandGroup.createCommandGroup(null,
            new AbstractCommand[] {getNewFormObjectCommand(),
                getDeleteCommand(), getDetailForm().getCancelCommand(),
                getDetailForm().getCommitCommand()});
        JComponent buttonBar = formCommandGroup.createButtonBar();
        GuiStandardUtils.attachDialogBorder(buttonBar);
        return buttonBar;
    }

    private JList _masterList;
    private String _renderProperty;
    private ListCellRenderer _renderer;
    private JComponent _buttonBar;
    private JComponent _createButtonBar;
}

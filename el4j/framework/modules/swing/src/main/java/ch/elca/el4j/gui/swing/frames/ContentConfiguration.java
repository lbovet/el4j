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
package ch.elca.el4j.gui.swing.frames;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindowTab;

/**
 * A descriptor of a {@link Content} or {@link ToolWindowTab}.
 * This is used to specify a docking frame before it gets created.
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
public class ContentConfiguration {
	/**
	 * @see Content#getId()
	 */
	protected String m_id;
	
	/**
	 * @see Content#getTitle()
	 */
	protected String m_title;
	
	/**
	 * @see Content#getIcon()
	 */
	protected Icon m_icon;
	
	/**
	 * @see Content#getComponent()
	 */
	protected JComponent m_component;
	
	/**
	 * @see Content#getToolTipText()
	 */
	protected String m_toolTip;
	
	/**
	 * @see Content
	 */
	protected Object[] m_constraints;
	
	/**
	 * @see ContentManager#addContent(String, String, Icon, java.awt.Component)
	 */
	public ContentConfiguration(String id, String title, Icon icon, JComponent component) {
		this(id, title, icon, component, null);
	}
	
	/**
	 * @see ContentManager#addContent(String, String, Icon, java.awt.Component, String, Object...)
	 */
	public ContentConfiguration(String id, String title, Icon icon, JComponent component, String tip,
		Object... constraints) {
		
		m_id = id;
		m_title = title;
		m_icon = icon;
		m_component = component;
		m_toolTip = tip;
		m_constraints = constraints;
		
	}
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return m_id;
	}
	/**
	 * @param id Is the id to set.
	 */
	public void setId(String id) {
		m_id = id;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return m_title;
	}
	/**
	 * @param title Is the title to set.
	 */
	public void setTitle(String title) {
		m_title = title;
	}
	/**
	 * @return Returns the icon.
	 */
	public Icon getIcon() {
		return m_icon;
	}
	/**
	 * @param icon Is the icon to set.
	 */
	public void setIcon(Icon icon) {
		m_icon = icon;
	}
	/**
	 * @return Returns the component.
	 */
	public JComponent getComponent() {
		return m_component;
	}
	/**
	 * @param component Is the component to set.
	 */
	public void setComponent(JComponent component) {
		m_component = component;
	}
	/**
	 * @return Returns the toolTip.
	 */
	public String getToolTip() {
		return m_toolTip;
	}
	/**
	 * @param toolTip Is the toolTip to set.
	 */
	public void setToolTip(String toolTip) {
		m_toolTip = toolTip;
	}
	/**
	 * @return Returns the constraints.
	 */
	public Object[] getConstraints() {
		return m_constraints;
	}
	/**
	 * @param constraints Is the constraints to set.
	 */
	public void setConstraints(Object[] constraints) {
		m_constraints = constraints;
	}
}

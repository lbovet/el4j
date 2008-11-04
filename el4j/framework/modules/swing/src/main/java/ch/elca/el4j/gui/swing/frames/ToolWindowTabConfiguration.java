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

import org.noos.xing.mydoggy.ContentManager;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;

public class ToolWindowTabConfiguration extends ContentConfiguration {
	protected ToolWindowAnchor m_anchor;
	
	/**
	 * @see ToolWindowManager#registerToolWindow(String, String, Icon, java.awt.Component, ToolWindowAnchor)
	 */
	public ToolWindowTabConfiguration(String id, String title, Icon icon, JComponent component, ToolWindowAnchor anchor) {
		super(id, title, icon, component, null);
		m_anchor = anchor;
	}

	/**
	 * @return Returns the anchor.
	 */
	public ToolWindowAnchor getAnchor() {
		return m_anchor;
	}

	/**
	 * @param anchor Is the anchor to set.
	 */
	public void setAnchor(ToolWindowAnchor anchor) {
		m_anchor = anchor;
	}
}

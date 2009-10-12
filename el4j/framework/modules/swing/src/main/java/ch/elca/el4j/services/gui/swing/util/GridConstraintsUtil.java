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
package ch.elca.el4j.services.gui.swing.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.apache.log4j.Logger;

/**
 * Utility to make {@link GridBagConstraints} code nicer.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class GridConstraintsUtil {

	/** A logger for debugging purposes. */
	private static final Logger s_log
		= Logger.getLogger(GridConstraintsUtil.class);
	
	/** The contained GridBagConstraints element. */
	private GridBagConstraints m_g;
	
	/** 
	 * The contained persistent element. This is used to reset "once" changes.
	 */
	private GridBagConstraints m_persistent;
	
	/**
	 * Constructor. Fields to zero.
	 */
	public GridConstraintsUtil() {
		m_g = new GridBagConstraints();
		m_persistent = new GridBagConstraints();
		m_g.gridx = 0;
		m_g.gridy = 0;
	}

	/**
	 * Constructor that allows an initial row to be specified.
	 * In particular, row -1 is allowed so that a loop can begin with newLine().
	 * @param initialRow The initial row.
	 */
	public GridConstraintsUtil(int initialRow) {
		this();
		m_g.gridy = initialRow;
	}
	
	/**
	 * Clone the constraints, reset the internal one then return the clone.
	 * @return A GridBagConstraints with the current values.
	 */
	private GridBagConstraints returnConstraints() {
		GridBagConstraints current = m_g;
		m_g = (GridBagConstraints) m_persistent.clone();
		m_g.gridx = current.gridx;
		m_g.gridy = current.gridy;
		
		if (s_log.isDebugEnabled()) {
			s_log.debug("x " + current.gridx + " y " + current.gridy + " w "
				+ current.gridwidth + " wx " + current.weightx + " wy "
				+ current.weighty + " f " + current.fill + " i t"
				+ current.insets.top + ",l" + current.insets.left + ",b"
				+ current.insets.bottom + ",r" + current.insets.right);
		}
		
		return current;
	}

	/**
	 * Set a new anchor for the next object. This is applied once then reset.
	 * @param anchor The anchor to set.
	 * @return <code>this</code> with the modification made.
	 */
	public GridConstraintsUtil anchor(int anchor) {
		m_g.anchor = anchor;
		return this;
	}

	
	/**
	 * Set a new width for the next object. This is applied once then reset.
	 * @param width The width to set.
	 * @return <code>this</code> with the modification made.
	 */
	public GridConstraintsUtil width(int width) {
		m_g.gridwidth = width;
		return this;
	}

	/**
	 * Set a new height for the next object. This is applied once then reset.
	 * @param height The height to set.
	 * @return <code>this</code> with the modification made.
	 */
	public GridConstraintsUtil height(int height) {
		m_g.gridheight = height;
		return this;
	}
	
	/**
	 * Set the column weight for the next object.
	 * @param weight The weight to set.
	 * @return <code>this</code> with the modification.
	 */
	public GridConstraintsUtil weight(double weight) {
		m_g.weightx = weight;
		return this;
	}
	
	/**
	 * Set the row weight for the next object.
	 * @param weightY The weight to set.
	 * @return <code>this</code> with the modification.
	 */
	public GridConstraintsUtil weightY(double weightY) {
		m_g.weighty = weightY;
		return this;
	}
	
	/**
	 * @return A GridBagConstraints pointing to the current row/column.
	 */
	public GridBagConstraints current() {
		return returnConstraints();
	}
	
	/**
	 * @return A GridBagConstraints for the next cell.
	 */
	public GridBagConstraints next() {
		m_g.gridx++;
		return returnConstraints();
	}
	
	/**
	 * @return A GridBagConstraints for the first cell on a new line.
	 */
	public GridBagConstraints newLine() {
		m_g.gridx = 0;
		m_g.gridy++;
		return returnConstraints();
	}
	
	/**
	 * Persistently set insets.
	 * @param i The insets.
	 */
	public void setPersistentInsets(Insets i) {
		m_persistent.insets = i;
		m_g.insets = i;
	}
	
	/**
	 * Persistently set the fill parameter.
	 * @param fill The fill parameter to set.
	 */
	public void setPeristentFill(int fill) {
		m_persistent.fill = fill;
		m_g.fill = fill;
	}
	
	/**
	 * Set insets for the next element.
	 * @param t Top.
	 * @param l Left.
	 * @param b Bottom.
	 * @param r Right.
	 * @return <code>this</code> with the modification.
	 */
	public GridConstraintsUtil insets(int t, int l, int b, int r) {
		m_g.insets = new Insets(t, l, b, r);
		return this;
	}
}

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
package ch.elca.el4j.services.gui.swing.cookswing;

import cookxml.common.CommonLib;
import cookxml.cookswing.CookSwing;
import cookxml.cookswing.CookSwingLib;
import cookxml.cookxml.CookXmlLib;
import cookxml.core.adder.DefaultAdder;
import cookxml.core.setter.DefaultSetter;
import cookxml.core.setter.DoNothingSetter;
import cookxml.core.taglibrary.InheritableTagLibrary;

import ch.elca.el4j.services.gui.swing.cookswing.action.ButtonActionSetter;
import ch.elca.el4j.services.gui.swing.cookswing.binding.BindingCreator;
import ch.elca.el4j.services.gui.swing.cookswing.binding.ColumnBindingCreator;
import ch.elca.el4j.services.gui.swing.cookswing.binding.ComboBoxBindingCreator;
import ch.elca.el4j.services.gui.swing.cookswing.binding.ListBindingCreator;
import ch.elca.el4j.services.gui.swing.cookswing.binding.NoAddVarSetter;
import ch.elca.el4j.services.gui.swing.cookswing.binding.TableBindingCreator;
import ch.elca.el4j.services.gui.swing.cookswing.designgridlayout.DesignGridLayoutCreator;
import ch.elca.el4j.services.gui.swing.cookswing.designgridlayout.EmptyRowCreator;
import ch.elca.el4j.services.gui.swing.cookswing.designgridlayout.RowAdder;
import ch.elca.el4j.services.gui.swing.cookswing.designgridlayout.RowCreator;

/**
 * This factory creates the enhanced tag library.
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
public final class TagLibraryFactory {
	/**
	 * Hidden default constructor.
	 */
	private TagLibraryFactory() { }
	
	/**
	 * @return    the enhanced tag library
	 */
	public static InheritableTagLibrary getTagLibrary() {
		final String swingNamespace = CookSwingLib.NAMESPACE;
		
		// enable setting of objects that should not be added to parent
		CookXmlLib.getSingletonTagLibrary().setSetter(
			null, "var", new NoAddVarSetter());
		
		// ignore colspan attribute (in common)
		CommonLib.getSingletonTagLibrary().setSetter(
			null, "colspan", DoNothingSetter.getInstance());
		
		// install default action attribute setter for buttons
		CookSwing.getSwingTagLibrary().setSetter(
			"abstractbutton", "action", new ButtonActionSetter());
		
		// ignore colspan attribute (in swing)
		CookSwing.getSwingTagLibrary().setSetter(
			null, "colspan", DoNothingSetter.getInstance());
		
		
		// install XML schemaLocation handler
		InheritableTagLibrary tagLibrary = new InheritableTagLibrary(
			CookSwing.getSwingTagLibrary());
		tagLibrary.setNameSpace("http://www.w3.org/2001/XMLSchema-instance");
		tagLibrary.setSetter(
			null, "schemaLocation", DoNothingSetter.getInstance());
		
		// install el4j taglibrary
		tagLibrary = new InheritableTagLibrary(tagLibrary);
		tagLibrary.setNameSpace("http://www.elca.ch/el4j/cookSwing");
		
		// default setters and adders
		tagLibrary.setSetter(null, null, DefaultSetter.getInstance());
		tagLibrary.addAdder(null, DefaultAdder.getInstance());
		
		// <create-component>
		tagLibrary.setCreator(
			"create-component", new CreateComponentCreator());
		tagLibrary.setAdder(
			"create-component", new CreateComponentAdder());
		tagLibrary.setSetter(
			"create-component", null, DoNothingSetter.getInstance());
		
		// <flattoolbar>
		tagLibrary.setCreator(
			"flattoolbar", new FlatToolBarCreator());
		tagLibrary.inheritTag(swingNamespace, "toolbar", "flattoolbar");
		tagLibrary.setAdder("flattoolbar", DefaultAdder.getInstance());
		
		// <windowmenu>
		tagLibrary.setCreator(
			"windowmenu", new WindowMenuCreator());
		tagLibrary.inheritTag(swingNamespace, "menu", "windowmenu");
		tagLibrary.setAdder("windowmenu", DefaultAdder.getInstance());
		tagLibrary.setSetter(
			"windowmenu", "desktopPaneId", DoNothingSetter.getInstance());
		
		// <binding>
		tagLibrary.setCreator(
			"binding", new BindingCreator());
		tagLibrary.setSetter(
			"binding", null, DoNothingSetter.getInstance());
		
		// <listbinding>
		tagLibrary.setCreator(
			"listbinding", new ListBindingCreator());
		tagLibrary.setSetter(
			"listbinding", null, DoNothingSetter.getInstance());
		
		// <tablebinding>
		tagLibrary.setCreator(
			"tablebinding", new TableBindingCreator());
		tagLibrary.setSetter(
			"tablebinding", null, DoNothingSetter.getInstance());
		
		// <column>
		tagLibrary.setCreator(
			"column", new ColumnBindingCreator());
		tagLibrary.setSetter(
			"column", null, DoNothingSetter.getInstance());
		
		// <comboboxbinding>
		tagLibrary.setCreator(
			"comboboxbinding", new ComboBoxBindingCreator());
		tagLibrary.setSetter(
			"comboboxbinding", null, DoNothingSetter.getInstance());
		
		// <designgridlayout>
		tagLibrary.setCreator(
			"designgridlayout", new DesignGridLayoutCreator());
		tagLibrary.setSetter(
			"designgridlayout", null, DoNothingSetter.getInstance());
		
		// <row>
		tagLibrary.setCreator("row", new RowCreator());
		tagLibrary.setAdder("row", new RowAdder());
		tagLibrary.setSetter("row", null, DoNothingSetter.getInstance());
		
		// <emptyrow>
		tagLibrary.setCreator("emptyrow", new EmptyRowCreator());
		tagLibrary.setSetter("emptyrow", null, DoNothingSetter.getInstance());
		
		
		return tagLibrary;
	}
}

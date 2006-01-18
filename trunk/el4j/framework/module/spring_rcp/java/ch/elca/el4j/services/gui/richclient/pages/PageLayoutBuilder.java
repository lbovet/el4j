/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.pages;

/**
 * Interface to place views on a page. This interface extends the one from 
 * Spring RCP to be able to place multiple views on a page on different 
 * locations (e.g. center, right, left and so on).
 * 
 * <b>ATTENTION:</b> This class has the same name in Spring RCP. The idea is 
 * that the people from Spring RCP will change their class in a next release
 * so we do not have to serve a separate class in the future.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Any Spring RCP developer
 * @author Martin Zeltner (MZE)
 */
public interface PageLayoutBuilder
    extends org.springframework.richclient.application.PageLayoutBuilder {
    
    /**
     * Adds the view with the given view descriptor id to the the current page.
     * The position argument is used to place the view.
     * 
     * @param viewDescriptorId
     *            Is the id of the view to add to this page.
     * @param positionArgument
     *            Is the argument to know where to place the given view.
     */
    public void addView(String viewDescriptorId, Object positionArgument);
}

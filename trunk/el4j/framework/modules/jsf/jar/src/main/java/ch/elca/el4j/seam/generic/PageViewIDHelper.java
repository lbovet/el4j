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
package ch.elca.el4j.seam.generic;

import javax.faces.context.FacesContext;

/**
 * Helper class providing static methods to map between master/detail page
 * view ids and entity shortnames. 
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author  Baeni Christoph (CBA)
 */
public final class PageViewIDHelper {
    /**
     * The hidden constructor.
     */
    private PageViewIDHelper() { }

    /**
     * @param entityShortName    the short name of the entity
     * @return                   the default detail page of this entity
     */
    public static String getDefaultDetailPage(String entityShortName) {
        if (entityShortName != null) {
            return "/" + entityShortName + ".xhtml";
        } else {
            return null;
        }
    }

    /**
     * @param entityShortName    the short name of the entity
     * @return                   the default master page of this entity
     */
    public static String getDefaultMasterPage(String entityShortName) {
        if (entityShortName != null) {
            return "/" + entityShortName + "Master.xhtml";
        } else {
            return null;
        }
    }

    /**
     * Guess entity shortname from the current JSF view id.
     * 
     * @return Returns the guessed entity shortname.
     */
    public static String deriveEntityShortName() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = facesContext.getViewRoot().getViewId();
        String entityShortName = viewId.replaceAll("^/", "").replaceAll(
            "(Master)?\\.xhtml$", "");

        return entityShortName;
    }
}

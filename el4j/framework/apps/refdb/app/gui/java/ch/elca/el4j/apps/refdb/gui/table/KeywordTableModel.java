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
package ch.elca.el4j.apps.refdb.gui.table;

import org.springframework.context.MessageSource;
import org.springframework.richclient.table.BeanTableModel;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;

/**
 * Table model for keyword dtos.
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
public class KeywordTableModel extends BeanTableModel {

    /**
     * Constructor.
     * 
     * @param messageSource That the table can fetch the column names. 
     */
    public KeywordTableModel(MessageSource messageSource) {
        super(KeywordDto.class, messageSource);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Display properties name and description of a keyword.
     */
    protected String[] createColumnPropertyNames() {
        return new String[] {"name", "description"};
    }

    /**
     * {@inheritDoc}
     * 
     * Both displayed columns are strings.
     */
    protected Class[] createColumnClasses() {
        return new Class[] {String.class, String.class};
    }
}

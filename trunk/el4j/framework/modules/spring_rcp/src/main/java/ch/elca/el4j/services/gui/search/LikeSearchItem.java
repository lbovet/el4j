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
package ch.elca.el4j.services.gui.search;

import org.springframework.util.StringUtils;

import ch.elca.el4j.services.search.criterias.AbstractCriteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;

/**
 * Is a like pattern search item.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class LikeSearchItem extends AbstractSearchItem {
    /**
     * Search item id.
     */
    public static final String DEFAULT_SEARCH_ITEM_ID = "like";

    /**
     * Search item type.
     */
    public static final Class DEFAULT_SEARCH_ITEM_TYPE = String.class;

    /**
     * Search item initial value.
     */
    public static final Object DEFAULT_SEARCH_ITEM_INITIAL_VALUE = "";

    /**
     * Default constructor.
     */
    public LikeSearchItem() {
        setId(DEFAULT_SEARCH_ITEM_ID);
        setType(DEFAULT_SEARCH_ITEM_TYPE);
        setInitialValue(DEFAULT_SEARCH_ITEM_INITIAL_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractCriteria[] getCriterias(Object[] values) {
        AbstractCriteria[] criterias = null;
        if (values.length >= 1) {
            String field = getTargetProperty();
            String stringValue = (String) values[0];
            if (StringUtils.hasText(stringValue)) {
                LikeCriteria patternCriteria 
                    = LikeCriteria.caseInsensitive(field, stringValue);
                criterias = new AbstractCriteria[] {patternCriteria};
            }
        }
        return criterias == null ? new AbstractCriteria[0] : criterias;
    }
}

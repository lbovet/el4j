/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.i18n;

import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.utils.DialogUtils;
import ch.elca.el4j.util.dom.reflect.EntityType;


/**
 * A message provider mimicking the key generation strategy from MessageUtils.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
@Deprecated
public class OldStyleMessageProvider extends MessageProvider {
    /**
     * Constructor.
     * 
     * @param ms the message source to use
     */
    public OldStyleMessageProvider(MessageSource ms) {
        super(ms);
    }

    /** {@inheritDoc} */
    @Override
    public Fetcher forConfirmation(String action, EntityType type, 
        int multiplicity) {
        
        String typeName = type.name;
        final String NAME_SUFFIX = "Dto";
        if (typeName.endsWith(NAME_SUFFIX)) {
            typeName = typeName.substring(
                0, typeName.length() - NAME_SUFFIX.length());
        }
        
        String decoratedCode = DialogUtils.decorateCode(
            StringUtils.uncapitalize(typeName) + StringUtils.capitalize(action),
            multiplicity);
        return new PrefixFetcher(prefixes(decoratedCode));
    }
}

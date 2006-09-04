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
package ch.elca.el4j.services.i18n;

import static org.springframework.util.StringUtils.capitalize;
import static org.springframework.util.StringUtils.uncapitalize;

import org.springframework.context.MessageSource;

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
     * Constructor
     * @param ms the message source to use
     */
    public OldStyleMessageProvider(MessageSource ms) {
        super(ms);
    }

    /** {@inheritDoc} */
    @Override
    public Fetcher forConfirmation(String action, EntityType type, int multiplicity) {
        String typeName = type.name;
        if (typeName.endsWith("Dto")) {
            typeName = typeName.substring(0, typeName.length() - 3);
        }
        
        String decoratedCode = DialogUtils.decorateCode(
            uncapitalize(typeName) + capitalize(action), multiplicity);
        return new PrefixFetcher(prefixes(decoratedCode));
    }
}

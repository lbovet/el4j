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
package ch.elca.el4j.services.persistence.hibernate.dialect;

import org.hibernate.dialect.DerbyDialect;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.TableHiLoGenerator;

/**
 * Temporary fix to re-enable flexible native key generation. Safety of this
 * fix is currently beeing determined.
 * 
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
// TODO: verify safetly of identity and sequence generation strategy.
// mail to hibernate mailing list currently pending.
public class PatchedDerbyDialect extends DerbyDialect {

    /** {@inheritDoc} */
    @Override
    public Class getNativeIdentifierGeneratorClass() {
        if ( supportsIdentityColumns() ) {
            return IdentityGenerator.class;
        }
        else if ( supportsSequences() ) {
            return SequenceGenerator.class;
        }
        else {
            return TableHiLoGenerator.class;
        }        
    }
    
}

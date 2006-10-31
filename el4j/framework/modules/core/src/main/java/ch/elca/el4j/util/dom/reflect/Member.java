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
package ch.elca.el4j.util.dom.reflect;

import ch.elca.el4j.util.collections.helpers.Function;

/** represents a type's member.
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
public abstract class Member {
    /** Maps a member to its name. */
    static final Function<Member, String> toName
        = new Function<Member, String>() {
            public String apply(Member d) {
                return d.name;
            }
        };
    
    /** the entity type declaring this member. */
    public final EntityType declaringType;
    
    /** this member's name. */
    public final String name;
    
    /***/
    Member(EntityType declaring, String n) {
        declaringType = declaring;
        name = n;
    }
}
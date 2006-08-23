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
package ch.elca.el4j.xmlmerge.matcher;

import org.jdom.Element;

import ch.elca.el4j.xmlmerge.Matcher;

/**
 * Elements compared with this matcher never match.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$$URL$$",
 *    "$$Revision$$",
 *    "$$Date$$",
 *    "$$Author$$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class SkipMatcher implements Matcher {

    /**
     * {@inheritDoc}
     */
    public boolean matches(Element originalElement, Element patchElement) {
        return false;
    }

}

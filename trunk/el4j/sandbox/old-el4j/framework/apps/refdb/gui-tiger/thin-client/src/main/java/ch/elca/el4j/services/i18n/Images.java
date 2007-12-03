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

import java.awt.Image;

import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.image.NoSuchImageResourceException;

import ch.elca.el4j.util.codingsupport.annotations.Preliminary;
import ch.elca.el4j.util.dom.reflect.EntityType;


/**
 * Provides localized images for entity types.
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
@Preliminary
public class Images {
    /** the backing image source. */
    ImageSource m_source;
    
    /** creates a new Images.
     * @param s the backing ImageSource
     */
    public Images(ImageSource s) {
        m_source = s;
    }
    
    /** return the image keyed {@code key} for the entity type 
     * {@code entityType} for a ch.elca.el4j.services.richclient element with schema
     * {@code schema}.
     */ 
    public Image getImage(EntityType entityType, String schema, String key) {
        String lkey = schema + "." + key;
        try {
            return m_source.getImage(
                lkey + "." + MessageProvider.instance().keyFor(entityType)
            );
        } catch (NoSuchImageResourceException e) {
            return m_source.getImage(lkey);
            // TODO: catch NoSuchImageException?
        }
    }
}

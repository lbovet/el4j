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
package ch.elca.el4j.apps.refdb.rules;

/**
 * RefdbBoundaries for reference database application dtos.
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
public final class RefdbBoundaries {
    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_KEYWORD_NAME = 64;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_KEYWORD_DESCRIPTION = 256;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_REFERENCE_NAME = 64;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_REFERENCE_HASHVALUE = 64;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_REFERENCE_DESCRIPTION = 256;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_REFERENCE_VERSION = 64;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_LINK_URL = 500;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_FORMALPUBLICATION_AUTHORNAME = 64;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_FORMALPUBLICATION_PUBLISHER = 64;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_BOOK_ISBNNUMBER = 20;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_ANNOTATION_ANNOTATOR = 64;

    /**
     * Max length boundary.
     */
    public static final int MAX_LENGTH_FILE_NAME = 64;


    /**
     * Min value boundary.
     */
    public static final int MIN_VALUE_FORMALPUBLICATION_PAGENUM = 1;

    /**
     * Max value boundary.
     */
    public static final int MAX_VALUE_FORMALPUBLICATION_PAGENUM = 9999;

    /**
     * Min value boundary.
     */
    public static final int MIN_VALUE_ANNOTATION_GRADE = 0;

    /**
     * Max value boundary.
     */
    public static final int MAX_VALUE_ANNOTATION_GRADE = 10;

    /**
     * Max size boundary.
     */
    public static final int MAX_SIZE_ANNOTATION_CONTENT = 10 * 1024 * 1024;

    /**
     * Max size boundary.
     */
    public static final int MAX_SIZE_FILE_CONTENT = 10 * 1024 * 1024;
}

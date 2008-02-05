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
package ch.elca.el4j.seam.generic.humanization;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

import org.jboss.seam.international.LocaleSelector;

/**
 * Static humanization functions intended to be included in facelets tag
 * library. We use the term <em>humanization</em> for the translation 
 * between technical names (e.g. names of entities and entity field) 
 * and names that are visible to users. <br> <br> 
 *  
 * The implementation is backed by .properties file but also offers a 
 * fallback humanization of the "fooBarBaz -> Foo Bar Baz" scheme. 
 * Humanization can be internationalized.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *  
 * @author Baeni Christoph (CBA)
 */
public final class Humanization {
    /**
     * The resource bundle holding the humanization strings.
     */
    private static ResourceBundle s_resourceBundle = null;
    
    /**
     * The locale string of the currently loaded resource bundle.
     */
    private static String s_localeString = null;
    
    /**
     * The hidden constructor.
     */
    private Humanization() { }

    /**
     * @return    the currently loaded resource bundle
     */
    private static ResourceBundle getResourceBundle() {
        Locale locale = LocaleSelector.instance().getLocale();
        if (s_resourceBundle == null
            || !s_localeString.equals(locale.toString())) {
            
            try {
                s_resourceBundle = ResourceBundle.getBundle("humanization",
                    LocaleSelector.instance().getLocale());
                s_localeString = locale.toString();
            } catch (MissingResourceException e) {
                s_resourceBundle = new ListResourceBundle() {
                    private final Object[][] m_contents = {};

                    public Object[][] getContents() {
                        return m_contents;
                    }
                };
            }
        }

        return s_resourceBundle;
    }

    /**
     * @param str    the string to capitalize
     * @return       the capitalized string
     */
    public static String capitalize(String str) {
        if (str.length() >= 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return "";
        }
    }

    /**
     * @param str    the string to uncapitalize
     * @return       the uncapitalized string
     */
    public static String uncapitalize(String str) {
        if (str.length() >= 1) {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        } else {
            return "";
        }
    }

    /**
     * @param str    the string to humanize
     * @return       the humanized string
     */
    public static String defaultHumanize(String str) {
        java.util.regex.Pattern capitalWord = java.util.regex.Pattern
            .compile("\\p{javaUpperCase}?[^\\p{javaUpperCase}]*");
        Matcher matcher = capitalWord.matcher(str);

        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            if (buffer.length() > 0) {
                buffer.append(" ");
            }
            matcher.appendReplacement(buffer, matcher.group());
        }
        matcher.appendTail(buffer);

        return capitalize(buffer.toString().trim());
    }

    /**
     * @param context         the context
     * @param propertyName    the property name
     * @return                the corresponding string from the resource bundle
     */
    private static String fetchProperty(String context, String propertyName) {
        ResourceBundle resourceBundle = getResourceBundle();
        String str;

        try {
            str = resourceBundle.getString(context + "." + propertyName);
        } catch (NullPointerException e) {
            str = null;
        } catch (MissingResourceException e) {
            str = null;
        }

        return str;
    }

    /**
     * @param context         the context
     * @param propertyName    the property name
     * @return                the corresponding string from the resource bundle
     *                        or the humanized property name if not available
     */
    private static String fetchPropertyOrHumnaize(String context,
        String propertyName) {
        String str = fetchProperty(context, propertyName);

        if (str == null) {
            str = defaultHumanize(propertyName);
        }

        return str;
    }

    /**
     * @param entityShortName    the entity short name
     * @return                   the humanized entity short name
     */
    public static String getEntityName(String entityShortName) {
        return fetchPropertyOrHumnaize("singular", entityShortName);
    }

    /**
     * @param entityShortName    the entity short name
     * @return                   the humanized entity short name (plural)
     */
    public static String getEntityNamePlural(String entityShortName) {
        String str = fetchProperty("plural", entityShortName);

        if (str == null) {
            str = getEntityName(entityShortName) + "s";
        }

        return str;
    }
    
    /**
     * @param entityShortName    the entity short name
     * @param sentence           the sentence identifier
     * @return                   the humanized sentence
     */
    public static String getEntitySentence(String entityShortName,
        String sentence) {
        
        return fetchProperty(sentence, entityShortName);
    }

    /**
     * @param entityShortName    the entity short name
     * @param fieldName          the field name in that entity
     * @return                   the humanized field name
     */
    public static String getFieldName(
        String entityShortName, String fieldName) {
        
        return fetchPropertyOrHumnaize("label." + entityShortName, fieldName);
    }

    /**
     * @param entityShortName    the entity short name
     * @param fieldName          the field name in that entity
     * @return                   the humanized field hint name
     */
    public static String getFieldHint(
        String entityShortName, String fieldName) {
        
        return fetchProperty("hint." + entityShortName, fieldName);
    }

    /**
     * @param entityShortName    the entity short name
     * @param groupName          the field group name
     * @return                   the humanized field group name
     */
    public static String getFieldGroupName(String entityShortName,
        String groupName) {
        return fetchPropertyOrHumnaize("group." + entityShortName, groupName);
    }

    /**
     * @param entityShortName    the entity short name
     * @param fieldName          the field name in that entity
     * @param enumName           the enum member
     * @return                   the humanized enum member name
     */
    public static String getFieldEnumName(String entityShortName,
        String fieldName, String enumName) {
        return fetchPropertyOrHumnaize("enum." + entityShortName + "."
            + fieldName, enumName);
    }

    /**
     * @param entityShortName    the entity short name
     * @param fieldName          the field name in that entity
     * @param value              the boolean value
     * @return                   the humanized boolean value name
     */
    public static String getFieldBoolText(String entityShortName,
        String fieldName, boolean value) {
        String str = fetchProperty("bool." + entityShortName + "." + fieldName,
            Boolean.valueOf(value).toString());

        if (str == null) {
            str = value ? "Yes" : "No";
        }

        return str;
    }
}

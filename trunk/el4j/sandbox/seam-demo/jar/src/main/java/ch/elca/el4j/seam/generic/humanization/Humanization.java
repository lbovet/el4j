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
 * library. Backed by .properties file but also offers a fallback humanization
 * of the "fooBarBaz -> Foo Bar Baz" scheme.
 * 
 * @author Baeni Christoph (CBA)
 */
public class Humanization {
    private static ResourceBundle resourceBundle = null;
    private static String localeString = null;

    private static ResourceBundle getResourceBundle() {
        Locale locale = LocaleSelector.instance().getLocale();
        if (resourceBundle == null || !localeString.equals(locale.toString())) {
            try {
                System.out.println(LocaleSelector.instance().getLocaleString());
                resourceBundle = ResourceBundle.getBundle("humanization",
                    LocaleSelector.instance().getLocale());
                localeString = locale.toString();
            } catch (MissingResourceException e) {
                resourceBundle = new ListResourceBundle() {
                    private final Object[][] contents = {};

                    public Object[][] getContents() {
                        return contents;
                    }
                };
            }
        }

        return resourceBundle;
    }

    public static String capitalize(String str) {
        if (str.length() >= 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return "";
        }
    }

    public static String uncapitalize(String str) {
        if (str.length() >= 1) {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        } else {
            return "";
        }
    }

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

    private static String fetchPropertyOrHumnaize(String context,
        String propertyName) {
        String str = fetchProperty(context, propertyName);

        if (str == null) {
            str = defaultHumanize(propertyName);
        }

        return str;
    }

    public static String getEntityName(String entityShortName) {
        return fetchPropertyOrHumnaize("singular", entityShortName);
    }

    public static String getEntityNamePlural(String entityShortName) {
        String str = fetchProperty("plural", entityShortName);

        if (str == null) {
            str = getEntityName(entityShortName) + "s";
        }

        return str;
    }

    public static String getFieldName(String entityShortName, String fieldName) {
        return fetchPropertyOrHumnaize("label." + entityShortName, fieldName);
    }

    public static String getFieldHint(String entityShortName, String fieldName) {
        return fetchProperty("hint." + entityShortName, fieldName);
    }

    public static String getFieldGroupName(String entityShortName,
        String groupName) {
        return fetchPropertyOrHumnaize("group." + entityShortName, groupName);
    }

    public static String getFieldEnumName(String entityShortName,
        String fieldName, String enumName) {
        return fetchPropertyOrHumnaize("enum." + entityShortName + "."
            + fieldName, enumName);
    }

    public static String getFieldBoolText(String entityShortName,
        String fieldName, boolean value) {
        String str = fetchProperty("bool." + entityShortName + "." + fieldName,
            new Boolean(value).toString());

        if (str == null) {
            str = value ? "Yes" : "No";
        }

        return str;
    }
}

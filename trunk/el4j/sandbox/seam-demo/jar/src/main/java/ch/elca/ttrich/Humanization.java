package ch.elca.ttrich;

import java.util.regex.*;
import java.util.*;

/**
 * Static humanization functions intended to be included in facelets tag library.
 * Backed by .properties file but also offers a fallback humanization of the
 * "fooBarBaz -> Foo Bar Baz" scheme.
 *
 * @author  Baeni Christoph (CBA)
 */
public class Humanization {
	private static ResourceBundle resourceBundle = null;
	
	private static ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			try {
				resourceBundle = ResourceBundle.getBundle("humanization");
			} catch (MissingResourceException  e) {
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
			return str.substring(0,1).toUpperCase() + str.substring(1);
		} else {
			return "";
		}
	}
	
	public static String uncapitalize(String str) {
		if (str.length() >= 1) {
			return str.substring(0,1).toLowerCase() + str.substring(1);
		} else {
			return "";
		}
	}
	
	public static String defaultHumanize(String str) {
		java.util.regex.Pattern capitalWord = java.util.regex.Pattern.compile("\\p{javaUpperCase}?[^\\p{javaUpperCase}]*");
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
	
	private static String fetchProperty(String context, String propertyName)  {
		ResourceBundle resourceBundle = getResourceBundle();
		String str;
		
		try {
			str = resourceBundle.getString(context + "." + propertyName);
		} catch(NullPointerException e) {
			str = null;
		} catch(MissingResourceException e) {
			str = null;
		}
		
		return str;
	}
	
	private static String fetchPropertyOrHumnaize(String context, String propertyName) {
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
			str = getEntityName(entityShortName) +"s";
		}
		
		return str;
	}
	
	public static String getFieldName(String entityShortName, String fieldName) {
		return fetchPropertyOrHumnaize("label." + entityShortName, fieldName);
	}

	public static String getFieldHint(String entityShortName, String fieldName) {
		return fetchProperty("hint." + entityShortName, fieldName);
	}
	
	public static String getFieldGroupName(String entityShortName, String groupName) {
		return fetchPropertyOrHumnaize("group." + entityShortName, groupName);
	}
	
	public static String getFieldEnumName(String entityShortName, String fieldName, String enumName) {
		return fetchPropertyOrHumnaize("enum." + entityShortName + "." + fieldName, enumName);
	}
	
	public static String getFieldBoolText(String entityShortName, String fieldName, boolean value) {
		String str = fetchProperty("bool." + entityShortName + "." + fieldName , new Boolean(value).toString());
		
		if (str == null) {
			str = value ? "Yes" : "No";
		}
		
		return str;
	}
}




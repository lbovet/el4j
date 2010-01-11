/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2009 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.util.codelist;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the base functionality for enums which
 * represent codelists and implement the Interface Codelist.
 * 
 * Since the extensibility of enum types is not supported by the 
 * language construct, the functional implementation is provided
 * in this utility class.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Jonas Hauenstein (JHN)
 */
public class CodelistUtility {
	
	/**
	 * The ResourceBundle base name.
	 */
	private String resBaseName;
	
	/**
	 * Constructor.
	 *
	 * @param resourceBaseName Base name of the associated ResourceBundle
	 */
	public CodelistUtility(String resourceBaseName) {
		resBaseName = resourceBaseName;
	}
	
	/**
	 * Getter for the short textual description of the code in a given language.
	 *
	 * This method returns the String with the property suffix ".short".
	 *
	 * @param code Code for which the text is loaded
	 * @param lang Java Locale of the desired language for the returned text
	 * @return The short textual description of the code
	 */
	public String loadShortText(String code, Locale lang) {
		ResourceBundle bundle = ResourceBundle.getBundle(resBaseName, lang);
		return bundle.getString(code + ".short");
	}

	/**
	 * Getter for the long textual description of the code in a given language.
	 *
	 * This method returns the String with the property suffix ".long".
	 *
	 * @param code Code for which the text is loaded
	 * @param lang Java Locale of the desired language for the returned text
	 * @return The long textual description of the code
	 */
	public String loadLongText(String code, Locale lang) {
		ResourceBundle bundle = ResourceBundle.getBundle(resBaseName, lang);
		return bundle.getString(code + ".long");
	}
	
	
	/**
	 * Returns a list of all java locales for which a translation of
	 * the textual description is available. 
	 *
	 * Returns an empty list if no translations were found.
	 *
	 * @return List of java locales
	 */
	public List<Locale> getLocales() {
		return getAvailableLocales(resBaseName);
	}
	
	/**
	 * Returns a list of all java locales for which a .properties file
	 * corresponding to the ResourceBundle base name is available. 
	 *
	 * Returns an empty list if no .properties files were found for
	 * the given base name.
	 *
	 * @param resourceBaseName Base name of the ResourceBundle
	 * @return List of java locales
	 */
	public static List<Locale> getAvailableLocales(String resourceBaseName) {
		String subdir = "";
		String pbname = resourceBaseName;
		List<Locale> loclist = new ArrayList<Locale>();
		
		//check for subdir syntax and reconstruct path if necessary (separated by . or /)
		if (resourceBaseName.contains(".")) {
			int li = resourceBaseName.lastIndexOf(".");
			subdir = resourceBaseName.substring(0, li);
			subdir.replace(".", "/");
			pbname = resourceBaseName.substring(li + 1);
		} else if (resourceBaseName.contains("/")) {
			int li = resourceBaseName.lastIndexOf("/");
			subdir = resourceBaseName.substring(0, li);
			pbname = resourceBaseName.substring(li + 1);
		}
		
		//get list of corresponding .properties files
		File pdir = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			URL purl = cld.getResource(subdir);
			pdir = new File(purl.getFile());
		} catch (NullPointerException e) {
			return loclist;
		}
		if (pdir.exists()) {
			String[] flist = pdir.list();
			
			//regexp to match all .properties files including mask for locales
			Pattern bfpattern = Pattern.compile("^" + pbname + "_(([a-z]{2})(_([A-Z]{2})){0,1})\\.properties$");
	
			for (String e : flist) {
				Matcher m = bfpattern.matcher(e);
				if (m.find()) {
					//create new locale depending on regexp result
					//(single _ or double _ if language variant is specified)
					Locale l = (m.group(4) == null) ? new Locale(m.group(1)) : new Locale(m.group(2), m.group(4));
					loclist.add(l);
				}
			}
		}	
		return loclist;	
	}
	
}

package ch.elca.ttrich;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

/**
 * 
 * @see Searching
 *
 * @author  Baeni Christoph (CBA)
 */
@Name("searching")
@Scope(EVENT)
public class Searching implements Serializable {	
	@RequestParameter
	private String restrict;
	
	private String encodeString(String str) {
		return str.replaceAll("\\\\","\\\\5c").replaceAll("=","\\\\3d").replaceAll(",","\\\\2c");
	}
	
	private String decodeString(String str) {
		return str.replaceAll("\\\\2c",",").replaceAll("\\\\3d","=").replaceAll("\\\\5c","\\\\");
	}
	
	private HashMap<String,String> decodeRestrictionsInternal(String restrictions) {
		HashMap<String,String> restrictionMap = new HashMap<String,String>();

		if (restrictions != null) {
			String[] restrictionTuples = restrictions.split(",");
			for (int i=0; i < restrictionTuples.length; i++) {
				String[] parts = restrictionTuples[i].split("=");
				if (parts.length == 2) {
					String name = decodeString(parts[0]);
					String value = decodeString(parts[1]);
					restrictionMap.put(name, value);
				}
			}
		}
		
		return restrictionMap;
	}
	
	private String cachedRestrictions = null;
	private HashMap<String,String> cachedRestrictionMap = null;
	private HashMap<String,String> decodeRestrictions(String restrictions) {
		if ((cachedRestrictions == null) ||
			((restrictions != null) && !restrictions.equals("") && !cachedRestrictions.equals(restrictions))) {
			
			cachedRestrictionMap = decodeRestrictionsInternal(restrictions);
			cachedRestrictions = restrictions;
		}

		return cachedRestrictionMap;
	}
	
	private String encodeRestrictions(Map<String,String> restrictions) {
		StringBuffer buf = new StringBuffer("r");
		
		for (String name: restrictions.keySet()) {
			String value = restrictions.get(name);
			
			buf.append(",");
			buf.append(encodeString(name) + "=" + encodeString(value));
		}
		
		return buf.toString();
	}
	
	public String computeNewRestrictionsParameter(String currentRestrictions, String restriction, Object value) {
		Map<String,String> restrictionsMap = (HashMap<String,String>)decodeRestrictions(currentRestrictions).clone();
		
		if (value != null) {
			restrictionsMap.put(restriction, value.toString());
		} else {
			restrictionsMap.remove(restriction);
		}
		
		return encodeRestrictions(restrictionsMap);
	}
	
	public HashMap<String,String> getRestrictionMap() {
		return decodeRestrictions(restrict);
	}
	
	public String getRestrictionString() {
		if (restrict == null) {
			return "";
		}
		
		return restrict;
	}
	
	public boolean isRestrictionInEffect(String restrictions, String restriction, Object value) {
		String restrictionString =  decodeStringRestriction(restrictions, restriction);
		
		if (value == null) {
			return (restrictionString == null);
		} else {
			return value.toString().equals(restrictionString);
		}
	}
	
	private String decodeStringRestriction(String restrictions, String restriction) {
		Map<String,String> restrictionsMap = decodeRestrictions(restrictions);
		
		return restrictionsMap.get(restriction);
	}
}
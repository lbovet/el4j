package ch.elca.el4j.seam.generic;

import java.util.HashMap;

/**
 * A very simple "cache" implementation for storing results of expensive computations.
 * 
 * CAUTION: This "cache" assumes that a value, once stored, remains valid forever!
 *
 * @author  Baeni Christoph (CBA)
 */
public class ResultCache {
	private HashMap<String,Object> cache = new HashMap<String,Object>();
	
	public String computeKey(String... parts) {
		String cacheKey = "";
		
		for (String part: parts) {
			cacheKey += "#" + part.replaceAll("\\\\","\\\\\\\\").replaceAll("#","\\\\#");
		}
		
		return cacheKey;
	}
	
	public boolean doesExist(String key) {
		return cache.containsKey(key);
	}
	
	public Object lookup(String key) {
		return cache.get(key);
	}
	
	public void store(String key, Object value) {
		cache.put(key, value);
	}
}

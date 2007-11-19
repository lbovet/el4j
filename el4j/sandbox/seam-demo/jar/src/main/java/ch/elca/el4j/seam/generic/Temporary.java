package ch.elca.el4j.seam.generic;

import java.io.Serializable;
import org.jboss.seam.annotations.*;
import static org.jboss.seam.ScopeType.EVENT;
import java.util.*;

/**
 * 
 * @see Temporary
 *
 * @author  Baeni Christoph (CBA)
 */
@Name("temporary")
@Scope(EVENT)
public class Temporary implements Serializable{
	private HashMap<String,Object> map;
	
	public Temporary() {
		map = new HashMap<String,Object>();
	}
	
	public HashMap<String,Object> getMap() {
		return map;
	}
}
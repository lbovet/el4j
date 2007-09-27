package ch.elca.ttrich;

import javax.faces.context.FacesContext;

/**
 * Helper class providing static methods to map between master/detail page
 * view ids and entity shortnames. 
 *
 * @author  Baeni Christoph (CBA)
 */
public class PageViewIDHelper {
	public static String getDefaultDetailPage(String entityShortName) {
		if (entityShortName != null) {
			return "/" + entityShortName + ".xhtml";
		} else {
			return null;
		}
	}

	public static String getDefaultMasterPage(String entityShortName) {
		if (entityShortName != null) {
			return "/" + entityShortName + "Master.xhtml";
		} else {
			return null;
		}
	}

	 /**
	  * Guess entity shortname from the current JSF view id.
	  * 
      * @return Returns the guessed entity shortname.
      */
	public static String deriveEntityShortName() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String viewId = facesContext.getViewRoot().getViewId();
		String entityShortName = viewId.replaceAll("^/","").replaceAll("(Master)?\\.xhtml$","");

		return entityShortName;
	}
}

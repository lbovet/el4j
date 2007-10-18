package ch.elca.el4j.seam.generic;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Replacement JSF converter for seam's EntityConverter.
 * Seam's EntityConverter cannot be used without a seam managed persistence context!
 * 
 * @see org.jboss.seam.ui.converter.EntityConverter
 * 
 * @author  Baeni Christoph (CBA)
 */
@Name("ch.elca.el4j.seam.generic.EntityConverter")
@Scope(CONVERSATION)
@Converter
@BypassInterceptors
public class EntityConverter implements javax.faces.convert.Converter, Serializable {
	private HashMap<Long,Object> mapping = new HashMap<Long,Object>();
	private ObjectManager m_ObjectManager;
	private Long m_NextId = new Long(1);

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return mapping.get(new Long(value));
	}

	public String getAsString(FacesContext contect, UIComponent component, Object entity) {
		Long id = allocateId();
		
		mapping.put(id, entity);
		
		return id.toString();
	}
	
	private Long allocateId() {
		return m_NextId++;
	}

	private ObjectManager getObjectManager() {
		if (m_ObjectManager == null) {
			m_ObjectManager = (ObjectManager)Component.getInstance("objectManager");
		}
		
		return m_ObjectManager;
	}
}

/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.richclient.naming;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.FormPropertyFaceDescriptor;
import org.springframework.binding.form.support.DefaultFormPropertyFaceDescriptor;
import org.springframework.binding.form.support.MessageSourceFormPropertyFaceDescriptorSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Instances of this class are MessageSourceFormPropertyFaceDescriptorSource(s) 
 * where the default messages that are returned if no localized message is 
 * available can be configured.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */

public class ConfigurablePropertyFaceDescriptorSource extends
    MessageSourceFormPropertyFaceDescriptorSource {
    
    /** the set of property face properties. */
    public enum Prop {
        displayName ("displayName"),
        caption     ("caption"    ),
        description ("description"),
        encodedLabel("label"      ),
        icon        ("icon"       );
        
        /** the property's message lookup key. */
        String m_key;
        /***/
        Prop(String key) {
            m_key = key;
        }
    }

    /** 
     * the default values, as nested map. The first key is the form property's 
     * path, the second the display property.
     * @see Naming#getDefaultPropertyFace(ch.elca.el4j.services.dom.Property)
     **/
    public Map<String, EnumMap<Prop, String>> defaults 
        = new HashMap<String, EnumMap<Prop, String>>();
    
    /**
     * Looks up the localized value of the desired FormPropertyFaceDescriptor
     * property, delegating to 
     * {@link #getMessageKeys(FormModel, String, String)} for message key 
     * generation.
     * 
     * except that the default from {@link #defaults} is used if the message is 
     * not found.
     * @param prop the face descriptor property wanted
     * @param formModel the requesting form model (for getMessageKeys)
     * @param formPropertyPath (for getMessageKeys)
     * @return the localized message or the value installed in {@link #defaults}
     *         if no localized message is available.  
     */
    protected String getMessage(FormModel formModel, String formPropertyPath, 
                                Prop prop) {
        
        String[] keys = getMessageKeys(formModel, formPropertyPath, prop.m_key);
        try {
            return getMessageSourceAccessor().getMessage(
                new DefaultMessageSourceResolvable(keys, null, null)
            );
        } catch (NoSuchMessageException e) {
            EnumMap<Prop, String> m = defaults.get(formPropertyPath);
            if (m != null) {
                String def = m.get(prop);
                if (def != null) {
                    return def;
                }
            }
            return formPropertyPath + "." + prop;                
        }
    }
    
    /** 
     * same as
     * {@link org.springframework.binding.form.support.MessageSourceFormPropertyFaceDescriptorSource#loadFormPropertyFaceDescriptor(FormModel,String)
     * super implementation} except that settings not available in the backing
     * message source are taken from {@link #defaults}.
     * @param formModel as super
     * @param formPropertyPath as super
     * @return as super
     */
    protected FormPropertyFaceDescriptor 
    loadFormPropertyFaceDescriptor(FormModel formModel, 
                                   String formPropertyPath) {
        
        // as in superclass, except that a different helper method is called
        String displayName  = getMessage(formModel, formPropertyPath, Prop.displayName);
        String caption      = getMessage(formModel, formPropertyPath, Prop.caption);
        String description  = getMessage(formModel, formPropertyPath, Prop.description);
        String encodedLabel = getMessage(formModel, formPropertyPath, Prop.encodedLabel);
        Icon icon = getIconSource().getIcon(getMessage(formModel, formPropertyPath, "icon"));
        return new DefaultFormPropertyFaceDescriptor(
            displayName, caption, description, encodedLabel, icon
        );
    }
}

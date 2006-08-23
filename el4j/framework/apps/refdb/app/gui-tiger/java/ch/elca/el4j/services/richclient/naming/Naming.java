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

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import ch.elca.el4j.core.exceptions.MisconfigurationRTException;
import ch.elca.el4j.services.dom.info.EntityType;
import ch.elca.el4j.services.dom.info.Property;
import ch.elca.el4j.services.richclient.naming.ConfigurableFieldFaceSource.Prop;
import ch.elca.el4j.util.codingsupport.annotations.ImplementationAssumption;



/**
 * provides localized names and descriptions for DOM elements.
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
public class Naming {
    /** the default Naming. */
    private static Naming s_instance;
    
    /** the backing message source. */
    MessageSourceAccessor m_source;

    /** @param ms the backing message source */
    public Naming(MessageSource ms) {
        m_source = new MessageSourceAccessor(ms);
    }
    
    /** returns {@code t}'s lookup key.
     * @param t .
     * @return .
     */
    protected String keyFor(EntityType t) {
        return t.name;
    }
    
    /**
     * returns the localized name for entity type <code>t</code>.
     * @param t .
     * @return .
     */
    public String getName(EntityType t) {
        return m_source.getMessage(keyFor(t) + ".displayName", null, t.name);
    }
    
    /**
     * returns the localized description for entity type <code>c</code>,
     * or <code>null</code> if none is found.
     * @param t .
     * @return .
     */
    public String getDescription(EntityType t) {
        try {
            return m_source.getMessage(
                keyFor(t) + ".Description",
                null,
                ""
            );
        } catch (NoSuchMessageException e) {
            return null;
        }
    }
    
    /**
     * 
     * @param p    the model property 
     * @param key  the visible property
     * @param def  the visible property's default message or
     *             <code>null</code> if there is none
     * @return     the resolved message, or <code>null</code> if none could
     *             be found
     */
    protected String get(Property p, String key, String def) {
        try {
            return m_source.getMessage(new DefaultMessageSourceResolvable(
                new String[] {
                    keyFor(p.declaringType) + "." + p.name + "." + key,
                    p.name + "." + key
                },
                new Object[] {
                    p.declaringType.name
                },
                def
            ));
        } catch (NoSuchMessageException e) {
            return null;
        }
    }
    
    /**
     * returns <code>p</code>'s localized name.
     * @param p .
     * @return .
     */
    public String getName(Property p) {
        // AMS: Personally, I find the key "displayName" a bit verbose 
        // ("Name" would suffice imho), but imho it's not worth creating
        // an incompatibility over.
        return get(p, "displayName", StringUtils.capitalize(p.name));
    }
    
    /**
     * returns <code>p</code>'s localized description, or <code>null</code>
     * if no description is available.
     * @param p .
     * @return .
     */    
    public String getDescription(Property p) {
        return get(p, "description", "");
    }
    
    /**
     * Looks up and returns the localizable string <code>key</code>
     * for a component with schema <code>schema</code> that is 
     * displaying entities of type <code>entityType</code>.
     * 
     *<p>for {@code}getComponentAttribute(Person.class, "Table", "title")}
     *this method checks the message codes
     *<pre>Table.title.Person
     *Table.title</pre>
     *<p>messages can use {0} to refer to the entity type's name.
     * @throws NoSuchMessageException iff no message could be found
     */
    public String getComponentAttribute(EntityType entityType, 
                                        String schema, String key)
        throws NoSuchMessageException {
        
        String lkey = schema + "." + key;
        return m_source.getMessage(new DefaultMessageSourceResolvable(
            new String[] {lkey + "." + keyFor(entityType), lkey},
            new Object[] {getName(entityType)}
        ));
    }
    
    /**
     * Looks up and returns an enum value's localized face property.
     * @param e the enum value whose face property is sought
     * @param key the name of the face property sought
     * @return the value of the face property, or {@code null}
     *         if the face property is not defined.
     */
    @ImplementationAssumption("unqualified enum names are unique within dom")
    public String getEnumValueFaceProperty(Enum<?> e, String key) {
        return getConstantValueFaceProperty(
            e.getDeclaringClass().getSimpleName(),
            e.name(),
            key
        );
    }
    
    /**
     * Looks up and returns an enum value's localized face property.
     * 
     * <p>For real enums, you might prefer 
     * {@link #getEnumValueFaceProperty(Enum, String)};
     * this method is provided for values that are conceptually enums, but are
     * not enums in the source code (boolean values, for instance).
     *  
     * @param type the unqualified name of the type the value belongs to 
     * @param value the name of the value
     * @param key the name of the desired face property
     * @return the value of the face property, or {@code null}
     *         if the face property is not defined.
     */
    public String getConstantValueFaceProperty(String type, String value,
                                               String key) {
        return m_source.getMessage(
            type + "." + value + "." + key,
            (String) null
        );
    }
    
    /**
     * returns a map with the default face properties for the model property
     * <code>p</code>. 
     * @param p
     * @return .
     */
    public EnumMap<Prop, String> getDefaultPropertyFace(Property p) {
        String name = getName(p);
        String label = "&" + name + (p.type.equals(boolean.class) ? "?" : ":");
        
        EnumMap<Prop, String> m = new EnumMap<Prop, String>(Prop.class);
        m.put(Prop.displayName,  name);
        m.put(Prop.caption,      name);
        m.put(Prop.description,  getDescription(p));
        m.put(Prop.encodedLabel, label);
        m.put(Prop.icon,         null);
        return m;
    }
    
    
    /** returns the registered default naming. */
    public static Naming instance() {
        if (s_instance == null) {
            throw new MisconfigurationRTException(
                "no default Naming was registered"
            );
        }
        return s_instance;
    }
    
    /** registers the default naming instance. */
    public static void setInstance(Naming n) {
        s_instance = n;
    }
}
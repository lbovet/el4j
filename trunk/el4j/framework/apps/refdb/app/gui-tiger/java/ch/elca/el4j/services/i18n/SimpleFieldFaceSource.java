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
package ch.elca.el4j.services.i18n;

import java.util.Map;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.form.support.MessageSourceFieldFaceSource;
import org.springframework.richclient.application.View;

import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.dom.reflect.Property;
import ch.elca.el4j.util.registy.Registry;
import ch.elca.el4j.util.registy.impl.StringMapBackedRegistry;

/**
 * Resolves field faces using the schema configured in the containing view.
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
public class SimpleFieldFaceSource extends MessageSourceFieldFaceSource {
    /** The default instance. */
    private static SimpleFieldFaceSource s_instance 
        = new SimpleFieldFaceSource();
    
    /** Constructor for subclasses. */
    protected SimpleFieldFaceSource() { }

    /** Returns the default instance. */
    public static SimpleFieldFaceSource instance() { return s_instance; }
    
    /** Sets the default instance. */
    public static void setInstance(SimpleFieldFaceSource instance) {
        s_instance = instance;
    }
    
    
    /** {@inheritDoc} */
    @Override @SuppressWarnings("unchecked")
    protected String getMessage(FormModel formModel, String formPropertyPath, 
                                String faceDescriptorProperty) {
        Map<String, Object> userMetadata
            = formModel.getFieldMetadata(formPropertyPath).getAllUserMetadata();
        Registry registry = new StringMapBackedRegistry(userMetadata);
        AbstractGenericView gv = registry.get(AbstractGenericView.class);
        String kind;
        if (gv != null) {
            kind = gv.schema;
        } else {
            View v = registry.get(View.class);
            Reject.ifNull(v, "required metadata is missing");
            kind = v.getId();
        }
        
        return MessageProvider.instance().getFieldFaceProperty(
            kind,
            registry.get(Property.class),
            faceDescriptorProperty
        );
    }
}

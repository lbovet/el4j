/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.core.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Mark a class that is annotated with {@link Component} (or compatible annotation) that it should be loaded lazily.
 * Such beans have to be collected like this:
 * <code><context:component-scan base-package="your.package"
		scope-resolver="ch.elca.el4j.core.context.annotations.LazyInitAwareScopeMetadataResolver" />
		</code>
 *
 * @deprecated As of EL4J version 2.0 (and the update to Spring 3) this annotation should be no longer used.
 * Use the newly added annotation {@link Lazy} of Spring 3 instead.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 * 
 * @author Stefan Wismer (SWI)
 * @author Jonas Hauenstein (JHN)
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Deprecated public @interface LazyInit { }

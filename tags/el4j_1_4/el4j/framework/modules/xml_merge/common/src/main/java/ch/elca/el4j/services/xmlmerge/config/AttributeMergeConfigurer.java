/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.xmlmerge.config;

import ch.elca.el4j.services.xmlmerge.ConfigurationException;
import ch.elca.el4j.services.xmlmerge.Configurer;
import ch.elca.el4j.services.xmlmerge.Mapper;
import ch.elca.el4j.services.xmlmerge.Matcher;
import ch.elca.el4j.services.xmlmerge.MergeAction;
import ch.elca.el4j.services.xmlmerge.XmlMerge;
import ch.elca.el4j.services.xmlmerge.action.OrderedMergeAction;
import ch.elca.el4j.services.xmlmerge.action.StandardActions;
import ch.elca.el4j.services.xmlmerge.factory.AttributeOperationFactory;
import ch.elca.el4j.services.xmlmerge.factory.OperationResolver;
import ch.elca.el4j.services.xmlmerge.factory.StaticOperationFactory;
import ch.elca.el4j.services.xmlmerge.mapper.NamespaceFilterMapper;
import ch.elca.el4j.services.xmlmerge.matcher.StandardMatchers;
import ch.elca.el4j.services.xmlmerge.matcher.TagMatcher;

/**
 * Configure to apply actions declared as attributes in the patch DOM.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class AttributeMergeConfigurer implements Configurer {

    /**
     * Attribute namespace.
     */
    public static final String ATTRIBUTE_NAMESPACE 
        = "http://xmlmerge.el4j.elca.ch"; 
    
    /**
     * Action attribute.
     */
    public static final String ACTION_ATTRIBUTE = "action";

    /**
     * Matcher attribute.
     */
    public static final String MATCHER_ATTRIBUTE = "matcher";
    
    /**
     * {@inheritDoc}
     */
    public void configure(XmlMerge xmlMerge) throws ConfigurationException {

        MergeAction defaultMergeAction = new OrderedMergeAction();

        Mapper mapper = new NamespaceFilterMapper(ATTRIBUTE_NAMESPACE);

        defaultMergeAction.setMapperFactory(new StaticOperationFactory(mapper));

        // Configure the action factory
        OperationResolver actionResolver = new OperationResolver(
            StandardActions.class);

        defaultMergeAction.setActionFactory(new AttributeOperationFactory(
            defaultMergeAction, actionResolver, ACTION_ATTRIBUTE,
            ATTRIBUTE_NAMESPACE));

        // Configure the matcher factory
        Matcher defaultMatcher = new TagMatcher();

        OperationResolver matcherResolver = new OperationResolver(
            StandardMatchers.class);

        defaultMergeAction.setMatcherFactory(new AttributeOperationFactory(
            defaultMatcher, matcherResolver, MATCHER_ATTRIBUTE,
            ATTRIBUTE_NAMESPACE));

        xmlMerge.setRootMapper(mapper);
        xmlMerge.setRootMergeAction(defaultMergeAction);
    }

}

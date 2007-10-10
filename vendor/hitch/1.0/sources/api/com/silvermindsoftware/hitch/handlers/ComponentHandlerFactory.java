package com.silvermindsoftware.hitch.handlers;

/**
 * Copyright 2007 Brandon Goodin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.silvermindsoftware.hitch.handlers.component.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ComponentHandlerFactory {

    private static final Log log = LogFactory.getLog(ComponentHandlerFactory.class);

    private Map<Object, ComponentHandler> componentHandler;

    public ComponentHandlerFactory() {

        this.componentHandler = new HashMap<Object, ComponentHandler>();

        ComponentHandler componentHandler = new TextComponentHandler();
        register(JButton.class, componentHandler);
        register(JTextField.class, componentHandler);
        register(JTextArea.class, componentHandler);

        componentHandler = new JLabelComponentHandler();
        register(JLabel.class, componentHandler);

        componentHandler = new ValueComponentHandler();
        register(JFormattedTextField.class, componentHandler);

        componentHandler = new JComboBoxComponentHandler();
        register(JComboBox.class, componentHandler);

        componentHandler = new JListComponentHandler();
        register(JList.class, componentHandler);

        componentHandler = new ToggleComponentHandler();
        register(JRadioButton.class, componentHandler);
        register(JCheckBox.class, componentHandler);
        register(JToggleButton.class, componentHandler);

        componentHandler = new JSpinnerComponentHandler();
        register(JSpinner.class, componentHandler);

        componentHandler = new JSliderComponentHandler();
        register(JSlider.class, componentHandler);

        //--- NEED TO BE ASSIGNED---
//		register(JMenuItem.class, componentHandler);
//		register(JRadioButtonMenuItem.class, componentHandler);
//		register(JCheckBoxMenuItem.class, componentHandler);
//		register(JMenu.class, componentHandler);
//		register(JMenuItem.class, componentHandler);


    }

    /**
     * Register a component handler instance for a key (generally a class)
     *
     * @param key              the key
     * @param componentHandler the component instance that will handle data mapping for the key
     */
    public void register(Object key, ComponentHandler componentHandler) {
        if (log.isDebugEnabled()) {
            log.debug("Adding '" + componentHandler.getClass() + "' to registry under key '" + key + "'.");
        }
        this.componentHandler.put(key, componentHandler);
    }

    /**
     * Looks in the registry for a suitable handler instance based on the 'key'. If no exact match
     * is found and key is of type Class, then we look for a superclass of 'key'. If we find a
     * match, we return it, and register it's handler under the subclass so future lookups are fast.
     *
     * @param key - generally a class (will it ever not be a class?)
     * @return the ComponentHandler for the key
     */
    public ComponentHandler getHandler(Object key) {

		// look for an exact match
		ComponentHandler returnValue = componentHandler.get(key);


		if(null == returnValue){
			// Darn, we didn't find one.
			if (log.isDebugEnabled()) {
				log.debug("Could not find match for key '" + key + "' in registry, searching for subclasses.");
			}
			if(key instanceof Class){
				// The search was for a class, so let's look to see if any of it's superclasses are
				// registered...
				Class keyClass = (Class) key;

				if (log.isDebugEnabled()) {
					log.debug("Searching for superclasses of '" + key + "' in registry.");
				}

				keyClass = keyClass.getSuperclass();
				while(keyClass != null){
					if (log.isDebugEnabled()) {
						log.debug("Looking for a match for " + keyClass);
					}
					returnValue = componentHandler.get(keyClass);
					if(null != returnValue) {
						if (log.isDebugEnabled()) {
							log.debug("Found a match for " + keyClass);
						}
						register(key, returnValue);
						break;
					}
					keyClass = keyClass.getSuperclass();
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Returning '" + returnValue + "' for key '" + key + "'");
		}

		return returnValue;
	}

    /**
     * Get the class that will be responsible for handling a particular component.
     *
     * @param componentType the component to look up
     * @return the class that will handle this component
     */
    public Class getHandlerType(Class componentType) {

        Class retVal = null;
        Object object = getHandler(componentType);
        if (object != null) {
            retVal = object.getClass();
        }
        return retVal;
	}

}

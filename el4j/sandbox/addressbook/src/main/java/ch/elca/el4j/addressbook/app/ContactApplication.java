/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ch.elca.el4j.addressbook.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationLauncher;

/**
 * This class is Starts the Master/Detail Prototype.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author David Stefan (DST)
 */
public class ContactApplication {

    /**
     * Logger.
     */
    private static Log s_logger = LogFactory.getLog(ContactApplication.class);
    
    /**
     * Spring config locations.
     */
    private static final String[] CONFIG_LOCATIONS = {
        "classpath*:scenarios/db/raw/*.xml",
        "classpath*:scenarios/dataaccess/hibernate/*.xml",
        "classpath*:scenarios/dataaccess/hibernate/contact/contact-hibernate-config.xml",
        "classpath*:optional/interception/transactionJava5Annotations.xml",
        "classpath*:ch/elca/el4j/addressbook/ctx/contact-application-context.xml"
    };
    /**
     * Main routine for the simple sample application.
     * 
     * @param args Parameters
     */
    public static void main(String[] args) {
        s_logger.info("Contact Application starting up");

        // In order to launch the platform, we have to construct an
        // application context that defines the beans (services) and
        // wiring. This is pretty much straight Spring.
        //
        // Part of this configuration will indicate the initial page to be
        // displayed.

        String rootContextDirectoryClassPath = "/ch/elca/el4j/addressbook/ctx";

        // The startup context defines elements that should be available
        // quickly such as a splash screen image.

        String startupContextPath = rootContextDirectoryClassPath
            + "/contact-startup-context.xml";

        // The ApplicationLauncher is responsible for loading the contexts,
        // presenting the splash screen, initializing the Application
        // singleton instance, creating the application window to display
        // the initial page.

        try {
            new ApplicationLauncher(startupContextPath, CONFIG_LOCATIONS);
        } catch (RuntimeException e) {
            s_logger.error("RuntimeException during startup", e);
        }
    }

}


package org.codehaus.cargo.container.tomcat;

import java.io.File;

import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.EmbeddedLocalContainer;
import org.codehaus.cargo.container.property.DatasourcePropertySet;
import org.codehaus.cargo.container.property.DataSource;
import org.codehaus.cargo.container.tomcat.internal.AbstractCatalinaStandaloneLocalConfiguration;

/**
 * Catalina standalone {@link org.codehaus.cargo.container.spi.configuration.ContainerConfiguration}
 * implementation.
 *
 * <p>
 * This code needs to work with both {@link Tomcat6xInstalledLocalContainer}
 * and {@link Tomcat6xEmbeddedLocalContainer}.
 *  
 * @version $Id$
 */
public class Tomcat6xStandaloneLocalConfiguration
    extends AbstractCatalinaStandaloneLocalConfiguration
{
    /**
     * {@inheritDoc}
     * @see AbstractCatalinaStandaloneLocalConfiguration#AbstractCatalinaStandaloneLocalConfiguration(String)
     */
    public Tomcat6xStandaloneLocalConfiguration(String dir)
    {
        super(dir);
    }

    /**
     * {@inheritDoc}
     * @see AbstractCatalinaStandaloneLocalConfiguration#setupManager(org.codehaus.cargo.container.LocalContainer)
     */
    protected void setupManager(LocalContainer container)
    {
        if (container instanceof EmbeddedLocalContainer)
        {
            // when running in the embedded mode, there's no need
            // of any manager application.
        }
        else
        {
            Copy copy = (Copy) getAntUtils().createAntTask("copy");

            FileSet fileSet = new FileSet();
            fileSet.setDir(new File(((InstalledLocalContainer) container).getHome()));
            fileSet.createInclude().setName("conf/Catalina/localhost/manager.xml");
            fileSet.createInclude().setName("server/lib/catalina.jar");
            fileSet.createInclude().setName("server/webapps/manager/**");
            copy.addFileset(fileSet);

            copy.setTodir(new File(getHome()));

            copy.execute();
        }
    }

    /**
     * @return the XML to be put into the server.xml file
     */
    protected String createDatasourceTokenValue()
    {
        getLogger().debug("Tomcat 6x createDatasourceTokenValue", this.getClass().getName());

        final String dataSourceProperty = getPropertyValue(DatasourcePropertySet.DATASOURCE);
        getLogger().debug("Datasource property value [" + dataSourceProperty + "]",
            this.getClass().getName());

        if (dataSourceProperty == null)
        {
            // have to return a non-empty string, as Ant's token stuff doesn't work otherwise
            return " ";
        }
        else
        {
            DataSource ds = new DataSource(dataSourceProperty);
            return
                "<Resource name='" + ds.getJndiLocation() + "'\n"
                    + "    auth='Container'\n"
                    + "    type='" + ds.getDataSourceType() + "'\n"
                    + "    username='" + ds.getUsername() + "'\n"
                    + "    password='" + ds.getPassword() + "'\n"
                    + "    driverClassName='" + ds.getDriverClass() + "'\n"
                    + "    url='" + ds.getUrl() + "'\n"
                    + "/>\n"
                    // As we are using a database - we will likely need a transaction factory too.
                    + "<Resource jotm.timeout='60' " 
                    + "    factory='org.objectweb.jotm.UserTransactionFactory' "
                    + "    name='UserTransaction' "
                    + "    type='javax.transaction.UserTransaction' "
                    + "    auth='Container'>\n"
                    + "</Resource>";
        }
    }

    /**
     * {@inheritDoc}
     * @see Object#toString()
     */
    public String toString()
    {
        return "Tomcat 6.x Standalone Configuration";
    }
}

/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.remoting.protocol.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

import ch.elca.el4j.services.remoting.RemotingServiceExporter;

/**
 * This class is a server-side bridge between an EJB and the Spring container.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Nicolas Schiper (NSC)
 * @author Andreas Bur (ABU)
 */
public class EjbServiceExporter implements Serializable {

    /**
     * The inner object.
     */
    private Object m_innerService;
    
    
    /**
     * The interface of the inner object.
     */
    private transient Class m_innerServiceInterface;
    
    /**
     * 
     * part of the hack.
     */
    private boolean m_implicitContextPassing;
    
    /**
     * Sets the interface of the service to export.
     * Typically populated via a bean reference.
     * The interface must be suitable for the particular service.
     * @param innerServiceInterface The interface of the inner object.
     */
    public void setInnerServiceInterface(Class innerServiceInterface) {
        this.m_innerServiceInterface = innerServiceInterface;
    }
    
    /**
     * Gets the interface of the service to export.
     * @return Returns the interface of the inner object.
     */
    public Class getInnerServiceInterface() {
        return this.m_innerServiceInterface;
    }

    /**
     * Sets the service to export.
     * Typically populated via a bean reference.
     * @param innerService The inner service object.
     */
    public void setInnerService(Object innerService) {
        this.m_innerService = innerService;
    }
    
    /**
     * Gets the service to export.
     * @return Returns the inner service object.
     */
    public Object getInnerService() {
        return this.m_innerService;
    }



    /**
     * Invokes method methodName on the inner object with <code>args</code> as
     * parameters.
     * 
     * @param methodName
     *      The name of the method to invoke.
     *      
     * @param argTypes
     *      The parameters' types.
     *      
     * @param args
     *      The parameter values used in the invocation.
     *      
     * @return Returns the result of the method invocation.
     * 
     * @throws Exception
     *      A potential exception thrown during the invocation.
     */
    public Object invoke(String methodName, Class[] argTypes, Object[] args)
        throws Exception {
        
        Method m;
        
        m = this.m_innerServiceInterface.getDeclaredMethod(
                methodName, argTypes);
        return m.invoke(this.m_innerService, args);
    }
    
    /**
     * The custom serializer method.
     * 
     * @param out
     *      The object output stream to write to.
     *      
     * @throws IOException
     *      If there is an error during writing.
     */
    private void writeObject(java.io.ObjectOutputStream out)
        throws IOException {
        
        out.defaultWriteObject();
        
        /* HACK Since the class is only available in the class loader that
         *      enriched the interface, we have to store a representation that
         *      can be reconstructed by an arbitrary class loader. Serializing
         *      the JavaClass generated during the enrichment process would do
         *      this job BUT there's a problem with the class loader that can't
         *      remember the enriched class and that also does not allow to
         *      define this class again.
         *      So, we deserialize the class' name an we put the class object in
         *      a singleton storage. During deserialization, we try first to
         *      query the class object from the interface storage and if we
         *      don't get it, it was never enriched and registered in the
         *      active class loader. Hence we enrich it again. 
         */
        out.writeUTF(m_innerServiceInterface.getName());
        InterfacesStorageSingleton.
            getInstance().putInterface(m_innerServiceInterface);
    }
    
    /**
     * The custom deserializer method.
     * 
     * @param in
     *      The object input stream to read serialized objects from.
     *      
     * @throws IOException
     *      If there's a problem while reading the input stream.
     *      
     * @throws ClassNotFoundException
     *      If the service type's class can not be loaded. 
     */
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        
        in.defaultReadObject();
        
        String className = in.readUTF();
        m_innerServiceInterface = InterfacesStorageSingleton.
                getInstance().getInterface(className);
        
        if (m_innerServiceInterface == null) {
            if (m_implicitContextPassing) {
                /* The service's interface has never been enriched within the
                 * current class loader. Doing it now allows to activate beans
                 * in other VMs (or to be more precise, in other class loaders)
                 * than they were passivated (e.g. this is required to pass a
                 * session to another node within a cluster).
                 * 
                 * HACK There is a wired problem with the class loader, that
                 *      can't remember the dynamically enriched class but that
                 *      also doesn't allow to define it again -- it throws a
                 *      LinkageError with 'duplicate class definition' message.
                 */
                m_innerServiceInterface = new RemotingServiceExporter().
                    getServiceInterfaceWithContext(m_innerService.getClass());
                
            } else {
                m_innerServiceInterface = m_innerService.getClass();
            }
        }
    }
}

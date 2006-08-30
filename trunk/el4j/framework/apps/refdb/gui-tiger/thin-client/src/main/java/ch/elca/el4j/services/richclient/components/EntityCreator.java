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
package ch.elca.el4j.services.richclient.components;

import org.springframework.util.StringUtils;

import ch.elca.el4j.services.gui.richclient.executors.AbstractBeanExecutor;
import ch.elca.el4j.util.codingsupport.annotations.Preliminary;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;

@Preliminary
public class EntityCreator<T> extends AbstractBeanExecutor {
    public SettableObservableValue<T> outlet 
        = new SettableObservableValue<T>(null);
    
    private Class<T> m_clazz;
    
    public EntityCreator(Class<T> clazz) {
        m_clazz = clazz;
    }
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        try {
            outlet.set(m_clazz.newInstance());
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getCommandId() {
        String cid = super.getCommandId();
        if (!StringUtils.hasText(cid)) {
            cid = "createCommand";
        }
        return cid;
    }

    @Override
    public void updateState() {
        setEnabled(true);
    }

    @Override
    public String getSchema() {
        return null;
    }
}
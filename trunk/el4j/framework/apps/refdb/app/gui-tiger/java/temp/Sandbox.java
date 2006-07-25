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
package temp;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.IntroductionInfoSupport;

public class Sandbox {
    static interface X {
        
    }
    
    public static class RealX implements X { } 
    
    static interface Y extends X {
        void foo();
    }
    
    static class Z extends IntroductionInfoSupport implements IntroductionInterceptor {
        Z() {
            publishedInterfaces.add(Y.class);
        }
        
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println(invocation);
            return null;
        }
    }
    
    static void proxy(X x) {
        ProxyFactory pf = new ProxyFactory();
        pf.setTarget(x);
        pf.setProxyTargetClass(true);
        pf.addAdvice(new Z());
        ((Y) pf.getProxy()).foo();
        
        
    }
    
    public static void main(String[] args) throws Exception {
        proxy(new RealX());
    }
}

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
package ch.elca.el4j.tests.services.debug;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import ch.elca.el4j.services.debug.BshCommandLineLauncher;
import ch.elca.el4j.services.debug.ResultHolder;
import ch.elca.el4j.services.debug.ShellExecutor;
import ch.elca.el4j.services.debug.ShellExecutorImpl;
import ch.elca.el4j.util.codingsupport.annotations.FindBugsSuppressWarnings;

/**
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 */
public class ShellExecutorTest {

	@Test
	public void testSimpleEval () {
		ShellExecutor se = new ShellExecutorImpl();
		
		se.eval("bsh.show=true;");
		Object result = se.eval("System.out.println(\"hello world\"); ii=10;");
		System.out.println("result:"+result);
	}
	
	@Test
	public void testBshCommandLauncher() {
		Properties bkup = System.getProperties();
		
		System.setProperty(BshCommandLineLauncher.BSH_LAUNCH_STRING, "uu=123;");
		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath*:mandatory/debugStartBshLauncher.xml");
		
		BshCommandLineLauncher b = (BshCommandLineLauncher) ac.getBean("bshLauncher");
		ResultHolder result = b.getShellExecutor().eval("i=uu;"); // returns the value of "uu" (=123)
		assertEquals(123, ((Number) result.getReturnValue()).longValue());
		
		System.setProperties(bkup); // restore original system properties
	}
	
	@Test
	@FindBugsSuppressWarnings(value = "DLS_DEAD_LOCAL_STORE",
							justification = "Test method doesn't care for values that are never read")
	public void testBshCommandLauncher2() {
		Properties bkup = System.getProperties();
		
		System.setProperty(BshCommandLineLauncher.BSH_LAUNCH_STRING, "aa=2;");
		ApplicationContext ac = new FileSystemXmlApplicationContext ("classpath*:mandatory/debugStartBshLauncher.xml");
		
		BshCommandLineLauncher b = (BshCommandLineLauncher) ac.getBean("bshLauncher");
		b.addScriptletClasspath("/test_scriptlets");
		ResultHolder result = b.getShellExecutor().eval("debug(); el4j_test(); threadInfo();"); // increment aa
		result = b.getShellExecutor().eval("i=aa;"); // returns the value of "vv" (=3)
		assertEquals(3, ((Number)result.getReturnValue()).longValue());
		
		System.setProperties(bkup); // restore original system properties
	}
	
}

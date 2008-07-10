package ch.elca.el4j.services.debug;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ShellExecutorTest {

	@Test
	public void testSimpleEval () {
		ShellExecutor se = new ShellExecutorImpl();
		
		se.eval("bsh.show=true;");
		Object result = se.eval("System.out.println(\"hallo welt\"); ii=10;");
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

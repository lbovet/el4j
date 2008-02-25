package ch.elca.el4j.tests.util.codingsupport;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

import ch.elca.el4j.tests.util.codingsupport.testclasses.A;
import ch.elca.el4j.tests.util.codingsupport.testclasses.B;
import ch.elca.el4j.tests.util.codingsupport.testclasses.DefaultPerson;
import ch.elca.el4j.util.codingsupport.AopHelper;

/**
 * This class shows some exceptions on stdout (for information purposes) 
 */
public class AopHelperTest extends TestCase {

	
	public void testIsProxied() {
		DefaultPerson p = new DefaultPerson();
		
		assertFalse(AopHelper.isProxied(p));
		
		p = AopHelper.addAdvice(p, new AMixin());
		
		assertTrue(AopHelper.isProxied(p));
	}

	public void testAdviceAddingOrder() {
		// for ordering tests we use this global list
		List<Integer> globalList = new ArrayList<Integer>();	
		
		DefaultPerson p = new DefaultPerson(); 
		p = AopHelper.addAdvice(p, 0, new AMixin(globalList), 
									  new CInterceptor(globalList), 
									  new BMixin(globalList));
		p.setAge(7);
		
		assertEquals(3,globalList.size());
		assertEquals(1,globalList.get(0).intValue());
		assertEquals(2,globalList.get(1).intValue());
		assertEquals(3,globalList.get(2).intValue());
		
		assertEquals(7, p.getAge());
	}
	
	// TODO add test that tests the MixinMixer with a spring config file/ auto proxy creator	
	
	
	public void testAopHelperAddAdvice() {
			List<Integer> globalList = new ArrayList<Integer>();			
		
			DefaultPerson p = new DefaultPerson();
			
			p = AopHelper.addAdvice(p, new AMixin(globalList));
			//System.out.println(DataDumper.dump(((IntroductionInfo)p).getInterfaces()));
			
			p = AopHelper.addAdvice(p, new BMixin(globalList));

			p = AopHelper.addAdvice(p, new CInterceptor(globalList));		
			
			p.setAge(11);
			
			assertEquals(3,globalList.size());
			assertEquals(2,globalList.get(0).intValue());
			assertEquals(3,globalList.get(1).intValue());
			assertEquals(1,globalList.get(2).intValue());			
			
			
			// print all interfaces p now implements
//			System.out.println(DataDumper.dump(p.getClass().getInterfaces() ));
//			System.out.println(DataDumper.dump(((IntroductionInfo)p).getInterfaces()));		
			

			System.out.println("B is: "+((B)p).getB());		
			System.out.println("A is: "+((A)p).getA());
			
			System.out.println("\nBefore calling getAge: ");
			System.out.println("Age is: "+p.getAge());		

			
			try {
				Thread.sleep(1000); // to sync with stderr (of exception output)
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
			System.out.println("\n\nRemoving all advice");
			
			p = (DefaultPerson)AopHelper.removeAllAdvice(p);
			System.out.println("Age is: "+p.getAge());
//			System.out.println(DataDumper.dump(p.getClass().getInterfaces() ));		
		}

		public void testLightAopHelperUsage() {
			DefaultPerson p = new DefaultPerson();
			
			p = AopHelper.addAdvice(p, new MethodInterceptor(){

				public Object invoke(MethodInvocation invocation) throws Throwable {
			    	System.out.println(" Invocation on my test object "+invocation.getMethod().getName());					
			    	Object returnValue = invocation.proceed(); 
			    	return returnValue;
				}
				
			});
			
			p.getAge();
			
		}
	
	}

// some helper test classes:


	@SuppressWarnings("serial")
	class CInterceptor implements MethodInterceptor{		
		
		public CInterceptor() { }
		
		List<Integer> globalList;		
		
		public CInterceptor(List<Integer> globalList) {
			this.globalList = globalList;
		}				
		
	    public Object invoke(MethodInvocation invocation) throws Throwable {
	    	if (globalList != null) { 
	    		globalList.add(2);
	    	}
	    	System.out.println(" In invoker C "+invocation.getMethod().getName());
	    	Object returnValue = invocation.proceed(); 
	    	return returnValue;        
	    }
	}


	@SuppressWarnings("serial")
	class AMixin extends DelegatingIntroductionInterceptor implements A{		
		int a = 42;
		
		public AMixin() { }
		
		List<Integer> globalList;		
		
		public AMixin(List<Integer> globalList) {
			this.globalList = globalList;
		}		
		
	    @Override
	    public Object invoke(MethodInvocation invocation) throws Throwable {
	    	if (globalList != null) {	    	
	    		globalList.add(1);
	    	}
	    	
	    	System.out.println(" In invoker of mixin A "+invocation.getMethod().getName());
	    	
	    	Exception e = new Exception();
	    	System.out.println (" Stack trace:");
	    	e.printStackTrace();
	    	System.out.println (" \n");
	    	
	    	return super.invoke(invocation);
	        
	    }

		public int getA() {
			return a;
		}
	}

	@SuppressWarnings("serial")
	class BMixin extends DelegatingIntroductionInterceptor implements B{		
		String b = "Carpe diem";
				
		public BMixin(){ }

		List<Integer> globalList;		
		
		public BMixin(List<Integer> globalList) {
			this.globalList = globalList;
		}
		
	    @Override
	    public Object invoke(MethodInvocation invocation) throws Throwable {
	    	if (globalList != null) {
	    		globalList.add(3);
	    	}
	    	System.out.println(" In invoker of mixin B "+invocation.getMethod().getName());
	    	return super.invoke(invocation);
	    }

		public String getB() {
			return b;
		}
	}	

For usage instruction, visit the corresponding wiki page at 
http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/JbossSeam

The template is referenced there as "Maven template"

Here's a short description of how to use it:
    * in this directory, execute    mvn clean install -DinitDB=true
    * cd war 
    * Run   mvn db:start jetty:run   to start the Derby network server and run jetty
    * You can access the application now at http://localhost:8080/

If you're developing based on the template, every time you change some file of 
the jar part, just re-install the artifact in your local repository by issuing 
=mvn clean install=. Jetty will detect this after some time (the check interval 
is set to 10 sec at the moment) and reload the changes. While working on the 
xhtml files in the war part, you can just save the files and reload your 
browser window. The made changes should be visible immediately. If you change 
the configuration files (e.g. the =web.xml= file) jetty will reload the 
application on its own, but sometimes not (e.g. when changing the file 
=pages.xml=). For such cases you can simply make some small change to =web.xml= 
and save it or issue =touch src/main/webapp/WEB-INF/web.xml=, to force jetty to 
reload.


* Know issues:
    
1) There is a (non fatal) issue during runtime. The following exception occurs. The
problem occurs only in deployments without an ejb container. It is described under 
http://jira.jboss.com/jira/browse/JBSEAM-1188 . It is fixed in seam 1.3.0 (not out
when this was written).


javax.naming.NameNotFoundException; remaining name 'EJBContext'
        at org.mortbay.naming.NamingContext.lookup(NamingContext.java:578)
        at org.mortbay.naming.NamingContext.lookup(NamingContext.java:665)
        at org.mortbay.naming.NamingContext.lookup(NamingContext.java:680)
        at org.mortbay.naming.java.javaRootURLContext.lookup(javaRootURLContext.java:112)
        at javax.naming.InitialContext.lookup(InitialContext.java:392)
        at org.jboss.seam.util.EJB.getEJBContext(EJB.java:115)
        at org.jboss.seam.util.Transactions.isEJBCTransactionActiveOrMarkedRollback(Transactions.java:111)
        at org.jboss.seam.util.Transactions.isTransactionActiveOrMarkedRollback(Transactions.java:54)
        at org.jboss.seam.web.ExceptionFilter.rollbackTransactionIfNecessary(ExceptionFilter.java:128)
        at org.jboss.seam.web.ExceptionFilter.doFilter(ExceptionFilter.java:63)
        at org.jboss.seam.web.SeamFilter$FilterChainImpl.doFilter(SeamFilter.java:49)
        at org.jboss.seam.debug.hot.HotDeployFilter.doFilter(HotDeployFilter.java:60)
        at org.jboss.seam.web.SeamFilter$FilterChainImpl.doFilter(SeamFilter.java:49)
        at org.jboss.seam.web.RedirectFilter.doFilter(RedirectFilter.java:45)
        at org.jboss.seam.web.SeamFilter$FilterChainImpl.doFilter(SeamFilter.java:49)
        at org.jboss.seam.web.MultipartFilter.doFilter(MultipartFilter.java:79)
        at org.jboss.seam.web.SeamFilter$FilterChainImpl.doFilter(SeamFilter.java:49)
        at org.jboss.seam.web.SeamFilter.doFilter(SeamFilter.java:84)
        at org.mortbay.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1041)
        at org.mortbay.jetty.servlet.ServletHandler.handle(ServletHandler.java:354)
        at org.mortbay.jetty.security.SecurityHandler.handle(SecurityHandler.java:179)
        at org.mortbay.jetty.servlet.SessionHandler.handle(SessionHandler.java:226)
        at org.mortbay.jetty.handler.ContextHandler.handle(ContextHandler.java:627)
        at org.mortbay.jetty.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:149)
        at org.mortbay.jetty.handler.HandlerCollection.handle(HandlerCollection.java:123)
        at org.mortbay.jetty.handler.HandlerWrapper.handle(HandlerWrapper.java:141)
        at org.mortbay.jetty.Server.handle(Server.java:269)
        at org.mortbay.jetty.HttpConnection.handleRequest(HttpConnection.java:430)
        at org.mortbay.jetty.HttpConnection$RequestHandler.headerComplete(HttpConnection.java:687)
        at org.mortbay.jetty.HttpParser.parseNext(HttpParser.java:492)
        at org.mortbay.jetty.HttpParser.parseAvailable(HttpParser.java:199)
        at org.mortbay.jetty.HttpConnection.handle(HttpConnection.java:339)
        at org.mortbay.jetty.nio.HttpChannelEndPoint.run(HttpChannelEndPoint.java:270)
        at org.mortbay.thread.BoundedThreadPool$PoolThread.run(BoundedThreadPool.java:475)
	
2) When shutting the application down, theres an EHCache exception 
(=org.hibernate.cache.CacheException: java.lang.IllegalStateException: The 
CacheManager is not alive=) this error is related to a bug in EL4J and seems 
not to have an impact on the correct behavior of the application.

3) Validations are not handled in the same way in all parts of the application.
   The main focus of the application template was on the seam<->maven integration,
   the coding patterns are not yet very cleaned up.


For usage instruction, visit the corresponding wiki page at 
http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/JbossSeam

The template is referenced there as "Maven template"

Here's a short description of how to use it:
    * in this directory, execute    mvn clean install -DinitDB=true
    * cd war 
    * Run   mvn db:start jetty:run   to start the Derby network server and run jetty
    * You can access the application now at http://localhost:8080/

Know issues:
    
There is a (not fatal) issue during runtime. The following exception occurs. The
problem is due to the deployment without an ejb container. It is dedescribed under 
http://jira.jboss.com/jira/browse/JBSEAM-1188 . Will be fixed in seam 1.3.0.


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

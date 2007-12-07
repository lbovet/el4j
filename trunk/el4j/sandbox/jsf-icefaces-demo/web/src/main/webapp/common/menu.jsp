<%@ include file="/common/taglibs.jsp"%>

<%-- this file determines which menus from menu-config.xml are displayed --%>

<menu:useMenuDisplayer name="Velocity" config="cssHorizontalMenu.vm" permissions="rolesAdapter">
<ul id="primary-nav" class="menuList">
    <li class="pad">&nbsp;</li>
    <%-- 
    <c:if test="${empty pageContext.request.remoteUser}">
    	<li>
    		<a href="<c:url value="/login.jsp"/>" class="current">
    		<fmt:message key="login.title"/></a>
    	</li>
    </c:if>
    --%>
    <menu:displayMenu name="MainMenu"/>
    <%--
    <menu:displayMenu name="UserMenu"/>
    <menu:displayMenu name="AdminMenu"/>
    --%>
    <menu:displayMenu name="AJAXMenu"/>
    <menu:displayMenu name="MiscMenu"/>
    <%-- 
    <menu:displayMenu name="Logout"/>
    --%>
</ul>
</menu:useMenuDisplayer>
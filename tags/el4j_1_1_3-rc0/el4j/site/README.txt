The site generation should be refactored and be reduced in its complexity:

* 	The configuration is scattered over the external pom.xml, the framework pom.xml, 
	the site pom.xml, the src site.xml and possibly the application poms if the 
	applications and demos should be added to the website again at a later point in time.
		
*	Some tasks are executed by using the exec plugin to call maven. There might be more reasonable ways
	(like using a shell script)
	
*	Move the directories skin/, src/ and site/ to maven/site/, if possible.
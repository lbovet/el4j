To be able to launch the XmlMerge web demo, you have to execute the 
following steps:

# Recursively compile all required targets files:
  ant jars.rec.module.module-xml_merge
  
# Deploy the demo application into Tomcat:
  ant deploy.war.module.eu.module-xml_merge.web

# Open http://localhost:8080/xmlmerge/demo in a browser.
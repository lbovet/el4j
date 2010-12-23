To be able to launch the XmlMerge web demo, you have to execute the 
following steps:

# compile & install all required target files:
mvn install
  
# start Tomcat (in console 1):
mvn cargo:start

# Deploy the demo application into Tomcat (in console 2):
cd web
mvn war:exploded cargo:deploy

# Open http://localhost:8080/xmlmerge/demo in a browser.
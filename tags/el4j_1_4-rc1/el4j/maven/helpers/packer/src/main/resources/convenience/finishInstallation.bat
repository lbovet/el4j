:: experimental .bat file for users that do not like CYGWIN

@echo This is a convenience setup. In case something does not work as expected,
@echo please check the documentation under http://wiki.elca.ch/twiki/el4j/bin/view/EL4J 
@echo and http://el4j.sourceforge.net/
@echo

mkdir "%HOME%\.m2"
xcopy  "etc\m2\settings.xml" "%HOME%\.m2" /p

@echo
@echo
@echo do the following steps if within elca:
@echo  uncomment the proxy tag in settings.xml
@echo


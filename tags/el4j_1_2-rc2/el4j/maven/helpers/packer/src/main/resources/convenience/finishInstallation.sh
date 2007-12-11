#!/bin/sh

echo
echo This is a convenience setup. In case something does not work as expected,
echo please check the documentation under http://wiki.elca.ch/twiki/el4j/bin/view/EL4J 
echo and http://el4j.sourceforge.net/
echo

case "`uname`" in
CYGWIN*)
	mkdir "$HOME/.m2"
	cp -i etc/m2/settings.xml "$HOME/.m2"
	;;
*)
	mkdir "$HOME/.m2"
	cp -i etc/m2/settingsLINUX.xml "$HOME/.m2/settings.xml"
	;;
esac

echo
echo
echo do the following steps if within elca:
echo  \* uncomment the proxy tag in settings.xml
echo


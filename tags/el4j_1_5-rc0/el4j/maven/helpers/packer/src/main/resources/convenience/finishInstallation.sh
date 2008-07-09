#!/bin/sh

echo
echo This is a convenience setup. In case something does not work as expected,
echo please check the documentation under http://wiki.elca.ch/twiki/el4j/bin/view/EL4J 
echo and http://el4j.sourceforge.net/
echo

if [ ! -e ~/.bash_profile ] ; then
	cat aliases.sh > ~/.bash_profile
	echo "Aliases installed."
else
	if [ $(grep "EL4J aliases" ~/.bash_profile | wc -l) -eq 0 ] ; then
		cat aliases.sh >> ~/.bash_profile
		echo "Aliases installed."
	else
		echo "Skipping .bash_profile because it already contains marker comment 'EL4J aliases'"
		echo "Aliases not installed."
	fi
fi

echo
echo "Have a look at http://wiki.elca.ch/twiki/el4j/bin/view/EL4J/CygwinFaqAndTips"
echo "for more useful aliases"
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
echo  \* uncomment the mirror tags in settings.xml
echo


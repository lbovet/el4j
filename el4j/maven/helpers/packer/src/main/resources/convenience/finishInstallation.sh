#!/bin/sh

echo
echo "This is a convenience setup. In case something does not work as expected,"
echo "please check the documentation under http://wiki.elca.ch/twiki/el4j/bin/view/EL4J"
echo "and http://el4j.sourceforge.net/"
echo

case "`uname`" in
CYGWIN*)
	bashFile=~/.bash_profile
	bashFileName="~/.bash_profile"
	;;
*)
	bashFile=~/.bashrc
	bashFileName="~/.bashrc"
	;;
esac

echo "Enter 'yes' to automatically append aliases to your $bashFileName, otherwise"
echo "please add the aliases stored in aliases.sh to your $bashFileName"
read autoInstall

if [ $autoInstall == "yes" ] ; then
	if [ ! -e "$bashFile" ] ; then
		cat aliases.sh > "$bashFile"
		echo "Aliases installed. Please check $bashFileName if everything is OK."
	else
		if [ $(grep "EL4J aliases" "$bashFile" | wc -l) -eq 0 ] ; then
			cat aliases.sh >> "$bashFile"
			echo "Aliases installed. Please check $bashFileName if everything is OK."
		else
			echo "Skipping .bashrc because it already contains marker comment 'EL4J aliases'"
			echo "Aliases not installed."
		fi
	fi
else
	echo "No aliases installed."
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
echo "do the following steps if within elca:"
echo " * uncomment the proxy tag in settings.xml"
echo " * uncomment the mirror tags in settings.xml"
echo


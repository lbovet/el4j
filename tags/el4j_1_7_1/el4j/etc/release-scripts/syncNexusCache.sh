#!/bin/bash

echo "Please enter your el4.elca-services.ch ftp username:"
read user
echo "Please enter your el4.elca-services.ch ftp password:"
read password

# path has to start with a '/' but must NOT end with a '/'
pathToSync="/ch"
tmpFile=updateNexus_files.tmp

echo "Synchronizing Nexus with el4.elca-services.ch..."

wget -r --level=0 -np --ftp-user=$user --ftp-password=$password --spider -o $tmpFile ftp://el4.elca-services.ch/htdocs/el4j/maven2repository$pathToSync

for i in $(cat $tmpFile | grep "\`" | grep -v ".listing" | cut -d"\`" -f 2 | sed "s#.*maven2repository/##" | sed "s#'##") ; do
	echo "http://leaffy.elca.ch:8082/nexus/content/repositories/el4-elca-services.release/$i"
	wget -O - "http://leaffy.elca.ch:8082/nexus/content/repositories/el4-elca-services.release/$i" > /dev/null
done
rm $tmpFile

rm -rf el4.elca-services.ch

echo "done."

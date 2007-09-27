echo "------------------------------------------------------"
echo " processing $1"

artifactId=`basename $1 | sed -e 's/.jar$//'`
echo " artifactid: $artifactId"

mvn install:install-file -Dfile=$1 -DgroupId=org.jboss.seam.atomic -Dversion=1.0 -Dpackaging=jar -DartifactId=$artifactId


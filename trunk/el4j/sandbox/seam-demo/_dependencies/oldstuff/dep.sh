artifactId=`basename $1 | sed -e 's/.jar$//'`

echo "<dependency>"
echo "	<groupId>cf.seam</groupId>"
echo "	<artifactId>$artifactId</artifactId>"
echo "	<version>1.0</version>"
echo "</dependency>"
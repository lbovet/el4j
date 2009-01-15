#!/bin/bash
# After updates, Hudson resets the folder structure in ~/.hudson/jobs/*/workspace:
# the symbolic links are deleted and standard folders are created.
# This script deletes this folders and recreates the symbolic links
# The script has to be executed in the folder ~/.hudson/jobs/

JOBS5="EL4J-EXTERNAL-CodeCheck EL4J-EXTERNAL_archetypes EL4J-EXTERNAL_nightly_build EL4J-EXTERNAL_svn_build EL4J-INTERNAL-CodeCheck EL4J-INTERNAL_nightly_build EL4J-INTERNAL_svn_build ELCA-ServiceIntegration_CodeCheck ELCA-ServiceIntegration_svn_build"
JOBS6="EL4J-EXTERNAL_archetypes_Java6 EL4J-EXTERNAL_nightly_build_Java6 EL4J-EXTERNAL_svn_build_Java6 EL4J-INTERNAL_nightly_build_Java6 EL4J-INTERNAL_svn_build_Java6"
for JOB in $JOBS5 ; do
	rm -rf $JOB/workspace/frequent
	ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
	echo "Done for Job $JOB"
done

for JOB in $JOBS6 ; do
	rm -rf $JOB/workspace/frequent
	ln -s ~/el4j/hudson_build_workspaces/frequent6/ $JOB/workspace/frequent
	echo "Done for Job $JOB"
done

echo "Completed, all folders should now be ok"

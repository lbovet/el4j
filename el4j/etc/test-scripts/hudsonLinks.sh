#!/bin/bash
# After updates, Hudson resets the folder structure in ~/.hudson/jobs/*/workspace:
# the symbolic links are deleted and standard folders are created.
# This script deletes this folders and recreates the symbolic links
# The script has to be executed in the folder ~/.hudson/jobs/

JOB=EL4J-EXTERNAL-CodeCheck
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-EXTERNAL_archetypes
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-EXTERNAL_archetypes_Java6
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent6/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-EXTERNAL_nightly_build
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-EXTERNAL_nightly_build_Java6
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent6/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-EXTERNAL_svn_build
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-EXTERNAL_svn_build_Java6
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent6/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-INTERNAL-CodeCheck
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-INTERNAL_nightly_build
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-INTERNAL_nightly_build_Java6
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent6/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-INTERNAL_svn_build
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=EL4J-INTERNAL_svn_build_Java6
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent6/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=ELCA-ServiceIntegration_CodeCheck
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

JOB=ELCA-ServiceIntegration_svn_build
rm -rf $JOB/workspace/frequent
ln -s ~/el4j/hudson_build_workspaces/frequent/ $JOB/workspace/frequent
echo "Done for Job $JOB"

echo "Completed, all folders should now be ok"

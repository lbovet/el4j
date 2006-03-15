#!/bin/bash

# $URL$
# $Revision$
# $Date$
# $Author$

# Upload generated website to SourceForge
rsync -rv --rsh=ssh /home/users2/tester/el4j/external/framework/dist/website/* swisswheel@shell.sf.net:/home/groups/e/el/el4j/htdocs/


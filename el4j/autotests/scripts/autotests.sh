#!/bin/bash

# $URL$
# $Revision$
# $Date$
# $Author$

DOTASK="${1}"
shift 1

# Setup the environment
./setup_environment.sh

#Execute tests with different profiles, generate reports and create a website
./website_framework.sh "${DOTASK}"
./website_framework.sh CleanOnly
echo ""
echo ""
./website_helloworld.sh "${DOTASK}"
./website_helloworld.sh CleanOnly

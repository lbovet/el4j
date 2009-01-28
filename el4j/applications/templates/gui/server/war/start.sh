#!/bin/bash
source mvn db:prepare cargo:undeploy cargo:deploy cargo:start

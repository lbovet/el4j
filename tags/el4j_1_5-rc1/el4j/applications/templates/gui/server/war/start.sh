#!/bin/bash
mvn db:prepare cargo:undeploy cargo:deploy cargo:start

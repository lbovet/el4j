#!/bin/bash

if [[ -z $1 ]]; then echo "Usage: $0 pkcs12-file"; exit; fi

openssl pkcs12 -info -nodes -in $1

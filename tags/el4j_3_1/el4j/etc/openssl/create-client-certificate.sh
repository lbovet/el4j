#!/bin/bash

if [[ -z $1 ]]; then echo "Usage: $0 client_name"; exit; fi


CA_DIR=ca-certificate
CA_DIR_SECRET=${CA_DIR}/secret
CA_FILENAME_PREFIX=srknis-
CA_PK_FILEPATH=${CA_DIR_SECRET}/${CA_FILENAME_PREFIX}ca-private-key.pem
CA_PEM_FILEPATH=${CA_DIR}/${CA_FILENAME_PREFIX}ca-certificate.pem
CA_CER_FILEPATH=${CA_DIR}/${CA_FILENAME_PREFIX}ca-certificate.cer


CLIENT_NAME=$1
CLIENT_DIR=client-certificates/${CLIENT_NAME}
CLIENT_DIR_SECRET=${CLIENT_DIR}/secret
CLIENT_FILENAME_PREFIX=srknis-${CLIENT_NAME}
CLIENT_PK_FILEPATH=${CLIENT_DIR_SECRET}/${CLIENT_FILENAME_PREFIX}-private-key.pem
CLIENT_SIGNREQ_FILEPATH=${CLIENT_DIR}/${CLIENT_FILENAME_PREFIX}-signing-request.pem
CLIENT_PEM_FILEPATH=${CLIENT_DIR}/${CLIENT_FILENAME_PREFIX}-certificate.pem
CLIENT_P12_FILEPATH=${CLIENT_DIR}/${CLIENT_FILENAME_PREFIX}-certificate.p12


echo "We (re-)create now the client certifcate for client with name '${CLIENT_NAME}' ..."
echo "Deleting previous directories '${CLIENT_DIR}' and '${CLIENT_DIR_SECRET}' and create them newly..."
rm -rf ${CLIENT_DIR_SECRET}
rm -rf ${CLIENT_DIR}
mkdir -p ${CLIENT_DIR}
mkdir -p ${CLIENT_DIR_SECRET}


echo "Generate client signing request..."
openssl req -new -nodes -out ${CLIENT_SIGNREQ_FILEPATH} -keyout ${CLIENT_PK_FILEPATH} -config openssl.conf

echo "Create the signed server certificate..."
openssl ca -extensions client_ext -extfile clientExtensions.txt -out ${CLIENT_PEM_FILEPATH} -config openssl.conf -infiles ${CLIENT_SIGNREQ_FILEPATH}

echo "Convert the certificate to PKCS12 format (*.p12)"
openssl pkcs12 -export -out ${CLIENT_P12_FILEPATH} -in ${CLIENT_PEM_FILEPATH} -inkey ${CLIENT_PK_FILEPATH}


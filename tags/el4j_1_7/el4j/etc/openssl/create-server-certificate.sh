#!/bin/bash

CA_DIR=ca-certificate
CA_DIR_SECRET=${CA_DIR}/secret
CA_FILENAME_PREFIX=srknis-
CA_PK_FILEPATH=${CA_DIR_SECRET}/${CA_FILENAME_PREFIX}ca-private-key.pem
CA_PEM_FILEPATH=${CA_DIR}/${CA_FILENAME_PREFIX}ca-certificate.pem
CA_CER_FILEPATH=${CA_DIR}/${CA_FILENAME_PREFIX}ca-certificate.cer

SERVER_DIR=server-certificate
SERVER_DIR_SECRET=${SERVER_DIR}/secret
SERVER_FILENAME_PREFIX=srknis-
SERVER_PK_FILEPATH=${SERVER_DIR_SECRET}/${SERVER_FILENAME_PREFIX}server-private-key.pem
SERVER_SIGNREQ_FILEPATH=${SERVER_DIR}/${SERVER_FILENAME_PREFIX}server-signing-request.pem
SERVER_PEM_FILEPATH=${SERVER_DIR}/${SERVER_FILENAME_PREFIX}server-certificate.pem
SERVER_P12_FILEPATH=${SERVER_DIR}/${SERVER_FILENAME_PREFIX}server-certificate.p12
SERVER_JKS_CA_KEYSTORE_FILEPATH=${SERVER_DIR}/${SERVER_FILENAME_PREFIX}server-ca-keystore.jks
SERVER_JKS_CA_KEYSTORE_PASSWORD=srk4bsd
SERVER_JKS_CA_KEYSTORE_ALIAS=srknis


echo "We (re-)create now the server certifcate and JKS ca keystore..."
echo "Deleting previous directories '${SERVER_DIR}' and '${SERVER_DIR_SECRET}' and create them newly..."
rm -rf ${SERVER_DIR_SECRET}
rm -rf ${SERVER_DIR}
mkdir -p ${SERVER_DIR}
mkdir -p ${SERVER_DIR_SECRET}


echo "Generate server signing request..."
openssl req -new -nodes -out ${SERVER_SIGNREQ_FILEPATH} -keyout ${SERVER_PK_FILEPATH} -config openssl.conf

echo "Create the signed server certificate..."
openssl ca -extensions server_ext -extfile serverExtensions.txt -out ${SERVER_PEM_FILEPATH} -config openssl.conf -infiles ${SERVER_SIGNREQ_FILEPATH}

echo "Convert the certificate to PKCS12 format (*.p12)"
openssl pkcs12 -export -out ${SERVER_P12_FILEPATH} -in ${SERVER_PEM_FILEPATH} -inkey ${SERVER_PK_FILEPATH}

echo "Create the JKS ca keystore and import the ca certificate (DER format)"
keytool -import -v -keystore ${SERVER_JKS_CA_KEYSTORE_FILEPATH} -storepass ${SERVER_JKS_CA_KEYSTORE_PASSWORD} -alias ${SERVER_JKS_CA_KEYSTORE_ALIAS} -file ${CA_CER_FILEPATH}

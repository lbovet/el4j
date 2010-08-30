#!/bin/bash

CA_DIR=ca-certificate
CA_DIR_SECRET=${CA_DIR}/secret
CA_FILENAME_PREFIX=srknis-
CA_PK_FILEPATH=${CA_DIR_SECRET}/${CA_FILENAME_PREFIX}ca-private-key.pem
CA_PEM_FILEPATH=${CA_DIR}/${CA_FILENAME_PREFIX}ca-certificate.pem
CA_CER_FILEPATH=${CA_DIR}/${CA_FILENAME_PREFIX}ca-certificate.cer


OPENSSL_TARGET_DIR=target
OPENSSL_SERIAL_FILE_FILEPATH=${OPENSSL_TARGET_DIR}/serial.conf
OPENSSL_CERTIFICATES_DB_FILEPATH=${OPENSSL_TARGET_DIR}/index.txt
OPENSSL_NEW_CERTIFICATES_DIR=${OPENSSL_TARGET_DIR}/new-certificates



echo "We (re-)create now the ca certifcate..."

echo "Deleting previous openssl directories '${OPENSSL_TARGET_DIR}' and '${OPENSSL_NEW_CERTIFICATES_DIR}' and create them newly including the needed files..."
rm -rf ${OPENSSL_NEW_CERTIFICATES_DIR}
rm -rf ${OPENSSL_TARGET_DIR}
mkdir -p ${OPENSSL_TARGET_DIR}
mkdir -p ${OPENSSL_NEW_CERTIFICATES_DIR}
echo "1000" > ${OPENSSL_SERIAL_FILE_FILEPATH}
touch ${OPENSSL_CERTIFICATES_DB_FILEPATH}


echo "Deleting previous directories '${CA_DIR}' and '${CA_DIR_SECRET}' and create them newly..."
rm -rf ${CA_DIR_SECRET}
rm -rf ${CA_DIR}
mkdir -p ${CA_DIR}
mkdir -p ${CA_DIR_SECRET}


echo "Generate the ca private key and certifcate"
openssl req -new -x509 -extensions v3_ca -keyout ${CA_PK_FILEPATH} -out ${CA_PEM_FILEPATH} -days 10000 -config openssl.conf

echo "Convert the ca certifcate to DER encoded format (*.cer)"
openssl x509 -in ${CA_PEM_FILEPATH} -outform DER -out ${CA_CER_FILEPATH}


@echo off

C:
chdir C:\cygwin\bin

set PROJECT_NAME=EL4J
set PROJECT_DIRECTORY=/cygdrive/d/Projects/%PROJECT_NAME%
set PROJECT_INIT_SCRIPT=%PROJECT_DIRECTORY%/external/etc/cygwin/init-script.sh

bash --login -i

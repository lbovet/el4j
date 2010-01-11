@echo off

C:
chdir C:\cygwin\bin

set PROJECT_NAME=yourProject
set PROJECT_DIRECTORY=D:\Projects\%PROJECT_NAME%
set PROJECT_INIT_SCRIPT=%PROJECT_DIRECTORY%\etc\cygwin\init-script.sh

bash --login -i

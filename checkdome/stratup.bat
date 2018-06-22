@echo off
rem 
set "CURRENT_DIR=%cd%"
echo "set JAVA_HOME=%CURRENT_DIR%\jdk1.8.0_171_i586"
set "JAVA_HOME=%CURRENT_DIR%\jdk1.8.0_171_i586"
echo "set CLASSPATH"
set "CLASSPATH=%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar"
echo "set Path"
set "Path=.;%JAVA_HOME%\bin;%JAVA_HOME%\jar\bin;"
rem
java -jar aa.jar
echo;  
pause 
@echo off
echo build start
call mvn -DskipTests=true clean package
echo copy-dependencies to target\lib
call mvn dependency:copy-dependencies -DoutputDirectory=target\lib
cd target
mkdir cute-generate
cp -rf .\lib cute-generate
::set jarName=ls -S | grep cute-code-generate -m 1
::ls -S | grep cute-code-generate -m 1
::echo %jarName%
::echo "jarName = " + %jarName%
::cp -rf %jarName% .\cute-generate
cd ../
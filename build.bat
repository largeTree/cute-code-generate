@echo off
mvn -DskipTests=true clean package
mvn dependency:copy-dependencies -DoutputDirectory=target\lib
cd target
mkdir cute-generate
cp .\lib cute-generate
#!/bin/bash

#rm -rf $HOME/deploy
git clone https://${GH_TOKEN}@github.com/${GH_ACCOUNT}/${GH_REPOSITORY} $HOME/deploy

#'convert' the repository to look like a local repository
find $HOME/deploy -name 'maven-metadata.xml' -exec sh -c 'mv -v "$0" "${0%.xml}-local.xml"' {} \;

#use maven to package and install the artifact in the repository 
mvn package
mkdir target/sources
mv target/*-sources.jar target/sources/
mvn install:install-file -DpomFile=pom.xml -DcreateChecksum=true -Dfile=$(ls target/*.jar) -DlocalRepositoryPath=$HOME/deploy
mvn install:install-file -DpomFile=pom.xml -DcreateChecksum=true -Dfile=$(ls target/sources/*-sources.jar) -DlocalRepositoryPath=$HOME/deploy -Dclassifier=sources

#'convert' the repository back to be a maven repository
find $HOME/deploy -name 'maven-metadata-local.xml' -exec sh -c 'mv -v "$0" "${0%-local.xml}.xml"' {} \;

#commit and push the changes to the deploy branch
cd $HOME/deploy

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"

git add -A .
git status
git commit -m "travis"
git push -f origin master

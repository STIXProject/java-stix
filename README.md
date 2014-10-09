# JAVA STIX 

JAXB2 generated Java bindindgs for  Structured Threat Information eXpression 
(STIX) .

## Cloning the repository

A `git clone` command will not retrieve schemas project automatically,  First
clone the project, then enter the project and run these additonal git commands
on the command line to retrieve the schema.

    git submodule init
    git submodule update

Then

    cd src/main/resources/schemas
    git submodule init
    git submodule update

Any time you see that the schemas project has been modified (when merging or 
pulling updates) you will need to run 

    git submodule update

again to update the schemas themselves.

## Install gradle

Visit 

http://www.gradle.org/installation

to install gradle, or if you use brew:

    brew install gradle

## To generate the JAXB2 XML bindings

Change directories to the project, and run:

    gradle -q generate

############################################################
# A Dockerfile used to create a java-stix build container
# based on Ubunu.
#
# Copyright (c) 2015, The MITRE Corporation. All rights reserved.
# See LICENSE for complete terms.
#
# @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
#
# WHAT TO DO:
#
# If you have Docker installed, from the root of the project run
# the following to create a container image for this Dockerfile via:
#
# docker build -t stix/java-stix .
#
# Then create a container using the image you just created via:
#
# docker run -t -i stix/java-stix /bin/bash
#
# To retreive the jar archives from the running docker container use following
# from the command-line of your docker host, not the container:
#
# docker cp <container id>:/java-stix/build/libs/stix-1.2.0.2-SNAPSHOT-javadoc.jar .
# docker cp <container id>:/java-stix/build/libs/stix-1.2.0.2-SNAPSHOT-sources.jar .
# docker cp <container id>:/java-stix/build/libs/stix-1.2.0.2-SNAPSHOT.jar .
#
# If the containder ID is not obvious, but you can also retrieve it via:
#
# docker ps
#
# An example of retrieving the snapshot jar would be the following:
#
# ➜  /tmp  docker cp 83ad9afb6096:/java-stix/build/libs/stix-1.2.0.2-SNAPSHOT.jar .
#
#
############################################################

# Set base image
FROM  dockerbase/java7

# File Maintainer
MAINTAINER STIX Project, The MITRE Corporation

# Update the sources list
RUN apt-get -y update

# Clone java-stix repo at the current branch into the container
COPY . java-stix

# Open the java-stix project
WORKDIR java-stix

# Build unsigned jar archives in debug to /java-stix/build/libs
RUN ./gradlew -x signArchives -d

# Clean up APT when done.
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

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
# docker build -t java_stix_img . 
#
# Then create a container using the image you just created via:
#
# docker run -t -i java_stix_img_v1_1_1_1 /bin/bash
#
# To retreive the jar archives from the running docker container use following 
# from the command-line of your docker host, not the container:
#
# docker cp <container id>:/java-stix/build/libs/stix-1.1.1.1-SNAPSHOT-javadoc.jar .
# docker cp <container id>:/java-stix/build/libs/stix-1.1.1.1-SNAPSHOT-sources.jar .
# docker cp <container id>:/java-stix/build/libs/stix-1.1.1.1-SNAPSHOT.jar .
#
# If the containder ID is not obvious, but you can also retrieve it via:
#
# docker ps
#
# An example of retrieving the snapshot jar would be the following:
#
# ➜  /tmp  docker cp 83ad9afb6096:/java-stix/build/libs/stix-1.1.1.1-SNAPSHOT.jar . 
#
#
############################################################

# Set base image
FROM ubuntu:15.04

# File Maintainer
MAINTAINER Michael Joseph Walsh

# Update the sources list
RUN apt-get -y update

# Install cmd-line dev toolchain
RUN apt-get install -y tar git curl nano wget dialog net-tools build-essential software-properties-common

# To install the default OpenJDK environment
RUN add-apt-repository -y ppa:openjdk-r/ppa
RUN apt-get -y update 
RUN apt-get -y install openjdk-8-jdk

# To install the OpenJDK 7, comment out the above and uncomment the following.
#RUN apt-get install -y openjdk-7-jdk

# Optionally to install the Oracle JDK, comment out the above, uncomment the 
# the next 3 lines, and then uncommment the preferred JDK version.
#RUN apt-get -y install python-software-properties
#RUN add-apt-repository -y ppa:webupd8team/java
#RUN apt-get -y update

#RUN apt-get install oracle-java7-installer
#RUN apt-get install oracle-java8-installer

# Clone java-stix repo at the current branch into the container
COPY . java-stix

# Open the java-stix project
WORKDIR java-stix

# Build unsigned jar archives in debug to /java-stix/build/libs
RUN ./gradlew -x signArchives -d

# Clean up APT when done.
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

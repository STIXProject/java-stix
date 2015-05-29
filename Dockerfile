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
# docker run -t -i java_stix_img /bin/bash
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
FROM ubuntu

# File Maintainer
MAINTAINER Michael Joseph Walsh

# Add the application resources URL
#RUN echo "deb http://archive.ubuntu.com/ubuntu/ $(lsb_release -sc) main universe" >> /etc/apt/sources.list

# Update the sources list
RUN apt-get update

# Install cmd-line dev toolchain
RUN apt-get install -y tar git curl nano wget dialog net-tools build-essential

# To install the OpenJDK environment
#RUN apt-get install default-jdk

# To install the OpenJDK 7, comment out the above and uncomment the following.
RUN apt-get install -y openjdk-7-jdk

# Optionally to install the Oracle JDK, comment out the above, uncomment the 
# the next 3 lines, and then uncommment the preferred JDK version.
#RUN apt-get install python-software-properties
#RUN add-apt-repository ppa:webupd8team/java
#RUN apt-get update

#RUN apt-get install oracle-java7-installer
#RUN apt-get install oracle-java8-installer

# Clone the java-stix repo
RUN git clone https://github.com/STIXProject/java-stix.git

# Open the java-stix project
WORKDIR java-stix

# Checkout the v1.1.1.1 branch
RUN git checkout -b v1.1.1.1 origin/v1.1.1.1

# Build unsigned jar archives in debug to /java-stix/build/libs
RUN ./gradlew -x signArchives -d 

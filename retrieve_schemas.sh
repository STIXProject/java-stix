#!/bin/sh

echo "Retrieving STIX schemas..."
git submodule init
git submodule update --force
cd src/main/resources/schemas

echo "Retrieving CybOX schemas..."
git submodule init
git submodule update --force
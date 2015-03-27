#!/bin/sh

echo "    Retrieving STIX schemas..."
git submodule init
git submodule update --force
cd src/main/resources/schemas/v1.1.1
git checkout tags/v1.1.1

echo "    Retrieving CybOX schemas..."
git submodule init
git submodule update --force
git checkout 3442ebe50385d3bd0b3305952b90d296e0a1242c


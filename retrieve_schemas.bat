echo     Retrieving STIX schemas...
git submodule init
git submodule update --force
cd src\main\resources\schemas\v1.2.0
git checkout tags/v1.2.0

echo     Retrieving CybOX schemas...
git submodule init
git submodule update --force
cd cybox
git checkout 97beb32c376a9223e91b52cb3e4c8d2af6baf786
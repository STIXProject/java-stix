#!/bin/sh

mvn install:install-file -Dfile=org.eclipse.core.contenttype_3.4.200.v20140207-1251.jar -DgroupId=org.eclipse.core -DartifactId=org.eclipse.core.contenttype -Dversion=3.4.200.v20140207-1251 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.jobs_3.6.0.v20140424-0053.jar -DgroupId=org.eclipse.core -DartifactId=org.eclipse.core.jobs -Dversion=3.6.0.v20140424-0053 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.resources_3.9.0.v20140514-1307.jar -DgroupId=org.eclipse.core -DartifactId=org.eclipse.core.resources -Dversion=3.9.0.v20140514-1307 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.core.runtime_3.10.0.v20140318-2214.jar -DgroupId=org.eclipse.core -DartifactId=org.eclipse.core.runtime -Dversion=3.10.0.v20140318-2214 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.equinox.common_3.6.200.v20130402-1505.jar -DgroupId=org.eclipse.equinox -DartifactId=org.eclipse.equinox.common -Dversion=3.6.200.v20130402-1505 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.equinox.preferences_3.5.200.v20140224-1527.jar -DgroupId=org.eclipse.equinox -DartifactId=org.eclipse.equinox.preferences -Dversion=3.5.200.v20140224-1527 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.equinox.registry_3.5.400.v20140428-1507.jar -DgroupId=org.eclipse.equinox -DartifactId=org.eclipse.equinox.registry -Dversion=3.5.400.v20140428-1507 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.jdt.core_3.10.0.v20140604-1726.jar -DgroupId=org.eclipse.jdt -DartifactId=org.eclipse.jdt.core -Dversion=3.10.0.v20140604-1726 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.osgi_3.10.0.v20140606-1445.jar -DgroupId=org.eclipse -DartifactId=org.eclipse.osgi -Dversion=3.10.0.v20140606-1445 -Dpackaging=jar
mvn install:install-file -Dfile=org.eclipse.text_3.5.300.v20130515-1451.jar -DgroupId=org.eclipse -DartifactId=org.eclipse.text -Dversion=3.5.300.v20130515-1451 -Dpackaging=jar

# java-stix 

Java bindings for STIX, the Structured Threat Information eXpression 
language used for representing structured cyber threat information.

This effort is being developed under the official [STIXProject](http://stixproject.github.io/).

For more information, see [http://stix.mitre.org/](http://stix.mitre.org/).

## Overview

A primary goal of java-stix is to remain faithful to both the STIX
specifications and to customary Java practices. java-stix is designed
to be intuitive both to Java developers and JAXB XML developers, but
it is not a one-for-one replacement for the [python-stix](https://github.com/STIXProject/python-stix) API.

## Versioning

Releases of java-stix will comply with the Semantic Versioning
specification at [http://semver.org/](http://semver.org/). Java-stix
is currently under active development; see TODO.txt for a tentative
roadmap.  Releases will be announced on the [STIX discussion list](http://stix.mitre.org/community/registration.html).

## Snapshots

Snapshots are being pushed to 

https://oss.sonatype.org/content/repositories/snapshots/org/mitre/stix/ 

So, users can simply retrieve java-stix directly via the Central Repository.

     <repositories>
       <repository>
         <id>snapshots-repo</id>
         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
         <releases><enabled>false</enabled></releases>
         <snapshots><enabled>true</enabled></snapshots>
       </repository>
     </repositories>
	
	<dependencies>
		<dependency>
			<groupId>org.mitre</groupId>
			<artifactId>stix</artifactId>
			<version>1.1.1-SNAPSHOT</version>
		</dependency>
	</dependencies>

# Building

## Clone the repository

git clone https://github.com/nemonik/java_stix.git

A `git clone` command will not retrieve schemas project
automatically, but the Gradle Buildscript will attempt to
retrieve them on execution.

## Retrieve the schemas

The Gradle buildscript will attempt to retreive and patch the 
schemas for you, but if it cannot for some reason you will need
to retrieve them yourself.

The Buildscript executes either the retrieve_schemas.sh shell script
or the retreieve_schemas.bat batch script as appopriate for your 
platform.  You can find these in the root of the project, and try
executing them yourself or you can follow the steps enumerated in
the next section for retrieving the schemas.

Afterward, the cybOX schemas are patched for the flaw described in (cybOX Schema issue #340)[https://github.com/CybOXProject/schemas/issues/340].
Until the issue is resolved please be aware of the patch.  The
(patch)[https://raw.githubusercontent.com/nemonik/java_stix/master/cybox_object_archive_file_object.patch] is found at the root of the project.

### The STIX schemas

To manually retrieve the schemas, enter the project and run 
these  additonal git commands on the command line:

    git submodule init
    git submodule update --force
    cd src/main/resources/schemas/v1.1.1
    git checkout tags/v1.1.1
    
Your not done.  You'll also need to retrieve the CybOX schemas.

### The CybOX schemas

While in the project:

    git submodule init
    git submodule update --force
    git checkout 3442ebe50385d3bd0b3305952b90d296e0a1242c

When the Buildscript first runs it will also patch the CybOX schemas 
so  that the JAXB2 Simple Document Object Model can be generated.
Once patched, it will remained patched locally.  If the Buildscript
fails to retrieve the schemas, you will need to patch the schemas
by hand from the command-line.  The patch file, `cybox_object_archive_object_patch` 
is in  the root of the project.  NetBeans can also apply patches via
`Team` -> `Patches` -> `Apply Diff Patch`. 

## Creating the JAXB2 Document Model

### You need Gradle installed

This project uses a Gradle Buildscript.  You will need to install
Gradle.

I use typially use the Gradle command-line interface and this can be
installed a number of ways.  
 
If you're using OS X and using brew simply install gradle via

    brew install gradle

Otherwise, install the Gradle command-line binaries from
[gradle.org](http://www.gradle.org) following their instructions.

If you're using the Eclipse IDE consider installing the latest
[Gradle IDE Pack](http://marketplace.eclipse.org/content/gradle-ide-pack) 
or use [Nodeclipse/Enide Gradle for Eclipse](http://marketplace.eclipse.org/content/gradle). 
Gradle Eclipse integration is somewhat emergent.  I'd advise using the Gradle command-line.

### Do these prior to building

And for now add the folling lines to your ~/gradle/gradle.properties file

    signingKeyId=filler
    signingPassword=filler
    secretKeyFile=filler
    sonotypeJiraId=filler
    sonotypeJiraPassword=filler
    
Otherwise, gradle will fail not finding "sonotypeJiraId" or some such.  I need to set all
these properties to archivePush java-stix to Maven Central.

### Building via Gradle buildscript

Change directories into the project and enter on the command-line:

    gradle

Success will look like this on UNIX:

    ➜  java_stix git:(master) ✗ gradle
    To honour the JVM settings for this build a new JVM will be forked. Please consider using the daemon: http://gradle.org/docs/2.2/userguide/gradle_daemon.html.
    :createPrefixNamespaceBindings
    :cleanGenerate
    :retrieveSchemas
    Retrieving STIX schemas...
    Submodule path 'src/main/resources/schemas': checked out 'fd6ce20a62e52a7ddeb5ab0fb0e5b760778c443e'
    Retrieving CybOX schemas...
    Submodule 'cybox' (https://github.com/CybOXProject/schemas.git) registered for path 'cybox'
    Submodule path 'cybox': checked out '97beb32c376a9223e91b52cb3e4c8d2af6baf786'
    
    :generateJAXB
    :generatedSourceTransformation
    :compileJava
    Note: Some input files use unchecked or unsafe operations.
    Note: Recompile with -Xlint:unchecked for details.
    :processResources
    :classes
    :jar
    
	BUILD SUCCESSFUL
	
	Total time: 2 mins 31.743 secs

If the build goes well you will find the JAXB Document Model in jar
at

	build/libs/java-stix-${version}.jar

where `${version}` is replaced with the `version` number in the Gradle Buildscript.

A build will go like so on Windows:

	$ gradle
	To honour the JVM settings for this build a new JVM will be forked. Please consider using the daemon: http://gradle.org/docs/2.2/userguide/gradle_daemon.html.
	:createPrefixNamespaceBindings
	:cleanGenerate
	:retrieveSchemas
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix>echo Retrieving STIX schemas... 
	Retrieving STIX schemas...
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix>git submodule init 
	Submodule 'src/main/resources/schemas' (https://github.com/STIXProject/schemas.git) registered for path 'src/main/resources/schemas'
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix>git submodule update --force 
	Submodule path 'src/main/resources/schemas': checked out 'fd6ce20a62e52a7ddeb5ab0fb0e5b760778c443e'
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix>cd src\main\resources\schemas 
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix\src\main\resources\schemas>echo Retrieving CybOX schemas... 
	Retrieving CybOX schemas...
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix\src\main\resources\schemas>git submodule init 
	Submodule 'cybox' (https://github.com/CybOXProject/schemas.git) registered for path 'cybox'
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix\src\main\resources\schemas>git submodule update --force 
	Submodule path 'cybox': checked out '97beb32c376a9223e91b52cb3e4c8d2af6baf786'
	
	C:\cygwin64\home\nemonik\Development\workplace\java_stix\src\main\resources\schemas>exit 0 
	
	:generateJAXB
	:generatedSourceTransformation
	:compileJava
	:processResources
	:classes
	:jar
	
	BUILD SUCCESSFUL
	
	Total time: 3 mins 21.132 secs

If the build goes well you will find the JAXB Document Model in jar
at

	build\libs\java-stix-${version}.jar
	
where `${version}` is replaced with the `version` number in the Gradle Buildscript.

## Trouble building?

`You are less likely to see the following happen now that the Gradle 
Buildscript will attempt to retreive the scheas, and if not will
continue.`

If while building you get this error:

    FAILURE: Build failed with an exception.
    
    * Where:
    Build file '/Users/nemonik/Development/workspace/STIXProject/java_stix/build.gradle' line: 52
    
    * What went wrong:
    Execution failed for task ':generate'.
    > grammar is not specified

    * Try:
    Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

 You likely didn't retrieve the schema decribed in the section above.

Or if you get this error while building:

    [ant:xjc] [ERROR] src-resolve: Cannot resolve the name 'cyboxCommon:ToolInformationType' to a(n) 'type definition' component.
    [ant:xjc]   line 907 of file:/Users/nemonik/Development/workspace/STIXProject/java_stix/src/main/resources/schemas/stix_common.xsd
    [ant:xjc] 
    [ant:xjc] [ERROR] src-resolve: Cannot resolve the name 'cybox:ObservableType' to a(n) 'type definition' component.
    [ant:xjc]   line 439 of file:/Users/nemonik/Development/workspace/STIXProject/java_stix/src/main/resources/schemas/stix_common.xsd
    [ant:xjc] 
    [ant:xjc] [ERROR] src-resolve: Cannot resolve the name 'SystemObj:SystemObjectType' to a(n) 'type definition' component.
    [ant:xjc]   line 149 of file:/Users/nemonik/Development/workspace/STIXProject/java_stix/src/main/resources/schemas/external/maec_4.1/maec_package_schema.xsd
    -- snip --

then you likely missed the step above where you must retrieved the
CybOX schemas.

### Importing the project into Eclipse

As Eclipse's various Gradle plugins have varying degrees of usefulness,
I usually import the project as a `Java Project` and not a `Gradle Project`.

You can still use `Gradle EnIDE` plugin to run the buildscript 
from the IDE, but you'll need to configure your `Gradle Home to use` 
via the Eclipse IDE preference panel.

After running the Gradle buildscript, if you are using the Eclipse IDE
you may want to run the following from the command-line to set up the
Ecipse environment:

	gradle clean cleanEclipse eclipse

It entirely depends on how smart yoour Eclipse Gradle plugin is and how
you imported the project.  The above command will change your prject to
essentially a Java Project. If you run the command, then right-click on
the Eclipse project and select "Refresh" to bring in the dependencies and 
source of JAXB Document Model. By default the generated source files are 
not editable to remmind you that changes to these files is usually a bad 
idea.

### Opening the project into NetBeans

I've tested the project with NetBeans 8.0.1 and it works great with the
[Gradle JavaEE Support plugin](http://plugins.netbeans.org/plugin/55529/gradle-javaee-support).
With the plugin installed, open the project and right-click on the project 
root and select `build` from the `task` panel.


# java-stix 

This project is the Java bindings for STIX, the Structured Threat Information 
eXpression language used for representing structured cyber threat information.

java-stix is being developed under the official [STIXProject][stix project].

For more information, see [http://stix.mitre.org/][stix].

## <a name="overview"></a>Overview

A primary goal of java-stix is to remain faithful to both the STIX
specifications and customary Java practices. java-stix is designed
to be intuitive both to Java developers and JAXB XML developers,
provide convenience on the JAXB model, but it is not a one-for-one
replacement for the [python-stix][python stix] API.

## <a name="versioning"></a>Versioning

Releases of java-stix will comply with the Semantic Versioning
specification at [http://semver.org/][semver]. Java-stix
is currently under active development; see TODO.txt for a tentative
roadmap.  Releases will be announced on the [STIX discussion list][list].

## <a name="snapshots"></a>Snapshots

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

At a later date, release artifacts will appear in the Maven Central Repository.

# <a name="building"></a>Building

## <a name="cloning"></a>Clone the repository

git clone https://github.com/nemonik/java_stix.git

A `git clone` command will not retrieve schemas project
automatically, but the Gradle build script will attempt to
retrieve them on execution.

## <a name="retrieving_schemas"></a>Retrieve the schemas

The Gradle build script will attempt to retreive and patch the 
schemas for you, but if it cannot for some reason you will need
to retrieve them yourself.

Gradle will execute either the retrieve_schemas.sh shell script
or the retreieve_schemas.bat batch script as appopriate for your 
platform.  You can find these in the root of the project, and try
executing them yourself or you can follow the steps enumerated in
the next section for retrieving the schemas.

Afterward, the cybOX schemas are patched for the flaw described in [cybOX Schema issue #340][issue 340].
Until the issue is resolved please be aware of the patch.  The
[patch][patch 340] is found at the root of the project.

## <a name="re_retrieving_schemas"></a>Re-retrieve the schemas

If you should need to re-retrieve the schemas have the Gradle
build script do it for you:

    gradle cleanSchema

Then [build](#building_via_gradle) as normal.

### <a name="stix_schemas"></a>The STIX schemas

To manually retrieve the schemas, enter the project and run 
these  additonal git commands on the command line:

    git submodule init
    git submodule update --force
    cd src/main/resources/schemas/v1.1.1
    git checkout tags/v1.1.1
    
Your not done.  You'll also need to retrieve the CybOX schemas.

### <a name="cybox_schemas"></a>The CybOX schemas

While in the project:

    git submodule init
    git submodule update --force
    git checkout 3442ebe50385d3bd0b3305952b90d296e0a1242c

When Gradle build script first runs it will also patch the CybOX schemas 
so that the JAXB2 Simple Document Object Model can be generated.
Once patched, it will remained patched locally.  If Gradle
fails to retrieve the schemas, you will need to patch the schemas
by hand from the command-line.  The patch file, `cybox_object_archive_object_patch` 
is in  the root of the project.  NetBeans can also apply patches via
`Team` -> `Patches` -> `Apply Diff Patch`. 

## <a name="creating_document_object_model"></a>Creating the JAXB2 Document Model

### <a name="need_gradle_installed"></a>You need Gradle installed

This project uses a Gradle build script.  You will need to install
Gradle.

I use typially use the Gradle command-line interface and this can be
installed a number of ways.  
 
If you're using OS X and using brew simply install gradle via

    brew install gradle

Otherwise, install the Gradle command-line binaries from
[gradle.org][gradle] following their instructions.

If you're using the Eclipse IDE consider installing the latest
[Gradle IDE Pack][gradle ide pack] 
or use [Nodeclipse/Enide Gradle for Eclipse][nodeclipse]. 
Gradle Eclipse integration is somewhat emergent.  I'd advise using the Gradle command-line.

### <a name="building_via_gradle"></a>Building via Gradle build script

It is really unnecesary to build by hand, if your intention is to use the java-stix in your
project as [snapshot](#importing_into_eclipse) exist in the Maven Central Snapshot repository,
and soon releases will exist too. 

If you want to build, you will need to have earlier [installed Gradle](#need_gradle_installed). 

First change directories into the project and enter on the command-line:

    gradle

as `build` is the default Gradle task.

If you have trouble builidng via Gradle, see the [Trouble Building](trouble_building) section below.

Success will look like this on UNIX:

	➜  java_stix git:(master) gradle
	To honour the JVM settings for this build a new JVM will be forked. Please consider using the daemon: http://gradle.org/docs/2.3/userguide/gradle_daemon.html.
	:buildSrc:clean UP-TO-DATE
	:buildSrc:compileJava UP-TO-DATE
	:buildSrc:compileGroovy
	:buildSrc:processResources UP-TO-DATE
	:buildSrc:classes
	:buildSrc:jar
	:buildSrc:assemble
	:buildSrc:compileTestJava UP-TO-DATE
	:buildSrc:compileTestGroovy UP-TO-DATE
	:buildSrc:processTestResources UP-TO-DATE
	:buildSrc:testClasses UP-TO-DATE
	:buildSrc:test UP-TO-DATE
	:buildSrc:check UP-TO-DATE
	:buildSrc:build
	:clean UP-TO-DATE
	:retrieveSchemas
	    Retrieving STIX schemas...
	Submodule 'src/main/resources/schemas/v1.1.1' (https://github.com/STIXProject/schemas.git) registered for path 'src/main/resources/schemas/v1.1.1'
	Submodule path 'src/main/resources/schemas/v1.1.1': checked out 'deef231f0e6fa9f05122f8e0fc089f03128bfe21'
	    Retrieving CybOX schemas...
	Submodule 'cybox' (https://github.com/CybOXProject/schemas.git) registered for path 'cybox'
	Submodule path 'cybox': checked out '97beb32c376a9223e91b52cb3e4c8d2af6baf786'
	
	    Patching src/main/resources/schemas/v1.1.1/cybox/objects/Archive_File_Object.xsd
	:createPrefixNamespaceBindings
	    schemas/v1.1.1/external/open_ioc_2010/ioc.xsd claims the same prefix "tns" as schemas/v1.1.1/external/open_ioc_2010/ioc-TR.xsd
	:generateJAXB
	    Generating JAXB model to src/generated/java for STIX Schema v1.1.1 found in src/main/resources/schemas/v1.1.1
	:generatedSourceTransformation
	:compileJava
	Note: Some input files use unchecked or unsafe operations.
	Note: Recompile with -Xlint:unchecked for details.
	:processResources
	:classes
	:jar
	
	BUILD SUCCESSFUL
	
	Total time: 1 mins 24.123 secs

If the build goes well you will find the jars: 

	build/libs/stix-${version}.jar
	build/libs/stix-${version}-javadoc.jar 
	build/libs/stix-${version}.jar-sources.jar

where `${version}` is replaced with the `version` number in the Gradle build script.

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

If the build goes well you will find the jars: 

	build\libs\stix-${version}.jar
	build\libs\stix-${version}-javadoc.jar 
	build\libs\stix-${version}.jar-sources.jar

where `${version}` is replaced with the `version` number in the Gradle build script.

## <a name="trouble_building"></a>Trouble building?

`You are less likely to see the following happen now that the Gradle 
build script will attempt to retreive the scheas, but if it is not successful it
will continue resulting in an error.`

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

### <a name="importing_into_eclipse"></a>Importing the project into Eclipse

The author sees this section out of scope, but added it in hopes it 
will help you over the hurdle.

As Eclipse's various Gradle plugins have varying degrees of usefulness,
I usually import the project as a `Java Project` and not a `Gradle Project`.

You can still use `Gradle EnIDE` plugin to run the Gradle build script 
from the IDE, but you'll need to configure your `Gradle Home to use` 
via the Eclipse IDE preference panel.

After running the Gradle build script, if you are using the Eclipse IDE
you may want to run the following from the command-line to set up the
Ecipse environment:

	gradle build cleanEclipse eclipse

It entirely depends on how smart yoour Eclipse Gradle plugin is and how
you imported the project.  The above command will change your prject to
essentially a Java Project. If you run the command, then right-click on
the Eclipse project and select "Refresh" to bring in the dependencies and 
source of JAXB Document Model. By default the generated source files are 
not editable to remmind you that changes to these files is usually a bad 
idea.

### <a name="opening_in_netbeans"></a>Opening the project into NetBeans

Like the [eclipse section above](#importing_into_eclipse), the author sees this section out of scope, 
but added it in hopes it will help you over the hurdle.

I've tested the project with NetBeans 8.0.1 and it works great with the
[Gradle JavaEE Support plugin][netbeans gradle].
With the plugin installed, open the project and right-click on the project 
root and select `build` from the `task` panel.

[stix project]: http://stixproject.github.io/
[stix]: http://stix.mitre.org/
[python stix]: https://github.com/STIXProject/python-stix
[semver]: http://semver.org/
[list]: https://stix.mitre.org/community/registration.html
[gradle]: http://www.gradle.org
[issue 340]: https://github.com/CybOXProject/schemas/issues/340
[patch 340]: https://raw.githubusercontent.com/nemonik/java_stix/master/cybox_object_archive_file_object.patch
[gradle ide pack]: http://marketplace.eclipse.org/content/gradle-ide-pack
[nodeclipse]: http://marketplace.eclipse.org/content/gradle
[netbeans gradle]:http://plugins.netbeans.org/plugin/55529/gradle-javaee-support

# <a name="intro"></a>java-stix 

The Java bindings for STIX, the Structured Threat Information  eXpression 
language used for representing structured cyber threat information.

java-stix is being developed under the official [STIXProject][stix project]

For more information, see [http://stix.mitre.org/][stix].

[![Circle CI](https://circleci.com/gh/STIXProject/java-stix.svg?style=svg)](https://circleci.com/gh/STIXProject/java-stix) [![Build Status](https://travis-ci.org/STIXProject/java-stix.svg)](https://travis-ci.org/STIXProject/java-stix)

## <a name="overview"></a>Overview

A primary goal of java-stix is to remain faithful to both the STIX 
specifications and customary Java practices. java-stix is designed to be 
intuitive both to Java developers and JAXB XML developers, provide convenience 
methods to the XJC generated JAXB model, but  obviously is not a one-for-one 
replacement for the [python-stix][python stix] API.

## <a name="versioning"></a>Versioning

Releases of java-stix will comply with the Semantic Versioning specification at 
[http://semver.org/][semver]. Java-stix is currently under active development; 
see TODO.txt for a tentative roadmap.  Releases will be announced on the [STIX 
discussion list][list]. A java-stix for a particular schema release will match the 
STIX schema version in Semantic Versioning form. Follow up patches of java-stix 
for a particular schema release will increment a 4th digit. For example, the first 
patch release for java-stix `v1.2.0` would be `v1.2.0.1`. Patches will be worked 
in a branch prior to being tagged and released.

## <a name="releases"></a>Releases

Releases are distributed via the Maven Central Repository. 

### <a name="maven_dependency_info"></a>Apache Maven:

	<dependency>
	  <groupId>org.mitre</groupId>
	  <artifactId>stix</artifactId>
	  <version>1.2.0.1</version>
	</dependency>

### <a name="gradle_dependency_info"></a>Gradle:

	compile 'org.mitre:stix:1.2.0.1'

### <a name="ivy__dependency_info"></a>Apache Ivy:

	<dependency org="org.mitre" name="stix" rev="1.2.0.1" />

## <a name="snapshots"></a>Snapshots

Snapshots are being pushed to 

[https://oss.sonatype.org/content/repositories/snapshots/org/mitre/stix/](https://oss.sonatype.org/content/repositories/snapshots/org/mitre/stix/)

Users using Apache Maven for example can simply retrieve java-stix directly via the Central Repository:

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
			<version>1.2.0.1-SNAPSHOT</version>
		</dependency>
	</dependencies>

[Release](#releases) artifacts appear in the Maven Central Repository.

# <a name="building"></a>Building

java-stix builds under Java7, and Java8.

## <a name="cloning"></a>Clone the repository

Clone the java-stix project via:

	git clone https://github.com/STIXProject/java_stix.git

A `git clone` command will not retrieve schemas project automatically, but the 
Gradle build script will attempt to retrieve them on execution.

Alternatively, using the `--recursive` flag when cloning the repository will 
automatically initialize and update the schema submodules via:

	git clone --recursive https://github.com/STIXProject/java_stix.git    

### <a name="gradle_wrapper"></a>Using the Gradle Wrapper

This project uses a Gradle build script.  The project comes with a Gradle 
Wrapper, a batch script on Windows, and a shell script for UNIX platforms 
including OS X thereby removing the need to have Gradle installed. 

For a UNIX platform, you can run Gradle from the project root via:

	./gradlew -x signArchives

This documentation centers on UNIX-centric Gradle command-line execution  for 
brevity, but if you are on Windows you can run Gradle via:

	.\gradlew.bat -x signArchives
	

Executing the `tasks` Gradle task will inform you of all the tasks runnable 
from root project like so:

	./gradlew tasks
	
I'd advise using the Gradle command-line, but if you're using the Eclipse IDE 
consider installing the latest [Gradle IDE Pack][gradle ide pack].  Gradle 
Eclipse integration is somewhat emergent. or if you're using Netbeans consider 
using [Nodeclipse/Enide Gradle for Eclipse][nodeclipse].
 
## <a name="retrieving_schemas"></a>Retrieving the schemas

If the schemas are not present, the Gradle build script will attempt  to 
retreive and patch the schemas for you, but if it cannot for some reason you 
will need to retrieve them yourself.

Gradle will execute either the retrieve_schemas.sh shell script or the 
retreieve_schemas.bat batch script as appopriate for your platform.  You can 
find these in the root of the project, and try executing them yourself or you 
can follow the steps enumerated in the next section for retrieving the schemas.

Afterward, the cybOX schemas are patched for the flaw described in [cybOX 
Schema issue #340][issue 340].  Until the issue is resolved please be aware of 
the patch. The [patch][patch 340] is found at the root of the project.

## <a name="re_retrieving_schemas"></a>Re-retrieving the schemas

If you should need to re-retrieve the schemas have the Gradle build script do 
it for you:

	./gradlew cleanSchema

Then [build](#building_via_gradle) as normal.

### <a name="stix_schemas"></a>The STIX schemas

To manually retrieve the schemas, enter the project and run these additonal git 
commands on the command line:

	git submodule init
	git submodule update --force
	cd src/main/resources/schemas/v1.2.0
	git checkout tags/v1.2.0

Your not done.  You'll also need to retrieve the CybOX schemas.

### <a name="cybox_schemas"></a>The CybOX schemas

While in the project:

	cd src/main/resources/schemas/v1.2.0
	git submodule init
	git submodule update --force
	cd cybox
	git checkout 97beb32c376a9223e91b52cb3e4c8d2af6baf786

When Gradle build script first runs it will also patch the CybOX schemas so 
that the JAXB2 Simple Document Object Model can be generated. Once patched, it 
will remained patched locally.  If Gradle fails to retrieve the schemas, you 
will need to patch the schemas by hand from the command-line.  The patch file, 
`cybox_object_archive_object_patch` is in  the root of the project.  NetBeans 
can also apply patches via
`Team` -> `Patches` -> `Apply Diff Patch`. 

## <a name="creating_document_object_model"></a>Creating the JAXB2 Document Object Model

### <a name="building_via_gradle"></a>Building

It is really unnecessary to build by hand, if your intention is to use the 
java-stix in your project as [snapshot](#importing_into_eclipse) exist in the 
Maven Central Snapshot repository, and soon releases will exist too. 

If you have not set up the signing properties you can instruct Gradle to skip 
signing jars by doing the following:

	./gradlew -x signArchives

After the Gradle wrapper shell script has downloaded and cached the Gradle 
binary distribution, a successful build will look like something like this 
on UNIX:

	➜  java_stix git:(master) ✗ ./gradlew -x signArchives
	:buildSrc:compileJava UP-TO-DATE
	:buildSrc:compileGroovy UP-TO-DATE
	:buildSrc:processResources UP-TO-DATE
	:buildSrc:classes UP-TO-DATE
	:buildSrc:jar UP-TO-DATE
	:buildSrc:assemble UP-TO-DATE
	:buildSrc:compileTestJava UP-TO-DATE
	:buildSrc:compileTestGroovy UP-TO-DATE
	:buildSrc:processTestResources UP-TO-DATE
	:buildSrc:testClasses UP-TO-DATE
	:buildSrc:test UP-TO-DATE
	:buildSrc:check UP-TO-DATE
	:buildSrc:build UP-TO-DATE
	:clean
	:retrieveSchemas
	    Schemas are present. Retrieval is not needed.
	    /Users/walsh/Development/workspace/java_stix/src/main/resources/schemas/v1.2.0/cybox/objects/Archive_File_Object.xsd already patched.
	:createPrefixNamespaceBindings
	    schemas/v1.2.0/external/open_ioc_2010/ioc.xsd claims the same prefix "tns" as schemas/v1.2.0/external/open_ioc_2010/ioc-TR.xsd
	:generateJAXB
	    Generating JAXB model to /Users/walsh/Development/workspace/java_stix/src/generated/java for STIX Schema v1.2.0 found in /Users/walsh/Development/workspace/java_stix/src/main/resources/schemas/v1.2.0
	:generatedSourceTransformation
	:createPackageInfo
	:compileJava
	warning: [options] bootstrap class path not set in conjunction with -source 1.7
	Note: Some input files use unchecked or unsafe operations.
	Note: Recompile with -Xlint:unchecked for details.
	1 warning
	:compileGroovy UP-TO-DATE
	:processResources
	:classes
	:jar
	:javadoc
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:277: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:361: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:341: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:311: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:331: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:277: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:311: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:331: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:341: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:361: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/SPKIData.java:115: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/X509Data.java:132: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/X509Data.java:132: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/X509Data.java:132: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:277: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:361: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:341: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:311: warning - Tag @link: reference not found: byte[]
	/Users/walsh/Development/workspace/java_stix/src/generated/java/org/w3/_2000/_09/xmldsig/ObjectFactory.java:331: warning - Tag @link: reference not found: byte[]
	19 warnings
	:javadocJar
	:sourcesJar
	:assemble
	:compileTestJava UP-TO-DATE
	:compileTestGroovy
	:processTestResources
	:testClasses
	:test
	:check
	:build
	
	BUILD SUCCESSFUL
	
	Total time: 3 mins 45.121 secs

Ignore the javadoc tag warnings they have no impact on the build. Also, ignore 
the warning about unchecked or unsafe operations as they are an artifact of 
XJC being used to autogenerate the document object model.

If you want more detail consider passing `-d`/`--debug` or `-i`/`--info` along with
`--stacktrace` on the command line like so:

	./gradlew -x signArchives --stacktrace --debug

for a more detailed build.  I set an environment `CI_OPTS` variable with this 
value for CircleCi builds to be bettered informed as to why the buiid is 
failing. Travis CI apparently cano't handle running in debug. So if you want 
see this level of build detail, click on the CircelCi badge for at the [top of this readme](#intro).

If the build goes well you will find the jars: 

	build/libs/stix-${version}.jar
	build/libs/stix-${version}-javadoc.jar 
	build/libs/stix-${version}.jar-sources.jar

where `${version}` is replaced with the `version` number in the Gradle build 
script.

### <a name="unit_testing"></a>Unit Testing

This project uses the [Spock Framework][spockframework] for unit testing. These 
tests are authored in [Groovy][groovylang] and exist under `src/test/groovy` 
path.

To execute the tests:

	/gradlew -x signArchives test

Consider using `-d`/`--debug` or `-i`/`--info` for more details during test 
execution like so:

	./gradlew -x signArchives -i

The `test` task will generated a report that can be found at 
`build/reports/tests/index.html`.

### <a name="examples"></a>Examples

Examples exist in src/main/java/org/mitre/stix/examples

	src/main/java/org/mitre/stix/examples/
	├── CIQIdentity.java
	├── CreationToolMetadata.java
	├── IndicatorHash.java
	├── XML2Object.java
	└── XMLParser.java

To execute CIQIdentity on the command-line via Gradle, do the following:

	./gradlew -PmainClass=org.mitre.stix.examples.CIQIdentity -x signArchives execute 

## <a name="addendum"></a>Addendum

The author sees this section out of scope, but added it in hopes it will help 
you over a hurdle.

### <a name="building_on_windows"></a>Building On Windows

I don't frequently build on Windows so there is a chance the build may fail on 
this platform.  You will need additional dependencies normally found in UNIX, 
such as, GNU patch.  GNU Patch can be installed via either cygwin, Chocolatey, 
or directly.  Then you'll need to work out permission elevation  issues so 
Gradle can execute the command-line GNU patch command through Gradle's Ant 
integration. Alternatively, see the section on [building using the Dockerfile](#building_via_docker).

To build on Windows use the gradlew batch script like so:

	$ .\gradlew.bat -x signArchives

If the build goes well you will find the jars: 

	build\libs\stix-${version}.jar
	build\libs\stix-${version}-javadoc.jar 
	build\libs\stix-${version}.jar-sources.jar

where `${version}` is replaced with the `version` number in the Gradle build 
script.  

### <a name="building_via_docker"></a>Building via the Dockerfile

If you have Docker installed, from the root of the project run 
the following to create a container image for this Dockerfile via:

	docker build -t java_stix_img_v1_2_0_1 . 

Then create a container using the image you just created via:

	docker run -t -i java_stix_img_v1_2_0_1 /bin/bash

To retreive the jar archives from the running docker container use following 
from the command-line of your docker host, not the container:

	docker cp <container id>:/java-stix/build/libs/stix-1.2.0.1-SNAPSHOT-javadoc.jar .
	docker cp <container id>:/java-stix/build/libs/stix-1.2.0.1-SNAPSHOT-sources.jar .
	docker cp <container id>:/java-stix/build/libs/stix-1.2.0.1-SNAPSHOT.jar .

If the containder ID is not obvious, but you can also retrieve it via:

	docker ps

An example of retrieving the snapshot jar would be the following:

	docker cp 83ad9afb6096:/java-stix/build/libs/stix-1.2.0.1-SNAPSHOT.jar . 
	
By default the Docker container will build using OpenJdk 7.  You can modify
the Dockerfile to other JVM's noted in the file.  Please, note Gradle targets 
Java 1.7 compatibility regardless.

### <a name="importing_into_eclipse"></a>Importing the project into Eclipse

As Eclipse's various Gradle plugins have varying degrees of usefulness, I 
usually import the project as a `Java Project` and not a `Gradle Project`.

You can still use `Gradle EnIDE` plugin to run the Gradle build script from the 
IDE, but you'll need to configure your `Gradle Home to use` 
via the Eclipse IDE preference panel.

After running the Gradle build script, if you are using the Eclipse IDE you may 
want to run the following from the command-line to set up the Ecipse 
environment:

	./gradlew build cleanEclipse eclipse -x signArchives

It entirely depends on how smart yoour Eclipse Gradle plugin is and how you 
imported the project.  The above command will change your prject to essentially 
a Java Project. If you run the command, then right-click on the Eclipse project 
and select "Refresh" to bring in the dependencies and source of JAXB Document 
Model. By default the generated source files are not editable to remmind you 
that changes to these files is usually a bad idea.

### <a name="opening_in_netbeans"></a>Opening the project into NetBeans

Like the [eclipse section above](#importing_into_eclipse), the author sees this 
section out of scope, but added it in hopes it will help you over the hurdle.

I've tested the project with NetBeans 8.0.1 and it works great with the [Gradle 
JavaEE Support plugin][netbeans gradle]. With the plugin installed, open the 
project and right-click on the project root and select `build` from the `task` 
panel.

[stix project]: http://stixproject.github.io/
[stix]: http://stix.mitre.org/
[python stix]: https://github.com/STIXProject/python-stix
[semver]: http://semver.org/
[list]: https://stix.mitre.org/community/registration.html
[gradle]: http://www.gradle.org
[issue 340]: https://github.com/CybOXProject/schemas/issues/340
[patch 340]: https://raw.githubusercontent.com/STIXProject/java-stix/master/cybox_object_archive_file_object.patch
[gradle ide pack]: http://marketplace.eclipse.org/content/gradle-ide-pack
[nodeclipse]: http://marketplace.eclipse.org/content/gradle
[netbeans gradle]: http://plugins.netbeans.org/plugin/55529/gradle-javaee-support
[spockframework]: http://spockframework.org
[groovylang]: http://www.groovy-lang.org/

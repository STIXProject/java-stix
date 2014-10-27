# JAVA STIX 

The JAXB2 Document Model for Structured Threat Information eXpression 
(STIX), a standardized language used to represent structured cyber threat 
information.

## Cloning the repository

git clone https://github.com/nemonik/java_stix.git

## Retrieve the schemas

A `git clone` command will not retrieve schemas project automatically. 

### The STIX schemas

First clone the project, then enter the project and run these additonal git commands
on the command line to retrieve the STIX schemas.

    git submodule init
    git submodule update

### The CybOX schemas

You then will also need to retrieve the CybOX schemas.

In the project:

    cd src/main/resources/schemas
    git submodule init
    git submodule update

### Schema updates

Any time you see that the schemas project has been modified (when merging or 
pulling updates) you will need to run 

    git submodule update

again to update the schemas themselves, and then recreate the JAXB
document model.

## Create the JAXB2 Document Model

Change directories into the project and enter on the command-line:

    gradle
    
Success will look like this:

	:createPrefixNamespaceBindings
	:cleanGenerate
	:generate
	:compileJava
	Note: Some input files use unchecked or unsafe operations.
	Note: Recompile with -Xlint:unchecked for details.
	:processResources
	:classes
	:jar

	BUILD SUCCESSFUL
	
	Total time: 22.929 secs
    
If the build goes well you will find the JAXB Document Model in jar at

	buil/libs/java-stix-${version}.jar

## Trouble Building?

If while building you get this error:

    FAILURE: Build failed with an exception.
    
    * Where:
    Build file '/Users/walsh/Development/workspace/STIXProject/java_stix/build.gradle' line: 52
    
    * What went wrong:
    Execution failed for task ':generate'.
    > grammar is not specified

    * Try:
    Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

 You likely didn't retrieve the schema decribed in the section above.

If you get this error while building:

    [ant:xjc] [ERROR] src-resolve: Cannot resolve the name 'cyboxCommon:ToolInformationType' to a(n) 'type definition' component.
    [ant:xjc]   line 907 of file:/Users/walsh/Development/workspace/STIXProject/java_stix/src/main/resources/schemas/stix_common.xsd
    [ant:xjc] 
    [ant:xjc] [ERROR] src-resolve: Cannot resolve the name 'cybox:ObservableType' to a(n) 'type definition' component.
    [ant:xjc]   line 439 of file:/Users/walsh/Development/workspace/STIXProject/java_stix/src/main/resources/schemas/stix_common.xsd
    [ant:xjc] 
    [ant:xjc] [ERROR] src-resolve: Cannot resolve the name 'SystemObj:SystemObjectType' to a(n) 'type definition' component.
    [ant:xjc]   line 149 of file:/Users/walsh/Development/workspace/STIXProject/java_stix/src/main/resources/schemas/external/maec_4.1/maec_package_schema.xsd
    -- snip --

 then you likely missed the step above where you must retrieved the CybOX 
 schemas.
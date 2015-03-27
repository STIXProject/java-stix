
package org.mitre.stix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import org.gradle.api.artifacts.Configuration

/**
 * Gradle Task used to generate the JAXB Document Model
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
class GenerateJAXBTask extends DefaultTask {

	@Input Configuration classpath
	@Input String schemaVersion

	GenerateJAXBTask() {
	   description = "Generate the JAXB Document Model"
	}

    @TaskAction
	def start() {
		ant.taskdef(name: "xjc", classname: "org.jvnet.jaxb2_commons.xjc.XJC2Task", classpath: classpath.asPath)
	
		ant.mkdir(dir: "src/generated/java")
		
		def schemaDir = "src/main/resources/schemas/v${schemaVersion}"
		
		println "    Generating JAXB model to src/generated/java for STIX Schema v${schemaVersion} found in ${schemaDir}"
	
		ant.xjc(destdir: "src/generated/java", extension: true, classpath: classpath.asPath) {
			arg(line: "-readOnly -verbose -Xequals -XhashCode -Xfluent-api -Xvalue-constructor -Xdefault-value -Xnamespace-prefix -Xinject-code -XtoString")
			binding(dir: "src/main/resources", includes: "*.xjb")
			schema(dir: schemaDir, includes: "cybox/cybox_core.xsd")
			schema(dir: schemaDir, includes: "cybox/cybox_common.xsd")
			schema(dir: schemaDir, includes: "cybox/cybox_default_vocabularies.xsd")
			schema(dir: schemaDir, includes: "cybox/objects/*.xsd")
			//schema(dir: schemaDir, excludes: "cybox/external/**/*.xsd")
			schema(dir: schemaDir, includes: "*.xsd")
			schema(dir: schemaDir, includes: "extensions/**/*.xsd")
			schema(dir: schemaDir, includes: "external/**/*.xsd")
			
			//Doing it this way, xjc cannot find its way to all the imported schemas ...
			//schema(dir: schemaDir, includes: "**/*.xsd")
		}
	}
}
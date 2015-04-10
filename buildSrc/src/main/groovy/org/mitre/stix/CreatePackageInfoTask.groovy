/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import groovy.text.SimpleTemplateEngine

/**
 * Gradle Task used to generate the package-info.java for org.mitre.stix
 * containing the version for schema.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
class CreatePackageInfoTask extends DefaultTask {

	@Input String schemaVersion

	CreatePackageInfoTask() {
		description = "Generates the package-info.java for org.mitre.stix"
	}

	@TaskAction
	def start() {

		def srcTemplate = """
/**
 * Copyright (c) ${Calendar.instance.get(Calendar.YEAR)}, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 *
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */

@Version(schema = "\${schemaVersion}")
package org.mitre.stix;
"""

		def engine = new SimpleTemplateEngine()
		
		def template = engine.createTemplate(srcTemplate).make(["schemaVersion": schemaVersion])
		def source = template.toString()
		
		if (! project.file("src/generated/java/org/mitre/stix/").exists()) {
			println "creating path"
			project.file("src/generated/java/org/mitre/stix/").mkdirs()
		}
		
		project.file("src/generated/java/org/mitre/stix/package-info.java").with { outFile ->
				outFile.setWritable(true)
				outFile.withWriter{ out -> out.println source }
				outFile.setWritable(false)
			}
	}
}
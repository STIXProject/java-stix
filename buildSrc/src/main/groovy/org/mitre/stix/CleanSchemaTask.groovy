/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Gradle Task used to remove the retrieved schema files.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
class CleanSchemaTask extends DefaultTask {

	@Input String schemaVersion

	CleanSchemaTask() {
		description = "Removes the retrieved schema files."
	}

	@TaskAction
	def start() {

		def schemaFolder = project.file("src/main/resources/schemas/v${schemaVersion}")

		if (schemaFolder.exists()) {
			ant.delete(dir: schemaFolder.getPath())
			println "    Deleted ${schemaFolder.getPath()}"
		} else {
			println "    ${schemaFolder.getPath()} has already been deleted."
		}
	}
}
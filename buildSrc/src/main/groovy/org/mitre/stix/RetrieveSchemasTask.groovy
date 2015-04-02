/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException

import org.apache.tools.ant.taskdefs.condition.Os

/**
 * Gradle Task used to attempt to automatically retrieve the schemas
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
class RetrieveSchemasTask extends DefaultTask {

	@Input String schemaVersion
	
	RetrieveSchemasTask() {
		description "Automatically attempt to retrieve the schemas."
	}
	
	def patch() {
		println("    Patching src/main/resources/schemas/v" + schemaVersion + "/cybox/objects/Archive_File_Object.xsd")
		ant.patch(patchfile: "cybox_object_archive_file_object.patch", originalfile: "src/main/resources/schemas/v" + schemaVersion + "/cybox/objects/Archive_File_Object.xsd")
	}
	
	def pull() {
	
		def command = null

		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			command = "cmd /c retrieve_schemas.bat"
		} else {
			command = "sh ./retrieve_schemas.sh"
		}

		def proc = command.execute(null, project.rootDir)
		proc.waitFor()

		println("    ${proc.in.text}")
	}

	@TaskAction
	def retrieve() {
		if ((project.file("src/main/resources/schemas/v${schemaVersion}").list().size() == 0) || (project.file("src/main/resources/schemas/v${schemaVersion}/cybox").list() == null)) {
			pull()
			patch()
			if ((project.file("src/main/resources/schemas/v${schemaVersion}").list().size() == 0) || (project.file("src/main/resources/schemas/v${schemaVersion}/cybox").list() == null)) {
				throw new GradleException("    Build error occurred: You will need retrieve schemas by hand. See README.md file.");
			}
		} else {
			println("    Schemas are present. Retrieval is not needed.")
		}
	}
}
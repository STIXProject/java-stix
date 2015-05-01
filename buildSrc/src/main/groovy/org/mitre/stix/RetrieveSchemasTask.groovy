/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix

import org.mitre.Checksum

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException

import groovy.io.FileType

import org.mitre.stix.Checksum

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
		def fileToBePatched = project.file("src/main/resources/schemas/v${schemaVersion}/cybox/objects/Archive_File_Object.xsd")
		
		if (new Checksum().calc(fileToBePatched) != "e986dddfa05a2404c155b7c2b93603e4af31b4e9") {
			
			println("    Patching ${fileToBePatched}")
			
			ant.patch(patchfile: "cybox_object_archive_file_object.patch", originalfile: fileToBePatched)
		} else {
			println("    ${fileToBePatched} already patched.")
		}
	}
	
	def pull() {
	
		def command = null

		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			command = "cmd /c .\retrieve_schemas.bat"
		} else {
			command = "sh ./retrieve_schemas.sh"
		}

		def proc = command.execute(null, project.rootDir)
		proc.waitFor()

		println("${proc.in.text}")
	}

	@TaskAction
	def retrieve() {
		
		if (project.fileTree("src/main/resources/schemas/v${schemaVersion}").isEmpty() || project.fileTree("src/main/resources/schemas/v${schemaVersion}/cybox").isEmpty()) {
			pull()
		} else {
			println("    Schemas are present. Retrieval is not needed.")
		}
		
		patch()
	}
}
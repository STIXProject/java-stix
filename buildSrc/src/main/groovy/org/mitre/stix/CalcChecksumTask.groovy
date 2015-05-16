/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.io.FileInputStream
import java.security.MessageDigest

public class Checksum {

	def calc(File file) {
		
		def messageDigest = MessageDigest.getInstance("SHA1")
		def fileInputStream = new FileInputStream(file)
		def bytes = new byte[1024]
		
		def bytesUsed = 0; 
		
		while ((bytesUsed = fileInputStream.read(bytes)) != -1) {
			messageDigest.update(bytes, 0, bytesUsed)
		}
		
		def stringBuffer = new StringBuffer("")
		
		def hash = messageDigest.digest();
		
		for (int i = 0; i < hash.length; i++) {
			stringBuffer.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1))
		}
		
		return stringBuffer.toString()
	}
}

class CalcChecksumTask extends DefaultTask {
	
	@Input String schemaVersion
	
	CalcChecksumTask() {
		description = "Calculates checksum for a file. For example: gradle -Pfilepath=src/main/resources/schemas/v${schemaVersion}/cybox/objects/Archive_File_Object.xsd calcChecksum"
	}
	
	@TaskAction
	def printChecksum() {
		if (project.filepath != null && !project.filepath.isEmpty()) {
			
			def checksum = new Checksum().calc(project.file(project.filepath))
			
			println "Hex Digest for ${project.filepath} is `${checksum}`."
		} else {
			println "Pass path for checksum to be calculated via `path` project property."
		}
	}
}
/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix

import groovy.io.FileType
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.io.File
import java.io.FileInputStream

import java.text.SimpleDateFormat

import java.util.Date

import javax.xml.parsers.DocumentBuilderFactory

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Gradle Task used to dynamically creates the src/main/resources/namespace-prefix.xjb
 * file from the schemas.
 *
 * namespace-prefix.xjb prevents XJC from dynamically assigning ns namespaces
 * and catalog.xml is used for parsing and validation.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
class PrefixNamespaceBindingsTask extends DefaultTask {

	PrefixNamespaceBindingsTask() {
		description "Dynamically create the prefix-namespace bindings file off of the schemas"
	}
	
	// Returns a hashmap Binding objects
	def get() {
	
		// ignore these namespaces cuz they are orphans
		def notPartOfThisCompilation = [
			"schemas/v1.1.1/cybox/external/cpe_2.3/cpe-naming_2.3.xsd",
			"schemas/v1.1.1/cybox/external/cpe_2.3/cpe-language_2.3.xsd",
			"schemas/v1.1.1/cybox/external/oasis_ciq_3.0/xlink-2003-12-31.xsd"
		]

		// fix paths for present platform
		if (File.separator.equals("\\")) {
			// couldn't get collect to work on windows
			def temp = []
			notPartOfThisCompilation.each {
				temp.add(it.replaceAll("/", "\\\\"))
			}

			notPartOfThisCompilation = temp
		}

		def prefixSchemaBindings = [:]

		// Gather up the schemas
		def schemas = []
		project.file("src/main/resources/schemas/v1.1.1").eachFileRecurse(FileType.FILES) { file ->
			if (file.name.endsWith(".xsd")) {
				schemas << file
			}
		}	
		
		schemas.each {schema ->

			def schemaLocation = "schemas/v1.1.1" + schema.getAbsolutePath().split("schemas/v1.1.1")[1]
			
			if (!notPartOfThisCompilation.contains(schemaLocation)) {
	
				def document = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(new FileInputStream(schema))
	
				document.getDocumentElement().normalize()
	
				def attributes = document.getDocumentElement().getAttributes()
	
				def attribute, targetNamespace, prefix
								
				for (int i = 0 ; i < attributes.getLength() ; i++) {
					attribute = attributes.item(i)
					targetNamespace = document.getDocumentElement().getAttribute("targetNamespace")
	
					if (attribute.getNodeName().startsWith("xmlns:") && attribute.getNodeValue().equals(targetNamespace)) {
	
						prefix = attributes.item(i).getNodeName().split(":")[1]
						
						if (prefixSchemaBindings.containsKey(prefix)) {
							//TODO: change to logging.
							logger.warn("    " + schemaLocation + " claims the same prefix \"" + prefix + "\" as " + prefixSchemaBindings[prefix])
						} else {
							prefixSchemaBindings[prefix] = schemaLocation
						}
					}
				}
			}
		}

		//Add these external schemas
		prefixSchemaBindings["xal"] = "schemas/v1.1.1/external/oasis_ciq_3.0/xAL-types.xsd"	
		prefixSchemaBindings["xpil"] = "schemas/v1.1.1/external/oasis_ciq_3.0/xPIL.xsd"
		prefixSchemaBindings["xnl"] = "schemas/v1.1.1/external/oasis_ciq_3.0/xNL-types.xsd"
		
		// Rename "maecPackage" to "maec"
		prefixSchemaBindings.remove("maecPackage")
		prefixSchemaBindings["maec"] = "schemas/v1.1.1/external/maec_4.1/maec_package_schema.xsd"

		// The crawl would map the 'tns' prefix to both of these... fix that.
		prefixSchemaBindings.remove("tns")
		prefixSchemaBindings["ioc-tr"] = "schemas/v1.1.1/external/open_ioc_2010/ioc-TR.xsd"
		prefixSchemaBindings["ioc"] = "schemas/v1.1.1/external/open_ioc_2010/ioc.xsd"

//		prefixSchemaBindings.each{ k, v ->
//			log.info("---> ${k} : ${v}")
//		}

		prefixSchemaBindings
	}

	// Dynamically creates the src/main/resources/namespace-prefix.xjb file used by XJC jaxb2-namespace-prefix plugin
	@TaskAction
	def create() {
	
		def prefixSchemaBindings = get()

		def dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		def date = new Date();

		def xmlBuilder = new StreamingMarkupBuilder()
		def writer = xmlBuilder.bind {
			mkp.comment "WARNING!!!!! Dynamically created on ${dateFormat.format(date)} by the build.gradle's createPrefixNamespaceBindings task. All changes made to this file will be lost."
			mkp.declareNamespace(jaxb:"http://java.sun.com/xml/ns/jaxb")
			mkp.declareNamespace(xsi:"http://www.w3.org/2001/XMLSchema-instance")
			mkp.declareNamespace(xs:"http://www.w3.org/2001/XMLSchema")
			mkp.declareNamespace(namespace:"http://jaxb2-commons.dev.java.net/namespace-prefix")
			'jaxb:bindings'(version: "2.1", "xsi:schemaLocation":"http://java.sun.com/xml/ns/jaxb http://java.sun.com/xml/ns/jaxb/bindingschema_2_0.xsd http://jaxb2-commons.dev.java.net/namespace-prefix http://java.net/projects/jaxb2-commons/sources/svn/content/namespace-prefix/trunk/src/main/resources/prefix-namespace-schema.xsd") {
				prefixSchemaBindings.each { prefix, schemaLocation ->
					'jaxb:bindings'(schemaLocation:schemaLocation) {
						'namespace:prefix'(name: prefix)
					}
				}
			}
		}
		
		project.file("src/main/resources/namespace-prefix.xjb")
				.setText(XmlUtil.serialize(writer.toString()))
				
	}
}
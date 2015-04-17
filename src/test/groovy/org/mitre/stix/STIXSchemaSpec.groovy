/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 *
 * Spock unit test for STIXSchema
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
import org.mitre.stix.STIXSchema
import org.apache.commons.io.IOUtils
import org.mitre.stix.stix_1.STIXPackage
 
class STIXSchemaSpec extends spock.lang.Specification{ 

	def "STIXSchema is a singleton"() {
		when: "when two instance are created"
 			def schema = STIXSchema.getInstance()
 			def schema2 =  STIXSchema.getInstance()
 		then: "they should be equal"
 			schema == schema2
 	}
	 
	def "version must be in semantic versioning 2.0.0 form"() {
		when:
 			def schema = STIXSchema.getInstance()
 		then:
 			schema.getVersion() =~ /(\d+)(\.)(\d+)(\.)(\d+)/
 	}
	
	def "validate known valid STIX XML"() {
		when: "retrieved from URL"
 			def schema = STIXSchema.getInstance()
 			def url = getClass().getClassLoader().getResource("org/mitre/stix/sample.xml")
 		then: "it should validate"
 			schema.validate(url)== true
	}
	
	def "validate known valid STIX XML"() {
		when: "contained in String"
 			def schema = STIXSchema.getInstance()
 			def url = getClass().getClassLoader().getResource("org/mitre/stix/sample.xml")
 			def xmlText = IOUtils.toString(url.openStream());
 		then: "it should validate"
 			schema.validate(xmlText) == true
	}
	
	def "STIXPackage model objects has the expected namespace URI"() {
		when: "STIXPackage object is created"
			def stixPackage = new STIXPackage()
 		then: 'Messaged to STIXSchema.getNamespaceURI, it returns "http://stix.mitre.org/stix-1"'
 			"http://stix.mitre.org/stix-1" == STIXSchema.getNamespaceURI(stixPackage)
	}
}
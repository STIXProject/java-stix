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
import com.github.zafarkhaja.semver.Version
 
class STIXSchemaSpec extends spock.lang.Specification{ 

	def "STIXSchema is a singleton"() {
		when: "when two instance are created"
 			def schema = STIXSchema.getInstance()
 			def schema2 =  STIXSchema.getInstance()
 		then: "they should be equal"
 			schema == schema2
 	}
	 
	def "Version must be in semantic versioning 2.0.0 form"() {
		when:
 			def schema = STIXSchema.getInstance()
 		then:
 			Version.valueOf(schema.getVersion())
 	}
	
	def "Valid STIX XML retrieved from a URL validates at true"() {
		when: "retrieved from URL"
 			def schema = STIXSchema.getInstance()
 			def url = getClass().getClassLoader().getResource("org/mitre/stix/sample.xml")
 		then: "it should validate"
 			schema.validate(url)== true
	}
	
	def "A String containing valid STIX XML validates at true"() {
		when: "contained in String"
 			def schema = STIXSchema.getInstance()
 			def url = getClass().getClassLoader().getResource("org/mitre/stix/sample.xml")
 			def xmlText = IOUtils.toString(url.openStream());
 		then: "it should validate"
 			schema.validate(xmlText) == true
	}
	
	def "SA TIXPackage model objects has the expected namespace URI"() {
		when: "STIXPackage object is created"
			def stixPackage = new STIXPackage()
 		then: 'Messaged to STIXSchema.getNamespaceURI returns "http://stix.mitre.org/stix-1"'
 			"http://stix.mitre.org/stix-1" == STIXSchema.getNamespaceURI(stixPackage)
	}
}
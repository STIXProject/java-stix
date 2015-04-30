/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 *
 * Spock unit test for DocumentUtilites
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
import org.mitre.stix.DocumentUtilities
import org.mitre.stix.stix_1.STIXPackage
import org.mitre.stix.stix_1.STIXHeaderType

import org.apache.commons.io.IOUtils

import java.io.StringReader;
import java.util.GregorianCalendar

import javax.xml.datatype.DatatypeFactory
import javax.xml.namespace.QName
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.XMLUnit

import spock.lang.*

class DocumentUtilitiesSpec extends spock.lang.Specification{

	@Shared def version = "1.1.1"
		
	@Shared def controlXML = """<?xml version="1.0" encoding="UTF-8"?>
<stix:STIX_Package
    id="example:package-af59abd3-102c-43d0-89ed-e0a90525d747"
    timestamp="1970-01-01T00:00:00.000Z" version="${version}"
    xmlns="http://xml/metadataSharing.xsd"
    xmlns:example="http://example.com/" xmlns:stix="http://stix.mitre.org/stix-1"/>"""
	
	@Shared def c = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
	@Shared epoch
	
	def setup() {
		c.setTimeInMillis(0)
		epoch = DatatypeFactory.newInstance().newXMLGregorianCalendar(c)
		
		XMLUnit.setIgnoreWhitespace(true)
		XMLUnit.setIgnoreAttributeOrder(true)
	}
	
	def "Converting a JAXBElement to a Document returns an expected result"() {
		when: "A STIX Package model is created, converted to  Document representation"
			def stixPackage = new STIXPackage()
								.withVersion(version)
								.withTimestamp(epoch)
								.withId(new QName("http://example.com/", 
									"package-af59abd3-102c-43d0-89ed-e0a90525d747", 
									"example"))
			
			def test = DocumentUtilities.toDocument(stixPackage.toJAXBElement())
			test.normalizeDocument()
			
		then: "It is identical to the expected XML String"
			
			def diff = new DetailedDiff(XMLUnit.compareXML(controlXML, DocumentUtilities.toXMLString(test)))
			
			diff.identical()
	}
	
	def "Converting an XML String to a Document returns an expected result"() {
		when: "An XML String is used to create a Document"
			
			def test = DocumentUtilities.toDocument(controlXML)
			test.normalizeDocument()
			
		then: "It is identical to the expected XML String"
			
			def diff = new DetailedDiff(XMLUnit.compareXML(controlXML, DocumentUtilities.toXMLString(test)))
			
			diff.identical()
	}
	
	def "Converting a Document to a String returns an expected result"() {
		when: "A Model is created, converted to Document representation, and then serialize to an XML String"
			
			def stixPackage = new STIXPackage()
								.withVersion(version)
								.withTimestamp(epoch)
								.withId(new QName("http://example.com/", 
									"package-af59abd3-102c-43d0-89ed-e0a90525d747", 
									"example"))
			
			def testXML = DocumentUtilities.toXMLString(stixPackage.toDocument())
			
		then: "It is identical the the expected XML String."
			
			def diff = new DetailedDiff(XMLUnit.compareXML(controlXML, testXML ))
			
			diff.identical()
	}
	
		def "Converting a JAXBElement to a String returns an expected result"() {
		when: "A Model is created, converted to JAXBElement, and then serialize to an XML String"
			
			def stixPackage = new STIXPackage()
								.withVersion(version)
								.withTimestamp(epoch)
								.withId(new QName("http://example.com/", 
									"package-af59abd3-102c-43d0-89ed-e0a90525d747", 
									"example"))
			
			def testXML = DocumentUtilities.toXMLString(stixPackage.toJAXBElement())
			
		then: "It is identical the the expected XML String."
			
			def diff = new DetailedDiff(XMLUnit.compareXML(controlXML, testXML ))
			
			diff.identical()
	}
}
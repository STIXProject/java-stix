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

class DocumentUtilitiesSpec extends spock.lang.Specification{
	
	def "Converting a JAXBElement to Document returns an expected result"() {
		when: "A STIX Package model is created and Document representation for the same is created"
			def version = "1.1.1" 
			
			def c = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
			c.setTimeInMillis(0)
			
			def epoch = DatatypeFactory.newInstance().newXMLGregorianCalendar(c)
			
			def stixPackage = new STIXPackage()
								.withVersion(version)
								.withTimestamp(epoch)
								.withId(new QName("http://example.com/", 
									"package-af59abd3-102c-43d0-89ed-e0a90525d747", 
									"example"))

			def documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			
			def inputSource = new InputSource()
			inputSource.setCharacterStream(new StringReader(stixPackage.toXMLString()))
			
			def doc1 = documentBuilder.parse(inputSource)
			doc1.normalizeDocument()
		then: "They produce identical XML"
			def doc2 = DocumentUtilities.toDocument(stixPackage.toJAXBElement())
			doc2.normalizeDocument()
			
			XMLUnit.setIgnoreWhitespace(true)
			XMLUnit.setIgnoreAttributeOrder(true)
			
			def diff = new DetailedDiff(XMLUnit.compareXML(DocumentUtilities.toXMLString(doc1), DocumentUtilities.toXMLString(doc2)))
			
			diff.identical()
	}
	
	def "Converting an XML String to Document returns an expected result"() {
		when: "An XML String is used to create a Document"
			def xml = """<?xml version="1.0" encoding="UTF-8"?>
    <stix:STIX_Package id="example:package-af59abd3-102c-43d0-89ed-e0a90525d747" timestamp="1970-01-01T00:00:00.000Z" version="1.1.1" xmlns="http://xml/metadataSharing.xsd" xmlns:example="http://example.com/" xmlns:stix="http://stix.mitre.org/stix-1"/>"""

			def documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			
			def inputSource = new InputSource()
			inputSource.setCharacterStream(new StringReader(xml))
			
			def doc1 = documentBuilder.parse(inputSource)
			doc1.normalizeDocument()
		then: "They are identical"
			def doc2 = DocumentUtilities.toDocument(xml)
			doc2.normalizeDocument()
			
			XMLUnit.setIgnoreWhitespace(true)
			XMLUnit.setIgnoreAttributeOrder(true)
			
			def diff = new DetailedDiff(XMLUnit.compareXML(DocumentUtilities.toXMLString(doc1), DocumentUtilities.toXMLString(doc2)))
			
			diff.identical()
	}

	def "Converting an JAXBElement to String returns an expected result"() {
		when: "A JAXBElement and an XML String is created"
			def version = "1.1.1" 
			
			def c = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
			c.setTimeInMillis(0)
			
			def epoch = DatatypeFactory.newInstance().newXMLGregorianCalendar(c)
			
			def stixPackage = new STIXPackage()
								.withVersion(version)
								.withTimestamp(epoch)
								.withId(new QName("http://example.com/", 
									"package-af59abd3-102c-43d0-89ed-e0a90525d747", 
									"example"))
			
			def controlXML = """<?xml version="1.0" encoding="UTF-8"?>
    <stix:STIX_Package id="example:package-af59abd3-102c-43d0-89ed-e0a90525d747" timestamp="1970-01-01T00:00:00.000Z" version="1.1.1" xmlns="http://xml/metadataSharing.xsd" xmlns:example="http://example.com/" xmlns:stix="http://stix.mitre.org/stix-1"/>"""
		then: "The JAXBElement is converted to identical XML"
			def testXML = DocumentUtilities.toXMLString(stixPackage.toJAXBElement())
			
			XMLUnit.setIgnoreWhitespace(true)
			XMLUnit.setIgnoreAttributeOrder(true)
			
			def diff = new DetailedDiff(XMLUnit.compareXML(controlXML, testXML ))
			
			diff.identical()
	}
}
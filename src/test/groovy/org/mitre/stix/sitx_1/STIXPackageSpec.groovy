/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 *
 * Spock unit test for DocumentUtilites
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
import java.util.ArrayList
import java.util.GregorianCalendar
import java.util.TimeZone
import java.util.UUID

import javax.xml.datatype.DatatypeConfigurationException
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import javax.xml.namespace.QName
import javax.xml.parsers.ParserConfigurationException

import oasis.names.tc.ciq.xnl._3.NameLine
import oasis.names.tc.ciq.xnl._3.PartyNameType
import oasis.names.tc.ciq.xnl._3.PartyNameType.OrganisationName
import oasis.names.tc.ciq.xnl._3.PartyNameType.PersonName
import oasis.names.tc.ciq.xpil._3.ContactNumbers
import oasis.names.tc.ciq.xpil._3.ElectronicAddressIdentifiers
import oasis.names.tc.ciq.xpil._3.FreeTextLines

import org.apache.commons.lang.StringUtils
import org.mitre.cybox.common_2.DateTimeWithPrecisionType
import org.mitre.cybox.common_2.HashListType
import org.mitre.cybox.common_2.HashType
import org.mitre.cybox.common_2.SimpleHashValueType
import org.mitre.cybox.common_2.TimeType
import org.mitre.cybox.cybox_2.ObjectType
import org.mitre.cybox.cybox_2.Observable
import org.mitre.cybox.default_vocabularies_2.HashNameVocab10
import org.mitre.cybox.objects.FileObjectType
import org.mitre.stix.DocumentUtilities
import org.mitre.stix.common_1.IndicatorBaseType
import org.mitre.stix.common_1.InformationSourceType
import org.mitre.stix.common_1.StructuredTextType
import org.mitre.stix.extensions.identity.CIQIdentity30InstanceType
import org.mitre.stix.extensions.identity.STIXCIQIdentity30Type
import org.mitre.stix.indicator_2.Indicator
import org.mitre.stix.stix_1.IndicatorsType
import org.mitre.stix.stix_1.STIXHeaderType
import org.mitre.stix.stix_1.STIXPackage

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.XMLUnit

import spock.lang.*

class STIXPackageSpec extends spock.lang.Specification{

	def "A STIXPackage when created creates the expected XML String"() {
		setup:
			XMLUnit.setIgnoreWhitespace(true)
			XMLUnit.setIgnoreAttributeOrder(true)
			
			def controlXML = """<?xml version="1.0" encoding="UTF-8"?>
    <stix:STIX_Package
        id="example:package-0ebcdf8d-88a0-42ea-9047-0cb2922fdaa9"
        timestamp="1970-01-01T00:00:00.000Z" version="1.2.0"
        xmlns="http://xml/metadataSharing.xsd"
        xmlns:FileObj="http://cybox.mitre.org/objects#FileObject-2"
        xmlns:cybox="http://cybox.mitre.org/cybox-2"
        xmlns:cyboxCommon="http://cybox.mitre.org/common-2"
        xmlns:cyboxVocabs="http://cybox.mitre.org/default_vocabularies-2"
        xmlns:example="http://example.com/"
        xmlns:indicator="http://stix.mitre.org/Indicator-2"
        xmlns:stix="http://stix.mitre.org/stix-1"
        xmlns:stix-ciqidentity="http://stix.mitre.org/extensions/Identity#CIQIdentity3.0-1"
        xmlns:stixCommon="http://stix.mitre.org/common-1"
        xmlns:xnl="urn:oasis:names:tc:ciq:xnl:3" xmlns:xpil="urn:oasis:names:tc:ciq:xpil:3">
        <stix:STIX_Header>
            <stix:Description>Example</stix:Description>
        </stix:STIX_Header>
        <stix:Indicators>
            <stix:Indicator
                id="example:indicator-71043d56-292b-4dd4-92ea-316e4d9cf740"
                timestamp="1970-01-01T00:00:00.000Z"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="indicator:IndicatorType">
                <indicator:Title>File Hash Example</indicator:Title>
                <indicator:Description>An indicator containing a File observable with an associated hash</indicator:Description>
                <indicator:Observable id="example:observable-df196b8a-c2a2-4bed-892a-d389d5947067">
                    <cybox:Object id="example:file-5b2a0d1d-f53c-4175-9d5e-009ef6182385">
                        <cybox:Properties xsi:type="FileObj:FileObjectType">
                            <FileObj:Hashes>
                                <cyboxCommon:Hash>
                                    <cyboxCommon:Type xsi:type="cyboxVocabs:HashNameVocab-1.0">MD5</cyboxCommon:Type>
                                    <cyboxCommon:Simple_Hash_Value>4EC0027BEF4D7E1786A04D021FA8A67F</cyboxCommon:Simple_Hash_Value>
                                </cyboxCommon:Hash>
                            </FileObj:Hashes>
                        </cybox:Properties>
                    </cybox:Object>
                </indicator:Observable>
                <indicator:Producer>
                    <stixCommon:Description>An indicator containing a File observable with an associated hash</stixCommon:Description>
                    <stixCommon:Identity xsi:type="stix-ciqidentity:CIQIdentity3.0InstanceType">
                        <stix-ciqidentity:Specification>
                            <xpil:FreeTextLines>
                                <xpil:FreeTextLine>Demonstrating Free Text!</xpil:FreeTextLine>
                            </xpil:FreeTextLines>
                            <xpil:PartyName>
                                <xnl:NameLine>Foo</xnl:NameLine>
                                <xnl:NameLine>Bar</xnl:NameLine>
                                <xnl:PersonName>
                                    <xnl:NameElement>John Smith</xnl:NameElement>
                                </xnl:PersonName>
                                <xnl:PersonName>
                                    <xnl:NameElement>Jill Smith</xnl:NameElement>
                                </xnl:PersonName>
                                <xnl:OrganisationName>
                                    <xnl:NameElement>Foo Inc.</xnl:NameElement>
                                </xnl:OrganisationName>
                                <xnl:OrganisationName>
                                    <xnl:NameElement>Bar Corp.</xnl:NameElement>
                                </xnl:OrganisationName>
                            </xpil:PartyName>
                            <xpil:ContactNumbers>
                                <xpil:ContactNumber>
                                    <xpil:ContactNumberElement>555-555-5555</xpil:ContactNumberElement>
                                    <xpil:ContactNumberElement>555-555-5556</xpil:ContactNumberElement>
                                </xpil:ContactNumber>
                            </xpil:ContactNumbers>
                            <xpil:ElectronicAddressIdentifiers>
                                <xpil:ElectronicAddressIdentifier>jsmith@example.com</xpil:ElectronicAddressIdentifier>
                            </xpil:ElectronicAddressIdentifiers>
                        </stix-ciqidentity:Specification>
                    </stixCommon:Identity>
                    <stixCommon:Time>
                        <cyboxCommon:Produced_Time>1970-01-01T00:00:00.000Z</cyboxCommon:Produced_Time>
                    </stixCommon:Time>
                </indicator:Producer>
            </stix:Indicator>
        </stix:Indicators>
    </stix:STIX_Package>"""
			
		when: "A STIXPackage is created, converted to Document representation, and then serialize to an XML String"
			def version = "1.2.0"
			
			def c = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
			c.setTimeInMillis(0)
			
			def epoch = DatatypeFactory.newInstance().newXMLGregorianCalendar(c)
			
			def contactNumbers = new ContactNumbers()
					.withContactNumbers(new ContactNumbers.ContactNumber()
							.withContactNumberElements(
									new ContactNumbers.ContactNumber.ContactNumberElement()
											.withValue("555-555-5555"),
									new ContactNumbers.ContactNumber.ContactNumberElement()
											.withValue("555-555-5556")))
			
			def electronicAddressIdentifiers = new ElectronicAddressIdentifiers()
					.withElectronicAddressIdentifiers(new ElectronicAddressIdentifiers.ElectronicAddressIdentifier()
							.withValue("jsmith@example.com"))
			
			def freeTextLines = new FreeTextLines()
					.withFreeTextLines(new FreeTextLines.FreeTextLine()
							.withValue("Demonstrating Free Text!"))
			
			def partyName = new PartyNameType()
					.withNameLines(new NameLine().withValue("Foo"),
							new NameLine().withValue("Bar"))
					.withPersonNames(
							new PersonName().withNameElements(new oasis.names.tc.ciq.xnl._3.PersonNameType.NameElement()
									.withValue("John Smith")),
							new PersonName()
									.withNameElements(new oasis.names.tc.ciq.xnl._3.PersonNameType.NameElement()
											.withValue("Jill Smith")))
					.withOrganisationNames(
							new OrganisationName().withNameElements(new oasis.names.tc.ciq.xnl._3.OrganisationNameType.NameElement()
									.withValue("Foo Inc.")),
							new OrganisationName()
									.withNameElements(new oasis.names.tc.ciq.xnl._3.OrganisationNameType.NameElement()
											.withValue("Bar Corp.")))
			
			def specification = new STIXCIQIdentity30Type()
					.withContactNumbers(contactNumbers)
					.withElectronicAddressIdentifiers(
							electronicAddressIdentifiers)
					.withFreeTextLines(freeTextLines).withPartyName(partyName)
			
			def identity = new CIQIdentity30InstanceType()
					.withSpecification(specification)
			
			def producer = new InformationSourceType()
					.withDescriptions(
							new StructuredTextType().withValue(
									"An indicator containing a File observable with an associated hash"))
					.withTime(
							new TimeType()
									.withProducedTime(new DateTimeWithPrecisionType(
											epoch, null))).withIdentity(identity)
			
			def fileObject = new FileObjectType()
					.withHashes(new HashListType(new ArrayList<HashType>() {
						{
							add(new HashType()
									.withType(
											new HashNameVocab10()
													.withValue("MD5"))
									.withSimpleHashValue(
											new SimpleHashValueType()
													.withValue("4EC0027BEF4D7E1786A04D021FA8A67F")))
						}
					}))

			def obj = new ObjectType().withProperties(fileObject)
					.withId(new QName("http://example.com/", "file-"
							+ "5b2a0d1d-f53c-4175-9d5e-009ef6182385", "example"))

			def observable = new Observable().withId(new QName(
					"http://example.com/", "observable-"
							+ "df196b8a-c2a2-4bed-892a-d389d5947067", "example"))

			observable.setObject(obj)

			def indicator = new Indicator()
					.withId(new QName("http://example.com/", "indicator-"
							+ "71043d56-292b-4dd4-92ea-316e4d9cf740", "example"))
					.withTimestamp(epoch)
					.withTitle("File Hash Example")
					.withDescriptions(
							new StructuredTextType().withValue(
									"An indicator containing a File observable with an associated hash"))
					.withProducer(producer)
					.withObservable(observable)

			def indicators = new IndicatorsType(
					new ArrayList<IndicatorBaseType>() {
						{
							add(indicator)
						}
					})

			def header = new STIXHeaderType()
					.withDescriptions(new StructuredTextType()
							.withValue("Example"))

			def stixPackage = new STIXPackage()
					.withSTIXHeader(header)
					.withIndicators(indicators)
					.withVersion("${version}")
					.withTimestamp(epoch)
					.withId(new QName("http://example.com/", "package-"
							+ "0ebcdf8d-88a0-42ea-9047-0cb2922fdaa9", "example"))
			
			def testXML = DocumentUtilities.toXMLString(stixPackage.toDocument())
			
		then: "It is identical the the expected XML String."
			def diff = new DetailedDiff(XMLUnit.compareXML(controlXML, testXML ))
			
			diff.identical()
	}
}
/**
 * Copyright (c) 2014, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import oasis.names.tc.ciq.xnl._3.NameLine;
import oasis.names.tc.ciq.xnl._3.PartyNameType;
import oasis.names.tc.ciq.xnl._3.PartyNameType.OrganisationName;
import oasis.names.tc.ciq.xnl._3.PartyNameType.PersonName;
import oasis.names.tc.ciq.xpil._3.ContactNumbers;
import oasis.names.tc.ciq.xpil._3.ElectronicAddressIdentifiers;
import oasis.names.tc.ciq.xpil._3.FreeTextLines;

import org.mitre.cybox.common_2.DateTimeWithPrecisionType;
import org.mitre.cybox.common_2.HashListType;
import org.mitre.cybox.common_2.HashType;
import org.mitre.cybox.common_2.SimpleHashValueType;
import org.mitre.cybox.common_2.TimeType;
import org.mitre.cybox.cybox_2.ObjectType;
import org.mitre.cybox.cybox_2.ObservableType;
import org.mitre.cybox.default_vocabularies_2.HashNameVocab10;
import org.mitre.cybox.objects.FileObjectType;
import org.mitre.stix.common_1.IndicatorBaseType;
import org.mitre.stix.common_1.InformationSourceType;
import org.mitre.stix.common_1.StructuredTextType;
import org.mitre.stix.extensions.identity.CIQIdentity30InstanceType;
import org.mitre.stix.extensions.identity.STIXCIQIdentity30Type;
import org.mitre.stix.indicator_2.IndicatorType;
import org.mitre.stix.stix_1.IndicatorsType;
import org.mitre.stix.stix_1.STIXHeaderType;
import org.mitre.stix.stix_1.STIXType;
import org.mitre.stix.stix_1.ObjectFactory;
import org.mitre.stix.util.Utilities;

/**
 * An example of how to add CIQ Identity information to a STIX Indicator.
 * 
 * Same as {@link https
 * ://raw.githubusercontent.com/STIXProject/python-stix/master
 * /examples/ciq_identity.py}.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class CIQIdentity {

	public CIQIdentity() {
	}

	/**
	 * @param args
	 * @throws DatatypeConfigurationException
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 */
	@SuppressWarnings("serial")
	public static void main(String[] args)
			throws DatatypeConfigurationException, JAXBException,
			ParserConfigurationException {

		// Get time for now.
		XMLGregorianCalendar now = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(
						new GregorianCalendar(TimeZone.getTimeZone("UTC")));

		ContactNumbers contactNumbers = new ContactNumbers()
				.withContactNumber(new ArrayList<ContactNumbers.ContactNumber>() {
					{
						add(new ContactNumbers.ContactNumber()
								.withContactNumberElement(new ArrayList<ContactNumbers.ContactNumber.ContactNumberElement>() {
									{
										add(new ContactNumbers.ContactNumber.ContactNumberElement()
												.withValue("555-555-5555"));
										add(new ContactNumbers.ContactNumber.ContactNumberElement()
												.withValue("555-555-5556"));
									}
								}));
					}
				});

		ElectronicAddressIdentifiers electronicAddressIdentifiers = new ElectronicAddressIdentifiers()
				.withElectronicAddressIdentifier(new ArrayList<ElectronicAddressIdentifiers.ElectronicAddressIdentifier>() {
					{
						add(new ElectronicAddressIdentifiers.ElectronicAddressIdentifier()
								.withValue("jsmith@example.com"));
					}
				});

		FreeTextLines freeTextLines = new FreeTextLines()
				.withFreeTextLine(new ArrayList<FreeTextLines.FreeTextLine>() {
					{
						add(new FreeTextLines.FreeTextLine()
								.withValue("Demonstrating Free Text!"));
					}
				});

		PartyNameType partyName = new PartyNameType()
				.withNameLine(new ArrayList<NameLine>() {
					{
						add(new NameLine().withValue("Foo"));
						add(new NameLine().withValue("Bar"));
					}
				}).withPersonName(new ArrayList<PersonName>() {
					{
						add(new PersonName()
								.withNameElement(new oasis.names.tc.ciq.xnl._3.PersonNameType.NameElement()
										.withValue("John Smith")));
						add(new PersonName()
								.withNameElement(new oasis.names.tc.ciq.xnl._3.PersonNameType.NameElement()
										.withValue("Jill Smith")));
					}
				}).withOrganisationName(new ArrayList<OrganisationName>() {
					{
						add(new OrganisationName()
								.withNameElement(new oasis.names.tc.ciq.xnl._3.OrganisationNameType.NameElement()
										.withValue("Foo Inc.")));
						add(new OrganisationName()
								.withNameElement(new oasis.names.tc.ciq.xnl._3.OrganisationNameType.NameElement()
										.withValue("Bar Corp.")));
					}
				});

		STIXCIQIdentity30Type specification = new STIXCIQIdentity30Type()
				.withContactNumbers(contactNumbers)
				.withElectronicAddressIdentifiers(electronicAddressIdentifiers)
				.withFreeTextLines(freeTextLines).withPartyName(partyName);

		CIQIdentity30InstanceType identity = new CIQIdentity30InstanceType()
				.withSpecification(specification);

		InformationSourceType producer = new InformationSourceType()
				.withDescription(
						new StructuredTextType(
								"An indicator containing a File observable with an associated hash",
								null))
				.withTime(
						new TimeType()
								.withProducedTime(new DateTimeWithPrecisionType(
										now, null))).withIdentity(identity);

		FileObjectType fileObject = new org.mitre.cybox.objects.FileObjectType()
				.withHashes(new HashListType(new ArrayList<HashType>() {
					{
						add(new HashType()
								.withType(
										new HashNameVocab10().withValue("MD5"))
								.withSimpleHashValue(
										new SimpleHashValueType()
												.withValue("4EC0027BEF4D7E1786A04D021FA8A67F")));
					}
				}));

		ObjectType obj = new ObjectType().withProperties(fileObject);

		ObservableType observable = new org.mitre.cybox.cybox_2.ObservableType();
		observable.setObject(obj);

		final IndicatorType indicator = new IndicatorType()
				.withTitle("File Hash Example")
				.withDescription(
						new StructuredTextType(
								"An indicator containing a File observable with an associated hash",
								null)).withProducer(producer)
				.withObservable(observable);

		IndicatorsType indicators = new IndicatorsType(
				new ArrayList<IndicatorBaseType>() {
					{
						add(indicator);
					}
				});
		
		STIXHeaderType header = new STIXHeaderType()
				.withDescription(new StructuredTextType("Example", null));

		STIXType stixType = new STIXType()
				.withSTIXHeader(header)
				.withIndicators(indicators)
				.withVersion("1.1.1")
				.withTimestamp(now)
				.withId(new QName("http://example.com/", "package-"
						+ UUID.randomUUID().toString(), "example"));
		
		ObjectFactory factory = new ObjectFactory();
		
		JAXBElement<STIXType> stixPackage = factory.createSTIXPackage(stixType);
		
		System.out.println(Utilities.getXMLString(stixPackage));
	}
}

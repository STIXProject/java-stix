/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

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

import org.apache.commons.lang.StringUtils;
import org.mitre.cybox.common_2.DateTimeWithPrecisionType;
import org.mitre.cybox.common_2.HashListType;
import org.mitre.cybox.common_2.HashType;
import org.mitre.cybox.common_2.SimpleHashValueType;
import org.mitre.cybox.common_2.TimeType;
import org.mitre.cybox.cybox_2.ObjectType;
import org.mitre.cybox.cybox_2.Observable;
import org.mitre.cybox.default_vocabularies_2.HashNameVocab10;
import org.mitre.cybox.objects.FileObjectType;
import org.mitre.stix.common_1.IndicatorBaseType;
import org.mitre.stix.common_1.InformationSourceType;
import org.mitre.stix.common_1.StructuredTextType;
import org.mitre.stix.extensions.identity.CIQIdentity30InstanceType;
import org.mitre.stix.extensions.identity.STIXCIQIdentity30Type;
import org.mitre.stix.indicator_2.Indicator;
import org.mitre.stix.stix_1.IndicatorsType;
import org.mitre.stix.stix_1.STIXHeaderType;
import org.mitre.stix.stix_1.STIXPackage;
import org.xml.sax.SAXException;

/**
 * An example of how to add CIQ Identity information to a STIX Indicator.
 * 
 * See <a href=
 * "https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/ciq_identity.py"
 * >https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/
 * ciq_identity.py</a>
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
	public static void main(String[] args) {

		try {
			// Get time for now.
			XMLGregorianCalendar now = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(
							new GregorianCalendar(TimeZone.getTimeZone("UTC")));

			ContactNumbers contactNumbers = new ContactNumbers()
					.withContactNumbers(new ContactNumbers.ContactNumber()
							.withContactNumberElements(
									new ContactNumbers.ContactNumber.ContactNumberElement()
											.withValue("555-555-5555"),
									new ContactNumbers.ContactNumber.ContactNumberElement()
											.withValue("555-555-5556")));

			ElectronicAddressIdentifiers electronicAddressIdentifiers = new ElectronicAddressIdentifiers()
					.withElectronicAddressIdentifiers(new ElectronicAddressIdentifiers.ElectronicAddressIdentifier()
							.withValue("jsmith@example.com"));

			FreeTextLines freeTextLines = new FreeTextLines()
					.withFreeTextLines(new FreeTextLines.FreeTextLine()
							.withValue("Demonstrating Free Text!"));

			PartyNameType partyName = new PartyNameType()
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
											.withValue("Bar Corp.")));

			STIXCIQIdentity30Type specification = new STIXCIQIdentity30Type()
					.withContactNumbers(contactNumbers)
					.withElectronicAddressIdentifiers(
							electronicAddressIdentifiers)
					.withFreeTextLines(freeTextLines).withPartyName(partyName);

			CIQIdentity30InstanceType identity = new CIQIdentity30InstanceType()
					.withSpecification(specification);

			InformationSourceType producer = new InformationSourceType()
					.withDescription(
							new StructuredTextType()
									.withValue("An indicator containing a File observable with an associated hash"))
					.withTime(
							new TimeType()
									.withProducedTime(new DateTimeWithPrecisionType(
											now, null))).withIdentity(identity);

			FileObjectType fileObject = new FileObjectType()
					.withHashes(new HashListType(new ArrayList<HashType>() {
						{
							add(new HashType()
									.withType(
											new HashNameVocab10()
													.withValue("MD5"))
									.withSimpleHashValue(
											new SimpleHashValueType()
													.withValue("4EC0027BEF4D7E1786A04D021FA8A67F")));
						}
					}));

			ObjectType obj = new ObjectType().withProperties(fileObject)
					.withId(new QName("http://example.com/", "file-"
							+ UUID.randomUUID().toString(), "example"));

			Observable observable = new Observable().withId(new QName(
					"http://example.com/", "observable-"
							+ UUID.randomUUID().toString(), "example"));

			observable.setObject(obj);

			final Indicator indicator = new Indicator()
					.withId(new QName("http://example.com/", "indicator-"
							+ UUID.randomUUID().toString(), "example"))
					.withTimestamp(now)
					.withTitle("File Hash Example")
					.withDescription(
							new StructuredTextType()
									.withValue("An indicator containing a File observable with an associated hash"))
					.withProducer(producer).withObservable(observable);

			IndicatorsType indicators = new IndicatorsType(
					new ArrayList<IndicatorBaseType>() {
						{
							add(indicator);
						}
					});

			STIXHeaderType header = new STIXHeaderType()
					.withDescription(new StructuredTextType()
							.withValue("Example"));

			STIXPackage stixPackage = new STIXPackage()
					.withSTIXHeader(header)
					.withIndicators(indicators)
					.withVersion("1.1.1")
					.withTimestamp(now)
					.withId(new QName("http://example.com/", "package-"
							+ UUID.randomUUID().toString(), "example"));

			System.out.println(stixPackage.toXMLString(true));

			System.out.println(StringUtils.repeat("-", 120));

			System.out.println("Validates: " + stixPackage.validate());

			System.out.println(StringUtils.repeat("-", 120));

			System.out.println(STIXPackage.fromXMLString(
					stixPackage.toXMLString()).toXMLString(true));

		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}

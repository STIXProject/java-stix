/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

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
import org.mitre.stix.common_1.IdentityType;
import org.mitre.stix.common_1.IndicatorBaseType;
import org.mitre.stix.common_1.InformationSourceType;
import org.mitre.stix.common_1.StructuredTextType;
import org.mitre.stix.indicator_2.Indicator;
import org.mitre.stix.stix_1.IndicatorsType;
import org.mitre.stix.stix_1.STIXHeaderType;
import org.mitre.stix.stix_1.STIXPackage;
import org.xml.sax.SAXException;

/**
 * Build a STIX Indicator document containing a File observable with an
 * associated hash.
 * 
 * See <a href=
 * "https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/indicator-hash.py"
 * >https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/
 * indicator-hash.py</a>
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class IndicatorHash {

	public IndicatorHash() {
	}

	@SuppressWarnings("serial")
	public static void main(String[] args) {

		try {
			// Get time for now.
			XMLGregorianCalendar now = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(
							new GregorianCalendar(TimeZone.getTimeZone("UTC")));

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

			IdentityType identity = new IdentityType()
					.withName("The MITRE Corporation");

			InformationSourceType producer = new InformationSourceType()
					.withIdentity(identity)
					.withTime(
							new TimeType()
									.withProducedTime(new DateTimeWithPrecisionType(
											now, null)));

			final Indicator indicator = new Indicator()
					.withId(new QName("http://example.com/", "indicator-"
							+ UUID.randomUUID().toString(), "example"))
					.withTimestamp(now)
					.withTitle("File Hash Example")
					.withDescriptions(
							new StructuredTextType()
									.withValue("An indicator containing a File observable with an associated hash"))
					.withObservable(observable).withProducer(producer);

			IndicatorsType indicators = new IndicatorsType(
					new ArrayList<IndicatorBaseType>() {
						{
							add(indicator);
						}
					});

			STIXHeaderType stixHeader = new STIXHeaderType()
					.withDescriptions(new StructuredTextType()
							.withValue("Example"));

			STIXPackage stixPackage = new STIXPackage()
					.withSTIXHeader(stixHeader)
					.withIndicators(indicators)
					.withVersion("1.2")
					.withTimestamp(now)
					.withId(new QName("http://example.com/", "package-"
							+ UUID.randomUUID().toString(), "example"));

			System.out.println(stixPackage.toXMLString(true));

			System.out.println(StringUtils.repeat("-", 120));

			System.out.println("Validates: " + stixPackage.validate());

		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}

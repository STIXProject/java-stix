/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.mitre.cybox.common_2.ToolInformationType;
import org.mitre.cybox.common_2.ToolsInformationType;
import org.mitre.stix.common_1.InformationSourceType;
import org.mitre.stix.common_1.StructuredTextType;
import org.mitre.stix.stix_1.STIXHeaderType;
import org.mitre.stix.stix_1.STIXPackage;

/**
 * Build a STIX Document with Tool Information
 * 
 * See <a href=
 * "https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/creation_tool_metadata.py"
 * >https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/
 * creation_tool_metadata.py</a>
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class CreationToolMetadata {

	public CreationToolMetadata() {
	}

	public static void main(String[] args) {

		try {
			// Get time for now.
			XMLGregorianCalendar now = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(
							new GregorianCalendar(TimeZone.getTimeZone("UTC")));

			STIXHeaderType header = new STIXHeaderType()
					.withDescriptions(new StructuredTextType().withValue("Example"))
					.withInformationSource(
							new InformationSourceType().withTools(new ToolsInformationType()
									.withTools(new ToolInformationType()
											.withName(
													"org.mitre.stix.examples.CreationToolMetadata")
											.withVendor("The MITRE Corporation"))));

			STIXPackage stixPackage = new STIXPackage()
					.withSTIXHeader(header)
					.withVersion("1.1.1")
					.withTimestamp(now)
					.withId(new QName("http://example.com/", "package-"
							+ UUID.randomUUID().toString(), "example"));

			System.out.println(stixPackage.toXMLString(true));

			System.out.println(StringUtils.repeat("-", 120));

			System.out.println("Validates: " + stixPackage.validate());

		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}

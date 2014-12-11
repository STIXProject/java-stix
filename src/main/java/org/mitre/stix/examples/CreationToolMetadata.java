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

import org.mitre.cybox.common_2.ToolInformationType;
import org.mitre.cybox.common_2.ToolsInformationType;
import org.mitre.stix.common_1.InformationSourceType;
import org.mitre.stix.common_1.StructuredTextType;
import org.mitre.stix.stix_1.ObjectFactory;
import org.mitre.stix.stix_1.STIXHeaderType;
import org.mitre.stix.stix_1.STIXType;
import org.mitre.stix.util.Utilities;

/**
 * Build a STIX Document with Tool Information
 * 
 * Same as {@link https
 * ://raw.githubusercontent.com/STIXProject/python-stix/master
 * /examples/creation_tool_metadata.py}.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class CreationToolMetadata {

	/**
	 * 
	 */
	public CreationToolMetadata() {
	}

	/**
	 * @param args
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 * @throws DatatypeConfigurationException 
	 */
	@SuppressWarnings("serial")
	public static void main(String[] args) throws JAXBException,
			ParserConfigurationException, DatatypeConfigurationException {

		// Get time for now.
		XMLGregorianCalendar now = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(
						new GregorianCalendar(TimeZone.getTimeZone("UTC")));

		STIXHeaderType header = new STIXHeaderType().withDescription(
				new StructuredTextType("Example", null)).withInformationSource(
				new InformationSourceType()
						.withTools(new ToolsInformationType()
								.withTool(new ArrayList<ToolInformationType>() {
									{
										add(new ToolInformationType()
												.withName(
														"org.mitre.stix.examples.CreationToolMetadata")
												.withVendor(
														"The MITRE Corporation"));
									}
								})));

		STIXType stixType = new STIXType()
				.withSTIXHeader(header)
				.withVersion("1.1.1")
				.withTimestamp(now)
				.withId(new QName("http://example.com/", "package-" + UUID.randomUUID()
						.toString(), "example"));
		
		ObjectFactory factory = new ObjectFactory();
		
		JAXBElement<STIXType> stixPackage = factory.createSTIXPackage(stixType);
		
		System.out.println(Utilities.getXMLString(stixPackage));
	}

}

/**
 * Copyright (c) 2014, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mitre.cybox.common_2.ToolInformationType;
import org.mitre.cybox.common_2.ToolsInformationType;
import org.mitre.stix.common_1.InformationSourceType;
import org.mitre.stix.common_1.StructuredTextType;
import org.mitre.stix.stix_1.STIXHeaderType;
import org.mitre.stix.stix_1.STIXType;
import org.mitre.stix.utility.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

		STIXType stix = new STIXType()
				.withSTIXHeader(header)
				.withVersion("1.1.1")
				.withTimestamp(now)
				.withId(new QName("http://example.com/", UUID.randomUUID()
						.toString(), "example"));

		// Marshal it to a Document
		org.mitre.stix.stix_1.ObjectFactory factory = new org.mitre.stix.stix_1.ObjectFactory();
		JAXBContext jaxbContext = JAXBContext
				.newInstance("org.mitre.stix.stix_1");
		JAXBElement<STIXType> stixPackage = factory.createSTIXPackage(stix);

		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
		// marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
		// "http://my.namespace my.schema.xsd");
		marshaller.marshal(stixPackage, document);

		// Remove unused Namespace
		Utilities.removeUnusedNamespaces(document);

		// Add example xnlns to root as per
		// https://github.com/STIXProject/schemas/wiki/Suggested-Practices-%281.1%29#formatting-ids
		// for the id.
		Element root = document.getDocumentElement();
		root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:example",
				"http://example.com");

		// TODO: schemalocation needs to be added... may need to gather up this
		// cruft into a utility.

		// Pretty print to standard out
		System.out.println(Utilities.getStringFromDocument(document));
	}

}

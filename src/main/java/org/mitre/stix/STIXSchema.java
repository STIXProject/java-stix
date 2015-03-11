/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents the schema.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
public class STIXSchema {

	private static final Logger LOGGER = Logger.getLogger(STIXSchema.class
			.getName());

	private String version;

	private static STIXSchema instance;

	private Map<String, String> prefixSchemaBindings;

	private Validator validator;
	
	private javax.xml.validation.Schema schema;

	/**
	 * Returns a Schema for the requested schema version
	 * 
	 * @return
	 */
	public synchronized static STIXSchema getInstance() {

		if (instance != null) {
			return instance;
		} else {
			instance = new STIXSchema();
		}

		return instance;
	}

	/**
	 * Private constructor to permit one Schema per version.
	 * 
	 * @param version
	 */
	private STIXSchema() {

		// TODO Pull from model
		this.version = "1.1.1";

		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] schemaResources;

		try {
			schemaResources = patternResolver
					.getResources("classpath*:schemas/v1.1.1/**/*.xsd");

			prefixSchemaBindings = new HashMap<String, String>();

			String url, prefix, targetNamespace;
			Document schemaDocument;
			NamedNodeMap attributes;
			Node attribute;

			for (Resource resource : schemaResources) {

				url = resource.getURL().toString();

				schemaDocument = DocumentBuilderFactory.newInstance()
						.newDocumentBuilder().parse(resource.getInputStream());

				schemaDocument.getDocumentElement().normalize();

				attributes = schemaDocument.getDocumentElement()
						.getAttributes();

				for (int i = 0; i < attributes.getLength(); i++) {

					attribute = attributes.item(i);

					targetNamespace = schemaDocument.getDocumentElement()
							.getAttribute("targetNamespace");

					if (attribute.getNodeName().startsWith("xmlns:")
							&& attribute.getNodeValue().equals(targetNamespace)) {

						prefix = attributes.item(i).getNodeName().split(":")[1];

						if ((prefixSchemaBindings
								.containsKey(prefix))
								&& (prefixSchemaBindings.get(
										prefix).split(
										"schemas/v1.1.1/")[1]
										.startsWith("external"))) {

							continue;

						}	

						LOGGER.fine("     adding: " + prefix + " :: "
								+ url);

						prefixSchemaBindings.put(prefix, url);
					}
				}
			}
			
		    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		    
		    Source[] schemas = new Source[prefixSchemaBindings.values().size()];
		    
		    int i = 0;
		    for (String schemaLocation : prefixSchemaBindings.values()) {
		    	schemas[i++] = new StreamSource(schemaLocation); 		    	
		    }

		    schema = factory.newSchema(schemas);

		    validator = schema.newValidator();	
		    validator.setErrorHandler(new ValidationErrorHandler());

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the schema version
	 * 
	 * @return
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Validate XML text retrieved from URL
	 * 
	 * @param url
	 * @throws IOException
	 * @throws SAXException
	 */
	public boolean validate(URL url) {

		String xmlText = null;

		try {
			xmlText = new Scanner(url.openStream(), "UTF-8")
					.useDelimiter("\\A").next();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return validate(xmlText);
	}

	/**
	 * Validate XML text String
	 * 
	 * @param xmlText
	 * @throws SAXException
	 * @throws IOException
	 */
	public boolean validate(String xmlText) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		// remove schema hint as we have em

		try {
			DocumentBuilder b = factory.newDocumentBuilder();
			Document document = b.parse(new ByteArrayInputStream(xmlText
					.getBytes()));

			Element root = document.getDocumentElement();

			root.removeAttribute("xsi:schemaLocation");

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			StringWriter buffer = new StringWriter();
			transformer
					.transform(new DOMSource(root), new StreamResult(buffer));
			xmlText = buffer.toString();

		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
		    validator.validate(new StreamSource(new ByteArrayInputStream(xmlText
					.getBytes(StandardCharsets.UTF_8))));

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {

		STIXSchema schema = STIXSchema.getInstance();

		System.out
				.println(schema
						.validate(new URL(
								"https://raw.githubusercontent.com/STIXProject/schemas/master/samples/STIX_Domain_Watchlist.xml")));
	}

	public javax.xml.validation.Schema getSchema() {

		return schema;
	}
}

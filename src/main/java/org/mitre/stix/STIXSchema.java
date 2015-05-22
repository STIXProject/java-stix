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
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
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

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Gathers up the STIX schema useful for marshalling and unmarshalling, and
 * validation.
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
	 * Returns STIXSchema object representing the STIX schema.
	 * 
	 * @return Always returns a STIXSchema object representing the STIX schema.
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
	 * Private constructor to permit a single STIXSchema to exists.
	 */
	private STIXSchema() {

		this.version = ((Version) this.getClass().getPackage()
				.getAnnotation(Version.class)).schema();

		ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] schemaResources;

		try {
			schemaResources = patternResolver
					.getResources("classpath*:schemas/v" + version
							+ "/**/*.xsd");

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

						if ((prefixSchemaBindings.containsKey(prefix))
								&& (prefixSchemaBindings.get(prefix).split(
										"schemas/v" + version + "/")[1]
										.startsWith("external"))) {

							continue;

						}

						LOGGER.fine("     adding: " + prefix + " :: " + url);

						prefixSchemaBindings.put(prefix, url);
					}
				}
			}

			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

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
	 * @return The STIX schema version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Validate XML text retrieved from URL
	 * 
	 * @param url
	 *            The URL object for the XML to be validated.
	 */
	public boolean validate(URL url) {

		String xmlText = null;

		try {
			xmlText = IOUtils.toString(url.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return validate(xmlText);
	}

	/**
	 * Validate an XML text String against the STIX schema
	 * 
	 * @param xmlText
	 *            A string of XML text to be validated
	 */
	public boolean validate(String xmlText) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		// This section removes the schema hint as we have the schema docs
		// otherwise exceptions may be thrown

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
			validator.validate(new StreamSource(new ByteArrayInputStream(
					xmlText.getBytes(StandardCharsets.UTF_8))));

		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			return false;
		}

		return true;
	}

	/**
	 * Returns Schema object representing the STIX schema.
	 * 
	 * @return Always returns a non-null Schema object representing the STIX
	 *         schema.
	 */
	public javax.xml.validation.Schema getSchema() {

		return schema;
	}

	public static void main(String[] args) throws ParserConfigurationException,
			SAXException, IOException {

		STIXSchema schema = STIXSchema.getInstance();

		System.out
				.println(schema
						.validate(new URL(
								"https://raw.githubusercontent.com/STIXProject/python-stix/v1.2.0.0/examples/sample.xml")));
		
		System.out.println(schema.getVersion());
	}

	/**
	 * Return the namespace URI from the package for the class of the object.
	 * 
	 * @param obj
	 *            Expects a JAXB model object.
	 * @return Name of the XML namespace.
	 */
	public static String getNamespaceURI(Object obj) {

		Package pkg = obj.getClass().getPackage();

		XmlSchema xmlSchemaAnnotation = pkg.getAnnotation(XmlSchema.class);

		return xmlSchemaAnnotation.namespace();
	}

	/**
	 * Return the name from the JAXB model object.
	 * 
	 * @param obj
	 *            Expects a JAXB model object.
	 * @return element name
	 */
	public static String getName(Object obj) {
		try {
			return obj.getClass().getAnnotation(
					XmlRootElement.class).name();
		} catch (NullPointerException e) {
			return obj.getClass().getAnnotation(
					XmlType.class).name();
		}
	}

	/**
	 * Return the QualifiedNam from the JAXB model object.
	 * 
	 * @param obj
	 *            Expects a JAXB model object.
	 * @return Qualified dName as defined by JAXB model
	 */
	public static QName getQualifiedName(Object obj) {
		return new QName(STIXSchema.getNamespaceURI(obj),
				STIXSchema.getName(obj));
	}
}

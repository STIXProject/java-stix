/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A collection of utility helper methods.
 *
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
public class DocumentUtilities {

	private static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String XML_NAMESPACE = "http://www.w3.org/2000/xmlns/";

	private static final JAXBContext STIX_CONTEXT = initDefaultContext();

	private static JAXBContext initDefaultContext(){
		try {
			return JAXBContext.newInstance("org.mitre.stix.stix_1");
		} catch(JAXBException e) {
			throw new RuntimeException("Exception initializing default JAXBContext" , e);
		}
	}

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger
			.getLogger(DocumentUtilities.class.getName());

	/**
	 * Returns a pretty printed String for a JAXBElement
	 *
	 * @param jaxbElement
	 *            JAXB representation of an Xml Element to be printed.
	 * @return String containing the XML mark-up.
	 */
	public static String toXMLString(JAXBElement<?> jaxbElement) {
		return toXMLString(jaxbElement, true);
	}

	/**
	 * Returns Document that is not formatted for a JAXBElement
	 *
	 * @param jaxbElement
	 *            JAXB representation of an XML Element
	 * @return Document.
	 */
	public static Document toDocument(JAXBElement<?> jaxbElement) {
		return toDocument(jaxbElement, false);
	}

	/**
	 * Returns a Document for a JAXBElement
	 *
	 * @param jaxbElement
	 *            JAXB representation of an XML Element
	 * @param prettyPrint
	 *            True for pretty print, otherwise false
	 * @return The Document representation
	 */
	public static Document toDocument(JAXBElement<?> jaxbElement,
			boolean prettyPrint) {

		Document document = null;

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setIgnoringElementContentWhitespace(true);
			documentBuilderFactory.isIgnoringComments();
			documentBuilderFactory.setCoalescing(true);

			document = documentBuilderFactory.newDocumentBuilder()
					.newDocument();

			String packName = jaxbElement.getDeclaredType().getPackage().getName();
			JAXBContext jaxbContext;
			if (packName.startsWith("org.mitre")){
				jaxbContext = STIX_CONTEXT;
			} else {
				jaxbContext = JAXBContext.newInstance(packName);
			}

			Marshaller marshaller = jaxbContext.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					prettyPrint);

			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			try {
				marshaller.marshal(jaxbElement, document);
			} catch (JAXBException e) {
				// otherwise handle non-XMLRootElements
				QName qualifiedName = new QName(
						STIXSchema.getNamespaceURI(jaxbElement), jaxbElement
								.getClass().getSimpleName());

				@SuppressWarnings({ "rawtypes", "unchecked" })
				JAXBElement root = new JAXBElement(qualifiedName,
						jaxbElement.getClass(), jaxbElement);

				marshaller.marshal(root, document);
			}

			removeUnusedNamespaces(document);

		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

		return document;
	}

	/**
	 * Returns a String for a JAXBElement
	 *
	 * @param jaxbElement
	 *            JAXB representation of an XML Element to be printed.
	 * @param prettyPrint
	 *            True for pretty print, otherwise false
	 * @return String containing the XML mark-up.
	 */
	public static String toXMLString(JAXBElement<?> jaxbElement,
			boolean prettyPrint) {

		Document document = toDocument(jaxbElement);

		return toXMLString(document, prettyPrint);

	}

	/**
	 * Returns String that is not formatted for a Document object representing the
	 * entire XML document.
	 *
	 * @param document
	 *            Document object representing the entire XML document
	 *
	 * @return Pretty printed String containing the XML mark-up.
	 */
	public static String toXMLString(Document document) {
		return toXMLString(document, false);
	}

	/**
	 * Returns a String for a Document object representing the entire XML
	 * document.
	 *
	 * @param document
	 *            Document object representing the entire XML document
	 * @param prettyPrint
	 *            True for pretty print, otherwise false
	 *
	 * @return String containing the XML mark-up.
	 */
	public static String toXMLString(Document document, boolean prettyPrint) {

		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			LSSerializer serializaer = domImplementationLS.createLSSerializer();

			if (prettyPrint) {
				serializaer.getDomConfig().setParameter("format-pretty-print",
						Boolean.TRUE);
				serializaer.getDomConfig().setParameter("xml-declaration",
						Boolean.TRUE);
			}

			// otherwise UTF-16 is used by default
			LSOutput lsOutput = domImplementationLS.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			lsOutput.setByteStream(byteStream);

			serializaer.write(document, lsOutput);

			return new String(byteStream.toByteArray(), "UTF-8");

		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private interface ElementVisitor {

		void visit(Element element);

	}

	/**
	 * Used to traverse an XML document.
	 *
	 * @param element
	 *            Represents an element in an XML document.
	 * @param visitor
	 *            Code to be executed.
	 *
	 */
	private final static void traverse(Element element, ElementVisitor visitor) {

		visitor.visit(element);

		NodeList children = element.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			traverse((Element) node, visitor);
		}
	}

	/**
	 * JAXB adds all namespaces known by the JAXBContext to the root element of
	 * the XML document for performance reasons as per JAXB-103
	 * (http://java.net/
	 * jira/browse/JAXB-103?focusedCommentId=64411&page=com.atlassian
	 * .jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_64411).
	 *
	 * This helper method based on Reboot's
	 * (http://stackoverflow.com/users/392730/reboot) response to a
	 * stackoverflow question on the subject. I've modified it slightly, but it
	 * will prune down the namespaces to only those used.
	 *
	 * @param document
	 *            Document object representing the entire XML document
	 */
	public static void removeUnusedNamespaces(Document document) {

		final Set<String> namespaces = new HashSet<String>();

		document.normalizeDocument();

		Element element = document.getDocumentElement();

		traverse(element, new ElementVisitor() {

			public void visit(Element element) {

				String namespace = element.getNamespaceURI();

				if (namespace == null)
					namespace = "";

				namespaces.add(namespace);

				NamedNodeMap attributes = element.getAttributes();

				for (int i = 0; i < attributes.getLength(); i++) {
					Node attribute = attributes.item(i);

					if (XML_NAMESPACE.equals(attribute.getNamespaceURI())) {
						continue;
					}

					String prefix;

					if (XML_SCHEMA_INSTANCE.equals(attribute.getNamespaceURI())) {
						if ("type".equals(attribute.getLocalName())) {
							String value = attribute.getNodeValue();

							if (value.contains(":")) {
								prefix = value.substring(0, value.indexOf(":"));
							} else {
								prefix = null;
							}
						} else {
							continue;
						}
					} else {
						String value = attribute.getNodeValue();

						if (value.contains(":")) {
							prefix = value.substring(0, value.indexOf(":"));
						} else {
							prefix = attribute.getPrefix();
						}
					}

					namespace = element.lookupNamespaceURI(prefix);

					if (namespace == null) {
						continue;
					}

					namespaces.add(namespace);
				}
			}

		});

		traverse(element, new ElementVisitor() {

			public void visit(Element element) {

				Set<String> removeLocalNames = new HashSet<String>();

				NamedNodeMap attributes = element.getAttributes();

				for (int i = 0; i < attributes.getLength(); i++) {
					Node attribute = attributes.item(i);

					if (!XML_NAMESPACE.equals(attribute.getNamespaceURI())) {
						continue;
					}

					if (namespaces.contains(attribute.getNodeValue())) {
						continue;
					}

					removeLocalNames.add(attribute.getLocalName());
				}

				for (String localName : removeLocalNames) {
					element.removeAttributeNS(XML_NAMESPACE, localName);
				}
			}

		});
	}

	/**
	 * Creates a Document from XML String
	 *
	 * @param xml
	 *            The XML String
	 * @return The Document representation
	 */
	public static Document toDocument(String xml) {
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setIgnoringElementContentWhitespace(true);
			documentBuilderFactory.isIgnoringComments();
			documentBuilderFactory.setCoalescing(true);

			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();

			InputSource inputSource = new InputSource();

			inputSource.setCharacterStream(new StringReader(xml));

			Document document = documentBuilder.parse(inputSource);

			removeUnusedNamespaces(document);

			return document;

		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Strips formatting from an XML String
	 *
	 * @param xml
	 *            The XML String to reformatted
	 * @return The XML String as on line.
	 */
	public static String stripFormattingfromXMLString(String xml) {
		try {
			Document document = DocumentUtilities.toDocument(xml);

			DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			LSSerializer serializaer = domImplementationLS.createLSSerializer();

			serializaer.getDomConfig().setParameter("format-pretty-print",
					Boolean.FALSE);

			serializaer.getDomConfig().setParameter("xml-declaration",
					Boolean.TRUE);

			// otherwise UTF-16 is used by default
			LSOutput lsOutput = domImplementationLS.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			lsOutput.setByteStream(byteStream);

			serializaer.write(document, lsOutput);

			return new String(byteStream.toByteArray(), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (ClassCastException e) {
			throw new RuntimeException(e);
		}
	}
}

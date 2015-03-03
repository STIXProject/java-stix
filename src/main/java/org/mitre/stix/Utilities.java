/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
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

/**
 * A collection of utility helper methods.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
public class Utilities {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Utilities.class
			.getName());
	
	private static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String XML_NAMESPACE = "http://www.w3.org/2000/xmlns/";
	
	private interface ElementVisitor {

		void visit(Element element);

	}

	/**
	 * Used to transverse an XML document.
	 * 
	 * @param element
	 * @param visitor
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
	 * Returns a String for a JAXBElement
	 * 
	 * @param jaxbElement
	 * @return
	 */
	public static String getXMLString(JAXBElement<?> jaxbElement) {
		return getXMLString(jaxbElement, true);
	}
	
	/**
	 * Returns a String for a JAXBElement
	 * 
	 * @param jaxbElement
	 * @param prettyPrint
	 * @return
	 */
	public static String getXMLString(JAXBElement<?> jaxbElement, boolean prettyPrint) {
		
		try {
	        Document document = DocumentBuilderFactory
	                .newInstance().newDocumentBuilder().newDocument();
			
	        JAXBContext jaxbContext = JAXBContext
	                .newInstance(jaxbElement.getDeclaredType().getPackage().getName());
	
	        Marshaller marshaller = jaxbContext.createMarshaller();
	
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
	                true);
	
	        marshaller
	                .setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	
	        try {
	            marshaller.marshal(jaxbElement, document);
	        } catch (JAXBException e) {
	            // otherwise handle non-XMLRootElements
	            QName qualifiedName = new QName(
	                    Utilities.getnamespaceURI(jaxbElement), jaxbElement.getClass().getSimpleName());
	
	            @SuppressWarnings({ "rawtypes", "unchecked" })
	            JAXBElement root = new JAXBElement(
	                    qualifiedName, jaxbElement.getClass(), jaxbElement);
	
	            marshaller.marshal(root, document);
	        }
	
	        Utilities.removeUnusedNamespaces(document);
	
	        return Utilities.getXMLString(document, prettyPrint);
        
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a String for a Document.
	 * 
	 * @param document
	 * @return
	 */
	public static String getXMLString(Document document) {
		return getXMLString(document, true);
	}
	
	/**
	 * Returns a String for a Document.
	 * 
	 * @param document
	 * @param prettyPrint
	 * @return
	 */
	public static String getXMLString(Document document, boolean prettyPrint) {

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

	/**
	 * JAXB adds all namespaces known by the JAXBContext to the root element of
	 * the XML document for performance reasons as per JAXB-103
	 * (http://java.net/
	 * jira/browse/JAXB-103?focusedCommentId=64411&page=com.atlassian
	 * .jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_64411).
	 * 
	 * This helper method based on Reboot's
	 * (http://stackoverflow.com/users/392730/reboot) response to a
	 * stackoverflow question on the subject will prune down the namespaces to
	 * only those used.
	 * 
	 * @param document
	 * @param class1 
	 * @param string 
	 */
	public static void removeUnusedNamespaces(Document document) {
				
		final Set<String> namespaces = new HashSet<String>();

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
	 * Pull the namespace URI from the package for the class of the object.
	 * 
	 * @param obj
	 * @return
	 */
	public static String getnamespaceURI(Object obj) {

		Package pkg = obj.getClass().getPackage();

		XmlSchema xmlSchemaAnnotation = pkg.getAnnotation(XmlSchema.class);

		return xmlSchemaAnnotation.namespace();
	}
}

package org.mitre.stix.utility;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

/**
 * A collection of utility helper methods.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
public class Utilities {

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
	 * Returns a String for a Document.
	 * 
	 * @param document
	 * @return
	 */
	public static String getStringFromDocument(Document document) {
		return getStringFromDocument(document, true);
	}

	/**
	 * Returns a String for a Document.
	 * 
	 * @param document
	 * @param prettyPrint
	 * @return
	 */
	public static String getStringFromDocument(Document document,
			boolean prettyPrint) {
		try {
			DOMSource domSource = new DOMSource(document);
			StreamResult result = new StreamResult(new StringWriter());

			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			if (prettyPrint) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");
			}

			transformer.transform(domSource, result);

			return result.getWriter().toString();

		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
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

					if (XML_NAMESPACE.equals(attribute.getNamespaceURI()))
						continue;

					String prefix;

					if (XML_SCHEMA_INSTANCE.equals(attribute.getNamespaceURI())) {
						if ("type".equals(attribute.getLocalName())) {
							String value = attribute.getNodeValue();

							if (value.contains(":"))
								prefix = value.substring(0, value.indexOf(":"));
							else
								prefix = null;
						} else {
							continue;
						}
					} else {
						prefix = attribute.getPrefix();
					}

					namespace = element.lookupNamespaceURI(prefix);

					if (namespace == null)
						namespace = "";

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

					if (!XML_NAMESPACE.equals(attribute.getNamespaceURI()))
						continue;

					if (namespaces.contains(attribute.getNodeValue()))
						continue;

					removeLocalNames.add(attribute.getLocalName());
				}

				for (String localName : removeLocalNames) {
					element.removeAttributeNS(XML_NAMESPACE, localName);
				}
			}

		});
	}
}

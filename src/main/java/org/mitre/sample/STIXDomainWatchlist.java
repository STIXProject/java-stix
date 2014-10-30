package org.mitre.sample;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.mitre.stix.stix_1.STIXType;
import org.mitre.stix.utility.Utilities;
import org.w3c.dom.Document;

/**
 * Reads in https://raw.githubusercontent.com/STIXProject/schemas/master/samples/STIX_Domain_Watchlist.xml
 * into the JAXB Document Model and then marshals it back out to XML.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class STIXDomainWatchlist {

	public STIXDomainWatchlist() {
	}

	public static void main(String[] args) throws JAXBException, IOException,
			ParserConfigurationException {
		JAXBContext jaxbContext = JAXBContext
				.newInstance("org.mitre.stix.stix_1");

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		URL url = new URL(
				"https://raw.githubusercontent.com/STIXProject/schemas/master/samples/STIX_Domain_Watchlist.xml");
		InputStream inputStream = url.openStream();

		JAXBElement<STIXType> stixType = unmarshaller.unmarshal(
				new StreamSource(inputStream), STIXType.class);
		inputStream.close();

		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();

		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.marshal(stixType, document);

		Utilities.removeUnusedNamespaces(document);
		
		System.out.println(Utilities.getStringFromDocument(document));
	}
}
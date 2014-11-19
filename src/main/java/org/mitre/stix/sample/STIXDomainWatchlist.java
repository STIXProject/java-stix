package org.mitre.stix.sample;

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mitre.stix.stix_1.STIXType;

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
		
		URL url = new URL("https://raw.githubusercontent.com/STIXProject/schemas/master/samples/STIX_Domain_Watchlist.xml");
		STIXType stix = STIXType.fromXML(url);
		
		System.out.println(stix.toXML());
	}
}
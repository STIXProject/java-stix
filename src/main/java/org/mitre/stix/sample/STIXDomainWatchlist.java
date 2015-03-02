/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.sample;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.mitre.stix.stix_1.STIXPackage;
import org.mitre.stix.util.Schema;

/**
 * Reads in
 * https://raw.githubusercontent.com/STIXProject/schemas/master/samples/
 * STIX_Domain_Watchlist.xml into the JAXB Document Model and then marshals it
 * back out to XML.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class STIXDomainWatchlist {

	public STIXDomainWatchlist() {
	}

	public static void main(String[] args) throws JAXBException, IOException,
			ParserConfigurationException {

		@SuppressWarnings("resource")
		String text = new Scanner(
				new URL(
						"https://raw.githubusercontent.com/STIXProject/schemas/master/samples/STIX_Domain_Watchlist.xml")
						.openStream(), "UTF-8").useDelimiter("\\A").next();

		STIXPackage stixPackage = STIXPackage.fromXMLString(text);
		
		System.out.println(stixPackage.toXMLString());
		
		System.out.println(Schema.getInstance().validate(stixPackage.toXMLString()));
	
	}
}
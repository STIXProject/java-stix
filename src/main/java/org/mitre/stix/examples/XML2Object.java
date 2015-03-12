/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import org.apache.commons.lang.StringUtils;
import org.mitre.stix.stix_1.STIXPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * Round-trip example. This code takes a STIX instance document from XML to
 * a binding object, then XML string, and then back into an binding object, which 
 * is then used to generate an XML string.
 * 
 * Same as {@link https
 * ://raw.githubusercontent.com/STIXProject/python-stix/master
 * /examples/xml2object.py}.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class XML2Object {

	public XML2Object() {

	}

	public static void main(String[] args) {
		
		try {
			URL url = XML2Object.class.getClass().getResource("/org/mitre/stix/examples/sample.xml");
			File file = new File(url.toURI());
			
			@SuppressWarnings("resource")
			String text = new Scanner(file).useDelimiter("\\A").next();
	
			STIXPackage stixPackage = STIXPackage.fromXMLString(text);
			
			text = stixPackage.toXMLString();
			
			System.out.println(text);
			
			System.out.println(StringUtils.repeat("-", 120));
			
			STIXPackage stixPackageTwo = STIXPackage.fromXMLString(text);
			
			System.out.println(stixPackageTwo.toXMLString());
			
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

	}
}

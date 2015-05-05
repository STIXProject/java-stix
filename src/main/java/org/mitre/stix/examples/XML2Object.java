/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.mitre.stix.stix_1.STIXPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Round-trip example. This code takes a STIX instance document from XML to a
 * binding object, then XML string, and then back into an binding object, which
 * is then used to generate an XML string.
 * 
 * See <a href=
 * "https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/xml2object.py"
 * >https://raw.githubusercontent.com/STIXProject/python-stix/master/examples/
 * xml2object.py</a>
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class XML2Object {

	public XML2Object() {

	}

	public static void main(String[] args) {

		try {
			URL url = XML2Object.class.getClass().getResource(
					"/org/mitre/stix/examples/sample.xml");
			File file = new File(url.toURI());

			String text = FileUtils.readFileToString(file);

			STIXPackage stixPackage = STIXPackage.fromXMLString(text);

			text = stixPackage.toXMLString(true);

			System.out.println(text);

			System.out.println(StringUtils.repeat("-", 120));

			STIXPackage stixPackageTwo = STIXPackage.fromXMLString(text);

			System.out.println(stixPackageTwo.toXMLString(true));

		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}

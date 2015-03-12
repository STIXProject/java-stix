/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix.examples;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.mitre.stix.common_1.IndicatorBaseType;
import org.mitre.stix.indicator_2.Indicator;
import org.mitre.stix.stix_1.STIXPackage;

/**
 * Prints indicator, observable, object count from a STIXPackage object
 * 
 * Same as {@link https
 * ://raw.githubusercontent.com/STIXProject/python-stix/master
 * /examples/xml_parser.py}.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
public class XMLParser {

	public XMLParser() {

	}

	public static void main(String[] args) {

		File file = null;

		if (args.length > 0) {
			file = new File(args[0]);
		} else {
			try {
				URL url = XML2Object.class.getClass().getResource(
						"/org/mitre/stix/examples/sample.xml");
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}

		try {

			STIXPackage stixPackage = STIXPackage.fromXMLString(FileUtils
					.readFileToString(file));

			int indicatorCount = 0, observablesCount = 0, objectCount = 0;

			if (stixPackage.getIndicators() != null) {
				if (stixPackage.getIndicators().getIndicators() != null) {
					List<IndicatorBaseType> indicators = stixPackage
							.getIndicators().getIndicators();

					indicatorCount = indicators.size();

					for (int i = 0; i < indicatorCount; i++) {

						Indicator indicator = (Indicator) indicators.get(i);

						if (indicator.getObservable() != null) {
							observablesCount++;
							if (indicator.getObservable().getObject() != null) {
								objectCount++;
							}
						}
					}
				}
			}

			System.out.format("Indicators: %d%n", indicatorCount);
			System.out.format("Observables: %d%n", observablesCount);
			System.out.format("Objects: %d%n", objectCount);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

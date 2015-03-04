package org.mitre.stix.examples;

import org.mitre.stix.stix_1.STIXPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class Xml2Object {

	public Xml2Object() {

	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
	
		
		try {
			URL url = Xml2Object.class.getClass().getResource("/org/mitre/stix/examples/sample.xml");
			File file = new File(url.toURI());
			
			String text = new Scanner(file).useDelimiter("\\A").next();
	
			STIXPackage stixPackage = STIXPackage.fromXMLString(text);
			
			System.out.println(stixPackage.toXMLString());
			
			STIXPackage stixPackageTwo = STIXPackage.fromXMLString(stixPackage.toXMLString());
			
			System.out.println(stixPackageTwo.toXMLString());
			
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}

	}
}

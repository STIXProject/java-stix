/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix;

import javax.xml.bind.ValidationEvent;

public class ValidationEventHandler implements javax.xml.bind.ValidationEventHandler {
	/* (non-Javadoc)
	 * @see javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.ValidationEvent)
	 */
	public boolean handleEvent(ValidationEvent event) {
		System.out.println("");
		System.out.println("EventT");
		System.out.println("\tSeverity: " + event.getSeverity());
		System.out.println("\tMessage: " + event.getMessage());
		System.out.println("\tLinked Excpetion: " + event.getLinkedException());
		System.out.println("\tLocator");
		System.out.println("\tLine Number: "
				+ event.getLocator().getLineNumber());
		System.out.println("\tColumn Number: "
				+ event.getLocator().getColumnNumber());
		System.out.println("\tOffset: " + event.getLocator().getOffset());
		System.out.println("\tObject: " + event.getLocator().getObject());
		System.out.println("\tNode: " + event.getLocator().getNode());
		System.out.println("\tURL: " + event.getLocator().getURL());
		return true;
	}
}
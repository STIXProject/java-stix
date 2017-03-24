/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix;

import javax.xml.bind.ValidationEvent;
import java.util.logging.Logger;

public class ValidationEventHandler implements javax.xml.bind.ValidationEventHandler {
	private static final Logger LOGGER = Logger.getLogger(ValidationErrorHandler.class.getName());

	/* (non-Javadoc)
	 * @see javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml.bind.ValidationEvent)
	 */
	public boolean handleEvent(ValidationEvent event) {
		LOGGER.info("");
		LOGGER.info("EventT");
		LOGGER.info("\tSeverity: " + event.getSeverity());
		LOGGER.info("\tMessage: " + event.getMessage());
		LOGGER.info("\tLinked Excpetion: " + event.getLinkedException());
		LOGGER.info("\tLocator");
		LOGGER.info("\tLine Number: "
				+ event.getLocator().getLineNumber());
		LOGGER.info("\tColumn Number: "
				+ event.getLocator().getColumnNumber());
		LOGGER.info("\tOffset: " + event.getLocator().getOffset());
		LOGGER.info("\tObject: " + event.getLocator().getObject());
		LOGGER.info("\tNode: " + event.getLocator().getNode());
		LOGGER.info("\tURL: " + event.getLocator().getURL());
		return true;
	}
}

/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

import java.util.logging.Logger;

/**
 * Parsing and validating error handler
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>) *
 */
public class ValidationErrorHandler implements ErrorHandler {
	private static final Logger LOGGER = Logger.getLogger(ValidationErrorHandler.class.getName());
	
	private void log(String type, SAXParseException e) {
		LOGGER.warning("SAXParseException " + type);
		LOGGER.warning("\tPublic ID: " + e.getPublicId());
		LOGGER.warning("\tSystem ID: " + e.getSystemId());
		LOGGER.warning("\tLine     : " + e.getLineNumber());
		LOGGER.warning("\tColumn   : " + e.getColumnNumber());
		LOGGER.warning("\tMessage  : " + e.getMessage());
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
	 */
	@Override
	public void error(SAXParseException e) throws SAXException {
		log("ERROR", e);
		throw e;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
	 */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		log("FATAL ERROR", e);
		throw e;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
	 */
	@Override
	public void warning(SAXParseException e) throws SAXException {
		log("WARNING", e);
		throw e;
	}
}

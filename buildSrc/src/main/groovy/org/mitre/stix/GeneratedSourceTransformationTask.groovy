/**
 * Copyright (c) 2015, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
package org.mitre.stix

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import groovy.io.FileType
import groovy.text.SimpleTemplateEngine

import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTParser
import org.eclipse.jdt.core.dom.CompilationUnit

import org.eclipse.jdt.core.dom.TypeDeclaration
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite
import org.eclipse.jdt.core.dom.rewrite.ListRewrite
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants

//import java.util.regex.Pattern

/**
 * Gradle Task used to perform syntactical analysis and transformations on the model 
 * (e.g., adding convenience methods)
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
class GeneratedSourceTransformationTask extends DefaultTask {
	
	GeneratedSourceTransformationTask() {
		description = "Perform syntactical analysis and transformations on the model (e.g., adding convenience methods)"
	}
	
	def lineSeperator = System.getProperty("line.separator")
	
	// Format src
	def format(src) {
		
		def options = DefaultCodeFormatterConstants.getEclipseDefaultSettings()
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5)
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5)
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5)
		
		def codeFormatter = ToolFactory.createCodeFormatter(options)
		
		def edit = codeFormatter.format(
				CodeFormatter.K_COMPILATION_UNIT,
				src,
				0,
				src.length(),
				0,
				lineSeperator
				)
		
		def document = new org.eclipse.jface.text.Document(src)
		
		try {
			edit.apply(document)
		} catch (Exception e) {
			e.printStackTrace()
		}
		
		document.get()
	}
	
	// Add copyright
	def addCopyright(source, comment) {
		comment + source
	}
	
	// Add a imports to source and organizes them
	def addImportsToSrc(source, importsToAdd) {
		def document = new org.eclipse.jface.text.Document(source)
		
		def parser = ASTParser.newParser(AST.JLS8)
		
		def options = JavaCore.getOptions()
		options.put(JavaCore.COMPILER_COMPLIANCE, "1.5")
		options.put(JavaCore.COMPILER_SOURCE, "1.5")
		parser.setCompilerOptions(options)
		
		parser.setSource(document.get().toCharArray())
		parser.setKind(ASTParser.K_COMPILATION_UNIT)
		
		def cu = (CompilationUnit) parser.createAST(null)
		
		if (!(cu.types().get(0) instanceof org.eclipse.jdt.core.dom.EnumDeclaration)) {
			cu.recordModifications()
			
			def ast = cu.getAST();
			
			def importMembers = []
			
			cu.imports().each { importDecl ->
				importMembers << importDecl.getName().toString()
			}
			
			cu.imports().clear()
			
			importsToAdd.each { member ->
				if (!importMembers.contains(member)) {
					importMembers << member
				}
			}
			
			importMembers.sort()
			
			importMembers.each { member ->
				def importDeclaration = ast.newImportDeclaration();
				importDeclaration.setName(ast.newName(member.split("\\.")))
			
				cu.imports().add(importDeclaration)
			}
			
			def edits = cu.rewrite(document, null)
			edits.apply(document)
		}
		
		document.get()
	}
	
	// Add methods to source
	def addMethodsToSrc(source, methodTemplate, templateBindings) {
		
		def document = new org.eclipse.jface.text.Document(source)
		def parser = ASTParser.newParser(AST.JLS8)
		
		def options = JavaCore.getOptions()
		options.put(JavaCore.COMPILER_COMPLIANCE, "1.5")
		options.put(JavaCore.COMPILER_SOURCE, "1.5")
		
		parser.setCompilerOptions(options)
		
		parser.setSource(document.get().toCharArray())
		parser.setKind(ASTParser.K_COMPILATION_UNIT)
		
		def cu = (CompilationUnit) parser.createAST(null)
		
		if (!(cu.types().get(0) instanceof org.eclipse.jdt.core.dom.EnumDeclaration)) {
			cu.recordModifications()
			
			def rewriter = ASTRewrite.create(cu.getAST())
			
			def engine = new SimpleTemplateEngine()
			
			def template = engine.createTemplate(methodTemplate).make(templateBindings)
			def methodSource = template.toString()
			
			def lrw = rewriter.getListRewrite((TypeDeclaration) cu.types().get(0),
					TypeDeclaration.BODY_DECLARATIONS_PROPERTY)
			
			lrw.insertLast(rewriter.createStringPlaceholder(methodSource,
					ASTNode.METHOD_DECLARATION), null)
			
			def edits = rewriter.rewriteAST(document, null)
			edits.apply(document)
		}
		
		document.get()
	}

	@TaskAction
	def sourceTransformation() {
		
		def addMethods = 
			[
				/.+(?<!(package-info)$)/:
					[
						[
							imports: 
							[
								"javax.xml.bind.JAXBContext",
								"javax.xml.bind.JAXBElement",
								"javax.xml.bind.JAXBException",
								"javax.xml.bind.Marshaller",
								"javax.xml.namespace.QName",
								"javax.xml.parsers.DocumentBuilderFactory",
								"javax.xml.parsers.ParserConfigurationException",
								"org.mitre.stix.DocumentUtilities",
								"org.mitre.stix.STIXSchema",
								"org.mitre.stix.ValidationEventHandler"
							],
							template:
								"""\
	/**
	 * Returns Document for JAXB Document Object Model object.
	 *
	 * @return the XML String for the JAXB object
	 */
	public org.w3c.dom.Document toDocument() {
		org.w3c.dom.Document document;
		Marshaller marshaller;
		
		try {
			document = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
			JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass()
					.getPackage().getName());
			
			marshaller = jaxbContext.createMarshaller();
			// pretty print
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			
			marshaller.setSchema(STIXSchema.getInstance().getSchema());
			marshaller.setEventHandler(new ValidationEventHandler());
			
			try {
				marshaller.marshal(this, document);
			} catch (JAXBException e) {
				QName qualifiedName = STIXSchema.getQualifiedName(this);
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JAXBElement root = new JAXBElement(qualifiedName, this.getClass(),
						this);
				
				marshaller.marshal(root, document);
			}
			
			DocumentUtilities.removeUnusedNamespaces(document);
			
			return document;
			
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
"""
						],
						[
							imports: 
							[
								"javax.xml.bind.JAXBElement",
								"javax.xml.namespace.QName",
								"org.mitre.stix.STIXSchema"
							],
							template:
								"""\
	/**
	 * Returns JAXBElement for JAXB Document Object Model object.
	 *
	 * @return the XML String for the JAXB object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JAXBElement<?> toJAXBElement() {
		QName qualifiedName = STIXSchema.getQualifiedName(this);
		
		return new JAXBElement(qualifiedName, \${name}.class, this); 
	}
"""
						],
						[
							imports: 
							[
								"org.mitre.stix.DocumentUtilities"
							],
							template:
								"""\
	/**
	 * Returns XML String for JAXB Document Object Model object.
	 *
	 * @return the XML String for the JAXB object
	 */
	public String toXMLString() {
		return DocumentUtilities.toXMLString(toDocument());
	}
"""
						],
						[
							imports: 
							[
								"java.io.StringReader",
								"javax.xml.bind.JAXBContext",
								"javax.xml.bind.JAXBException",
								"javax.xml.bind.Unmarshaller",
								"javax.xml.transform.stream.StreamSource",
								"org.mitre.stix.STIXSchema",
								"org.mitre.stix.ValidationEventHandler"
							],
							template:
								"""\
	/**
	 * Creates JAXB Document Object Model for XML text string
	 * 
	 * @param text
	 *            XML string for the document
	 * @return JAXB object
	 */
	public static \${name} fromXMLString(String text) {
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(\${name}.class.getPackage()
					.getName());
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			unmarshaller.setSchema(STIXSchema.getInstance().getSchema());
			unmarshaller.setEventHandler(new ValidationEventHandler());
			StreamSource streamSource = new StreamSource(new StringReader(text));
			return (\${name}) unmarshaller.unmarshal(streamSource);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} 
	}
"""
						],
						[
							imports: 
							[
							],
							template:
								"""\
	/**
	 * Validates the XML representation of this JAXB Document 
	 * Object Model object. Returning true indicating a successful
	 * validation, false if not.
	 * 
	 * @return boolean
	 */
	public boolean validate() {
		return STIXSchema.getInstance().validate(toXMLString());
	}
"""
						]
					]
			]
		
		def dom = []
		
		project.file("src/generated/java").eachFileRecurse(FileType.FILES) { file ->
			if (!file.name.endsWith("EnumType.java") && !file.name.endsWith("TypeEnum.java")) {

				def obj = new LinkedHashMap();
				obj.uri = file.toURI()
				obj.name = file.getName().split(/\./)[0]
				obj.package = file.getParent().replaceAll(project.file("src/generated/java").path, "").substring(1).replaceAll(System.getProperty("file.separator"),'.')
				
				dom << obj
			}
		}
		
		dom.each() { obj ->
			def templateBindings = ["pkg":obj.package, "name":obj.name]
			
			def source = project.file(obj.uri).readLines().iterator()
					.join(lineSeperator)
			
			addMethods.each { regex, methodDeclarations ->
				
				if ( obj.package + "." + obj.name ==~ regex) {
					
					logger.debug("    handling ${obj.package + "." + obj.name}")
					
					methodDeclarations.each {  methodDeclaration ->
						
						source = addImportsToSrc(source,
								methodDeclaration["imports"])
						
						source = addMethodsToSrc(source,
								methodDeclaration["template"], templateBindings)
						
					}
				} else {
					// handle package-info
					logger.debug("    ignoring ${obj.package + "." + obj.name}")
				}
				
				source = addCopyright(source, """
/**
 * Copyright (c) ${Calendar.instance.get(Calendar.YEAR)}, The MITRE Corporation. All rights reserved.
 * See LICENSE for complete terms.
 */
 
 """) 
				project.file(obj.uri).with { outFile ->
					outFile.setWritable(true)
					outFile.withWriter{ out -> out.println format(source) }
					outFile.setWritable(false)
				}
				
			}
		}
	}
}
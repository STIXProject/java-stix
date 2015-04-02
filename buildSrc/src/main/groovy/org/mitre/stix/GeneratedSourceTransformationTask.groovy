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

import java.util.LinkedHashMap
import java.util.regex.Pattern

/**
 * Gradle Task used to perfom syntactical analysis and tranformations on the model 
 * (e.g., adding convenience methods)
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 *
 */
class GeneratedSourceTransformationTask extends DefaultTask {

	GeneratedSourceTransformationTask() {
		description = "Perfom syntactical analysis and tranformations on the model (e.g., adding convenience methods)"
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
	def addMethodsToSrc(source, methodTemplates, templateBindings) {

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

			methodTemplates.each { methodTemplate ->
				def template = engine.createTemplate(methodTemplate).make(templateBindings)
				def methodSource = template.toString()

				def lrw = rewriter.getListRewrite((TypeDeclaration) cu.types().get(0),
						TypeDeclaration.BODY_DECLARATIONS_PROPERTY)

				lrw.insertLast(rewriter.createStringPlaceholder(methodSource,
						ASTNode.METHOD_DECLARATION), null)
			}

			def edits = rewriter.rewriteAST(document, null)
			edits.apply(document)
		}

		document.get()
	}

	@TaskAction
	def sourceTransformation() {

		def addMethods = [
			/(.*)/: [
				imports: [
					"java.io.StringReader",
					"java.io.StringWriter",
					"javax.xml.bind.JAXBContext",
					"javax.xml.bind.JAXBElement",
					"javax.xml.bind.JAXBException",
					"javax.xml.bind.Marshaller",
					"javax.xml.bind.Unmarshaller",
					"javax.xml.namespace.QName",
					"javax.xml.parsers.DocumentBuilderFactory",
					"javax.xml.parsers.ParserConfigurationException",
					"javax.xml.transform.stream.StreamSource",
					"org.mitre.stix.Utilities",
					"org.mitre.stix.STIXSchema",
					"org.mitre.stix.ValidationEventHandler"
				],
				methodTemplates: [
					"""\
	/**
	 * Returns XML String for JAXB Document Object Model object.
	 *
	 * @return the XML String for the JAXB object
	 */
	public String toXMLString() {
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
				// otherwise handle non-XMLRootElements
				QName qualifiedName = new QName(Utilities.getnamespaceURI(this),
						this.getClass().getSimpleName());

				@SuppressWarnings({ "rawtypes", "unchecked" })
				JAXBElement root = new JAXBElement(qualifiedName, this.getClass(),
						this);

				marshaller.marshal(root, document);
			}

			Utilities.removeUnusedNamespaces(document);

			return Utilities.getXMLString(document);		

		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}	
	}
""",
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
""",
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
				]]]

		//		def replaceCode = [
		//			/(org.mitre.stix.stix_1.STIXType) |
		//			(org.mitre.stix.indicator_2.IndicatorType) |
		//			(org.mitre.stix.incident_1.IncidentType) |
		//			(org.mitre.stix.ttp_1.TTPType) |
		//			(org.mitre.stix.threatactor_1.ThreatActorType) |
		//			(org.mitre.stix.campaign_1.CampaignType) |
		//			(org.mitre.stix.exploittarget_1.ExploitTargetType) |
		//			(org.mitre.stix.courseofaction_1.CourseOfActionType) |
		//			(org.mitre.cybox.cybox_2.ObservableType) |
		//			(org.mitre.cybox.cybox_2.ObjectType) |
		//			(org.mitre.cybox.objects.TaskActionTypeType) |
		//			(org.mitre.cybox.cybox_2.EventType)/: [
		//				'regex':/(?m)(\/\*\*.*)(public void setId.*})/, 'codeTemplate': """\
		//    /**
		//	 * Sets the value of the id property.
		//	 *
		//	 * Helloworld!
		//	 *
		//	 * @param value
		//	 *     allowed object is
		//	 *     {@link QName }
		//	 *
		//	 */
		//    public void setId(QName value) {
		//        this.id = value;
		//        System.out.println("Helloworld!");
		//    }
		//"""
		//			]]

		def dom = []

		project.file("src/generated/java").eachFileRecurse(FileType.FILES) { file ->
			if (!file.name.endsWith("package-info.java") && !file.name.endsWith("EnumType.java") && !file.name.endsWith("TypeEnum.java")) {

				def obj = new LinkedHashMap();
				obj.uri = file.toURI()
				obj.name = file.getName().split(/\./)[0]
				obj.package = file.getParent().split(Pattern.quote(System.getProperty("file.separator")))[3..-1].join('.')

				dom << obj
			}
		}

		dom.each() { obj ->
			def templateBindings = ["pkg":obj.package, "name":obj.name]

			addMethods.each { regex, methodDeclarations ->

				if ( obj.package + "." + obj.name ==~ regex) {

					def source = project.file(obj.uri).readLines().iterator()
							.join(lineSeperator)

					source = addImportsToSrc(source,
							methodDeclarations["imports"])

					source = addMethodsToSrc(source,
							methodDeclarations["methodTemplates"], templateBindings)

					//			replaceCode.each { regex, replace ->
					//				if ( obj.package + "." + obj.name ==~ regex) {
					//					if (lines == null) {
					//						lines = project.file(obj.uri).readLines().iterator().join("\n")
					//					}
					//
					//					def replacementCode = ""
					//					replace['codeTemplate'].eachLine { line ->
					//						replacementCode += line.replaceAll(/(\$\{pkg\})/, obj.package).replaceAll(/(\$\{name\})/, obj.name) + "\n"
					//					}
					//
					//					lines.replaceAll(replace["regex"], replacementCode)
					//				}
					//			}

					project.file( obj.uri ).with { outFile ->
						outFile.setWritable(true)
						outFile.withWriter{ out -> out.println format(source) }
						outFile.setWritable(false)
					}
				}
			}
		}
	}
}
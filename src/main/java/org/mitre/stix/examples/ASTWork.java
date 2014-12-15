package org.mitre.stix.examples;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class ASTWork {

	public ASTWork() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException,
			MalformedTreeException, BadLocationException {

		String source = new String(
				Files.readAllBytes(FileSystems
						.getDefault()
						.getPath(
								"src/generated/java/org/mitre/stix/stix_1/STIXType.java")));

		// StringBuffer buf = new StringBuffer();
		// buf.append("package org.mitre.stix;\n");
		// buf.append("public class Test {\n");
		// buf.append("    public void foo() {\n");
		// buf.append("    }\n");
		// buf.append("    public void bar() {\n");
		// buf.append("    }\n");
		// buf.append("}\n");
		//
		// String source = buf.toString();

		Document document = new org.eclipse.jface.text.Document(source);

		// create AST for source file...
		ASTParser parser = ASTParser.newParser(AST.JLS8);

		// Annotations are only available if source level is 1.5 or greater
		@SuppressWarnings("unchecked")
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
		parser.setCompilerOptions(options);

		parser.setSource(document.get().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		cu.recordModifications();
		AST ast = cu.getAST();
		ASTRewrite rewriter = ASTRewrite.create(ast);

		if (cu.getProblems().length > 0) {
			for (IProblem problem : cu.getProblems()) {
				System.out.println(">>>" + problem.getMessage());
			}
		}

		System.out.println(cu.types().size());

		for (Object o : cu.types()) {
			System.out.println("------------------------------------> "
					+ o.getClass().getName());
		}

		TypeDeclaration typeDecl = (TypeDeclaration) cu.types().get(0);

		// List<?> bodyDecls = typeDecl.bodyDeclarations();
		ListRewrite lrw = rewriter.getListRewrite(typeDecl,
				TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

		StringBuffer buf = new StringBuffer();
		buf.append("/**\n");
		buf.append("* Helloworld!\n");
		buf.append("* \n");
		buf.append("*/\n");
		buf.append("public String hello() {\n");
		buf.append("	System.out.prinlnt(\"Helloworld!\");\n");
		buf.append("}\n");

		String helloworldSrc = buf.toString();

		ASTNode snippetNode = rewriter.createStringPlaceholder(helloworldSrc,
				ASTNode.METHOD_DECLARATION);
		lrw.insertLast(snippetNode, null);

		lrw = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);

		List<ImportDeclaration> ImportDecls = cu.imports();
		ArrayList<String> imports = new ArrayList();

		for (ImportDeclaration importDecl : ImportDecls) {
			imports.add(importDecl.getName().toString());
		}

		if (!imports.contains("java.util.test")) {
			snippetNode = rewriter.createStringPlaceholder(
					"import java.util.test;", ASTNode.IMPORT_DECLARATION);

			lrw.insertLast(snippetNode, null);
		}

		// buf = new StringBuffer();
		// buf.append("/**\n");
		// buf.append("* New Foo!\n");
		// buf.append("* \n");
		// buf.append("*/\n");
		// buf.append("public String foo() {\n");
		// buf.append("	java.util.List<String> list = new java.util.List<String>();\n");
		// buf.append("	list.add(\"Cat\");\n");
		// buf.append("	System.out.prinlnt(\"New Foo!\");\n");
		// buf.append("}\n");
		//
		// String newFooSrc = buf.toString();
		// ASTNode newFooNode = rewriter.createStringPlaceholder(newFooSrc,
		// ASTNode.METHOD_DECLARATION);
		//
		// // Replace the original foo method
		// for (MethodDeclaration methodDeclaration : typeDecl.getMethods()) {
		// if (methodDeclaration.getName().toString().endsWith("foo")) {
		// lrw.replace(methodDeclaration, newFooNode, null);
		// break;
		// }
		// }

		// Generate source from AST
		TextEdit edits = rewriter.rewriteAST(document, null);
		edits.apply(document);

		source = document.get();

		System.out.println(source);

		@SuppressWarnings("rawtypes")
		Map formatterOptions = DefaultCodeFormatterConstants
				.getEclipseDefaultSettings();

		// initialize the compiler settings to be able to format 1.5 code
		formatterOptions
				.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
		formatterOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_5);
		formatterOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);

		CodeFormatter codeFormatter = ToolFactory
				.createCodeFormatter(formatterOptions);

		TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT,
				source, 0, source.length(), 0,
				System.getProperty("line.separator"));

		if (edit == null) {
			System.out.println("Crap!");
		}
		document = new Document(source);

		edit.apply(document);

//		System.out.println("attempting to arganize imports");
//		
//		try {
//			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//			IProject project = root.getProject("TESTJDT");
//			project.create(null);
//			project.open(null);
//
//			IProjectDescription description = project.getDescription();
//			description.setNatureIds(new String[] { JavaCore.NATURE_ID });
//
//			project.setDescription(description, null);
//			IJavaProject javaProject = JavaCore.create(project);
//
//			IFolder folder = project.getFolder("src");
//			folder.create(true, true, null);
//
//			IPackageFragmentRoot srcFolder = javaProject
//					.getPackageFragmentRoot(folder);
//
//			IPackageFragment fragment = srcFolder.createPackageFragment(
//					"org.mitre.stix.stix_1", true, null);
//
//			ICompilationUnit icu = fragment.createCompilationUnit(
//					"STIXType.java", document.get(), false, null);
//
//			NullProgressMonitor pm = new NullProgressMonitor();
//			
//			OrganizeImportsOperation op = new OrganizeImportsOperation(icu, cu, true, true, true, null);
//			
//			edit = op.createTextEdit(pm);
//			
//			
//		} catch (Exception e) {
//
//		}
//		
//		edit.apply(document);

		// display the formatted string on the System out
		System.out.println(document.get());

	}

}
package compiler;
import java.util.ArrayList;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import core.DrawableClass;



public class LiveCompiler{
	
	JavaSourceFromString source;
	int count = 0;
	
	String[] templateSetup = {"public class DynClassSetup extends PersistentDrawableClass{\r\n", "import core.*;\r\nimport processing.core.*;\r\n"};
	String[] templateDraw = {"public class DynClassDraw extends DrawableClass{\r\n", "import core.*;\r\nimport processing.core.*;\r\n"};
	
	
	
	JavaCompiler compiler ;
	JavaFileManager fileManager;
	ArrayList<JavaSourceFromString> jfiles = new ArrayList<JavaSourceFromString>();
	
	boolean willFail = false;
	
	
	public LiveCompiler(){
		
		System.setProperty("java.home", "C:\\Program Files (x86)\\Java\\jdk1.8.0_05\\jre");
		compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler == null){
			System.out.println("WARNIGN NO COMPILER FOUND");
			return;
		}
      
		 fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));
		
	}
	
	public Object compile(StringBuilder setup, StringBuilder draw) throws InstantiationException, IllegalAccessException, ClassNotFoundException, FuckedSourceException{
		willFail = false;
		jfiles = new ArrayList<JavaSourceFromString>();
		if(setup == null){
			addFile(draw, "DynClassDraw", templateDraw);
			return compile("DynClassDraw");
		}
		addFile(setup, "DynClassSetup", templateSetup);
		addFile(draw, "DynClassDraw",templateDraw);
		PersistentDrawableClass setupClass = (PersistentDrawableClass) compile("DynClassSetup");
		DrawableClass drawClass = (DrawableClass)compile("DynClassDraw");
		setupClass.setDrawClass(drawClass);
		
		return setupClass;
		
		
	}
	
	public Object compile(String fullName) throws FuckedSourceException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		if(willFail){
			System.out.println("THIS WILL FAIL");
			return null;
		}
		System.out.println("COMPILING----- " + fullName);
		
        //set up a diagnostic capture
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        
        
        // We specify a task to the compiler. Compiler should use our file
        // manager and our list of "files".
        // Then we run the compilation with call()
        compiler.getTask(null, fileManager, diagnostics, null,  null, jfiles).call();
        for(Diagnostic d : diagnostics.getDiagnostics() ){
        	System.out.println(d.toString());
        	FuckedSourceException e = new FuckedSourceException();
        	e.row = d.getLineNumber() - 4;
        	e.column = d.getColumnNumber();
        	throw e;
        }

        // Creating an instance of our compiled class and
        // return it
        Object instance = fileManager.getClassLoader(null)
            .loadClass(fullName).newInstance();	
        
        System.out.println("COMPILING COMPLETE! " + instance);
        return instance;
	}
	
	public void addFile(StringBuilder  s, String fullName, String[] template) {
		
		//whilst scanning the strings look for an odd number of pushmatrix and popmatrix strings. Fail if they arent even
		int pushCount = findCounts("p.pushMatrix()", s.toString());
		int popCount = findCounts("p.popMatrix()", s.toString());
		if(pushCount != popCount){
			System.out.println("push and pop dont match");
			willFail = true;
			return ;
		}
		
		//insert the headers to make this a proper class
		for(int i = 0; i < template.length; i++){
			s.insert(0, template[i]);
		}
		s.append("}");
		
		
		
		
		
	

        // Dynamic compiling requires specifying
        // a list of "files" to compile. In our case
        // this is a list containing one "file" which is in our case
        // our own implementation (see details below)
        
        jfiles.add(new JavaSourceFromString(fullName, s	));
        System.out.println("added " + fullName + " to files");
        
	}

	private int findCounts(String search, String input) {
		int lastIndex = 0;
		int count =0;

		while(lastIndex != -1){

		       lastIndex = input.indexOf(search,lastIndex);

		       if( lastIndex != -1){
		             count ++;
		             lastIndex+=search.length();
		      }
		}
		return count;
	}
	
	
}

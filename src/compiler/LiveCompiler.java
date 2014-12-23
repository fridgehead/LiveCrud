package compiler;
import java.util.ArrayList;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;


public class LiveCompiler{
	
	JavaSourceFromString source;
	int count = 0;
	
	String[] template = {	"public class DynClass extends DrawableClass{\r\n", 
							"import core.*;\r\n",
							"import processing.core.*;\r\n",
							"import damkjer.ocd.Camera;\r\n",
							"import de.voidplus.leapmotion.*;\r\n",
							};
	
	
	public LiveCompiler(){}
	
	public Object compile(StringBuilder  s) throws InstantiationException, IllegalAccessException, ClassNotFoundException, FuckedSourceException{
		String fullName = "DynClass";
		//whilst scanning the strings look for an odd number of pushmatrix and popmatrix strings. Fail if they arent even
		int pushCount = findCounts("p.pushMatrix()", s.toString());
		int popCount = findCounts("p.popMatrix()", s.toString());
		if(pushCount != popCount){
			System.out.println("push and pop dont match");
			return null;
		}
		
		//insert the headers to make this a proper class
		for(int i = 0; i < template.length; i++){
			s.insert(0, template[i]);
		}
		s.append("}");
		
		
		
		System.out.println("COMPILING-----");
		System.out.println(s);
		
		count ++;
		System.setProperty("java.home", "C:\\Program Files (x86)\\Java\\jdk1.8.0_05\\jre");
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if(compiler == null){
			System.out.println("WARNIGN NO COMPILER FOUND");
			return null;
		}
        JavaFileManager fileManager = new
            ClassFileManager(compiler
                .getStandardFileManager(null, null, null));

        // Dynamic compiling requires specifying
        // a list of "files" to compile. In our case
        // this is a list containing one "file" which is in our case
        // our own implementation (see details below)
        ArrayList<JavaSourceFromString> jfiles = new ArrayList<JavaSourceFromString>();
        jfiles.add(new JavaSourceFromString(fullName, s	));
        

        //set up a diagnostic capture
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        
        
        // We specify a task to the compiler. Compiler should use our file
        // manager and our list of "files".
        // Then we run the compilation with call()
        compiler.getTask(null, fileManager, diagnostics, null,  null, jfiles).call();
        for(Diagnostic d : diagnostics.getDiagnostics() ){
        	FuckedSourceException e = new FuckedSourceException();
        	e.row = d.getLineNumber() - template.length - 1;
        	e.column = d.getColumnNumber();
        	e.error = d.getMessage(Locale.ENGLISH);
        	throw e;
        }

        // Creating an instance of our compiled class and
        // return it
        Object instance = fileManager.getClassLoader(null)
            .loadClass(fullName).newInstance();	
        System.out.println("COMPILING COMPLETE!");
        return instance;
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

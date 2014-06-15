package compiler;
import java.util.ArrayList;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.ToolProvider;


public class LiveCompiler {
	
	JavaSourceFromString source;
	int count = 0;
	
	public LiveCompiler(){}
	
	public Object compile(StringBuilder  s) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		String fullName = "DynClass";
		
		s.insert(0, "public class DynClass extends DrawableClass{\r\n");
		s.insert(0, "import core.*;\r\nimport processing.core.*;\r\n;");
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

        // We specify a task to the compiler. Compiler should use our file
        // manager and our list of "files".
        // Then we run the compilation with call()
        compiler.getTask(null, fileManager, null, null,
            null, jfiles).call();

        // Creating an instance of our compiled class and
        // running its toString() method
        Object instance = fileManager.getClassLoader(null)
            .loadClass(fullName).newInstance();	
        System.out.println("COMPILING COMPLETE!");
        return instance;
	}
	
	
}

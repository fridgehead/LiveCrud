package compiler;
import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.HashMap;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class ClassFileManager extends
ForwardingJavaFileManager {
	/**
	 * Instance of JavaClassObject that will store the
	 * compiled bytecode of our class
	 */
	private JavaClassObject jclassObject;
	private HashMap<String, JavaClassObject>  data;

	/**
	 * Will initialize the manager with the specified
	 * standard java file manager
	 *
	 * @param standardManger
	 */
	public ClassFileManager(StandardJavaFileManager
			standardManager) {
		super(standardManager);
		data = new HashMap<String, JavaClassObject>();
	}

	/**
	 * Will be used by us to get the class loader for our
	 * compiled class. It creates an anonymous class
	 * extending the SecureClassLoader which uses the
	 * byte code created by the compiler and stored in
	 * the JavaClassObject, and returns the Class for it
	 */
	@Override
	public ClassLoader getClassLoader(Location location) {
		return new SecureClassLoader() {
			@Override
			protected Class<?> findClass(String name)
					throws ClassNotFoundException {

				try { 
					JavaClassObject j = data.get(name);
					byte[] b = j.getBytes();

					return super.defineClass(name, j.getBytes(), 0, b.length);
				} catch (Exception e){
					throw new ClassNotFoundException(name);
				}
			}


		};
	}

	/**
	 * Gives the compiler an instance of the JavaClassObject
	 * so that the compiler can write the byte code into it.
	 */
	@Override
	public JavaClassObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling)
					throws IOException {
		
		JavaClassObject j = new JavaClassObject(className, kind);
		data.put(className, j);
		return j;
	}
}
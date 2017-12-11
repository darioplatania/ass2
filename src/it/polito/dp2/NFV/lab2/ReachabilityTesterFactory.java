/**
 * 
 */
package it.polito.dp2.NFV.lab2;

import it.polito.dp2.NFV.FactoryConfigurationError;

/**
 * Defines a factory API that enables applications to obtain one or more objects
 * implementing the {@link ReachabilityTester} interface.
 *
 */
public abstract class ReachabilityTesterFactory {

	private static final String propertyName = "it.polito.dp2.NFV.lab2.ReachabilityTesterFactory";
	
	protected ReachabilityTesterFactory() {}
	
	/**
	 * Obtain a new instance of a <tt>ReachabilityTesterFactory</tt>.
	 * 
	 * <p>
	 * This static method creates a new factory instance. This method uses the
	 * <tt>it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory</tt> system property to
	 * determine the ReachabilityTesterFactory implementation class to load.
	 * </p>
	 * <p>
	 * Once an application has obtained a reference to a
	 * <tt>ReachabilityTesterFactory</tt> it can use the factory to obtain a new
	 * {@link ReachabilityTester} instance.
	 * </p>
	 * 
	 * @return a new instance of a <tt>ReachabilityTesterFactory</tt>.
	 * 
	 * @throws FactoryConfigurationError if the implementation is not available 
	 * or cannot be instantiated.
	 */
	public static ReachabilityTesterFactory newInstance() throws FactoryConfigurationError {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		
		if(loader == null) {
			loader = ReachabilityTesterFactory.class.getClassLoader();
		}
		
		String className = System.getProperty(propertyName);
		if (className == null) {
			throw new FactoryConfigurationError("cannot create a new instance of a ReachabilityTesterFactory"
												+ "since the system property '" + propertyName + "'"
												+ "is not defined");
		}
		
		try {
			Class<?> c = (loader != null) ? loader.loadClass(className) : Class.forName(className);
			return (ReachabilityTesterFactory) c.newInstance();
		} catch (Exception e) {
			throw new FactoryConfigurationError(e, "error instantiatig class '" + className + "'.");
		}
	}
	
	
	/**
	 * Creates a new instance of a {@link ReachabilityTester} implementation.
	 * 
	 * @return a new instance of a {@link ReachabilityTester} implementation.
	 * @throws ReachabilityTesterException if an implementation of {@link ReachabilityTester} cannot be created.
	 */
	public abstract ReachabilityTester newReachabilityTester() throws ReachabilityTesterException;
}
package org.curso.java.avanzado;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que carga un driver JDBC y lo envuelve en un proxy.
 * 
 * Esta clase permite cargar un driver JDBC desde un archivo de propiedades o
 * desde parámetros específicos, y proporciona un proxy para el driver cargado.
 * 
 * @author Aurelio García
 */
public class ProxyDBDriverLoader {

	public ProxyDBDriverLoader() {
		this(null, null, null);
	}
	
	public ProxyDBDriverLoader(String propertiesFile) {
		this.loadPropertiesFromFile(propertiesFile);
	}
	
	public ProxyDBDriverLoader(String urlDB, String className, String libraryJDBC) {
		this.urlDB = urlDB;
		this.className = className;
		this.libraryJDBC = libraryJDBC;
	}
	
	private String urlDB = null;  
	private String className = null; 
	private String libraryJDBC = null; 
	private Driver libraryDriver = null; 
	private Driver proxyDriver = null; 
	
	/***
	 * Método que devuelve la URL de la base de datos.
	 * 
	 * @return URL de la base de datos.
	 */
	public String getUrlDB() {
		return urlDB;
	}	
	
	/***
	 * Método que establece la URL de la base de datos.
	 * 
	 * @param urlDB URL de la base de datos.
	 */
	public void setUrlDB(String urlDB) {
		this.urlDB = urlDB;
	}
	
	/***
	 * Método que devuelve el nombre de la clase del driver JDBC.
	 * 
	 * @return Nombre de la clase del driver JDBC.
	 */
	public String getClassName() {
		return className;
	}
	/***
	 * Método que establece el nombre de la clase del driver JDBC.
	 * 
	 * @param className Nombre de la clase del driver JDBC.
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/***
	 * Método que devuelve el nombre del driver JDBC.
	 * 
	 * @return Nombre del driver JDBC.
	 */
	public String getLibraryJDBC() {
		return libraryJDBC;
	}
	/***
	 * Método que establece el nombre del driver JDBC.
	 * 
	 * @param driverJDBC Nombre del driver JDBC.
	 */
	public void setLibraryJDBC(String driverJDBC) {
		this.libraryJDBC = driverJDBC;
	}
	
	/***
	 * Método que devuelve el driver JDBC registrado.
	 * 
	 * @return Driver JDBC.
	 */
	public Driver getDriver() {
		return this.loadDriver();
	}

	
	/***
	 * Método que carga las propiedades desde un archivo XML.
	 * 
	 * @param propertiesFile Nombre del archivo de propiedades.
	 * @throws IllegalArgumentException Si el nombre del archivo es nulo o vacío.
	 */
	private void loadPropertiesFromFile(String propertiesFile) {
		if (propertiesFile == null || propertiesFile.isEmpty()) {
			throw new IllegalArgumentException("El nombre del archivo de propiedades no puede ser nulo o vacío.");
		}
		 Properties properties = new Properties();
		 try (InputStream is = new FileInputStream(propertiesFile)) {
		    properties.loadFromXML(is);
		    if (properties.containsKey("url_db")) {
		    	urlDB = properties.getProperty("url_db");
		    }
		    if (properties.containsKey("class_name") ) {
		    	className = properties.getProperty("class_name");
		    }
		    if (properties.containsKey("driver_jdbc")) {
		    	libraryJDBC = properties.getProperty("driver_jdbc");
		    }
		 } catch (IOException ex) {
			 System.out.println("No se pudo cargar el archivo de propiedades: " + ex.getMessage());
		 }
	}
	
	/***
	 * Método que carga el driver JDBC.
	 * 
	 * @throws SQLException Si ocurre un error al cargar el driver JDBC.
	 */
	private Driver loadDriver() {
		if (proxyDriver == null) {
			try {
				// Cargar la librería del driver JDBC	
				 URL u = new URI("file:./lib/" + libraryJDBC).toURL();
				 // Cargar la clase del driver JDBC
				 URLClassLoader ucl = new URLClassLoader(new URL[] { u });
				// Instanciar el driver JDBC
				Class<?> driverClass = Class.forName(className, true, ucl);
				libraryDriver = (Driver) driverClass.getDeclaredConstructor().newInstance();
				// Crear el proxy del driver JDBC
				proxyDriver = new ProxyDBDriver(libraryDriver);
			} catch (Exception e) {
				throw new InternalError("Error al registrar el driver JDBC: " + e.getMessage(), e);
			}
		}
		return proxyDriver;
	}
	
	
}

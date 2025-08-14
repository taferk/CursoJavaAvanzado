package org.curso.java.avanzado;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Clase que envuelve un driver JDBC y lo convierte en un proxy.
 * 
 * Esta clase permite crear un proxy para un driver JDBC existente, delegando
 * todas las llamadas al driver original.
 * 
 * @author Aurelio García
 */
public class ProxyDBDriver implements Driver {
	private Driver innerDriver;
	ProxyDBDriver(Driver driver) {
		this.innerDriver = driver;
	}
	public boolean acceptsURL(String u) throws SQLException {
		return this.innerDriver.acceptsURL(u);
	}
	public Connection connect(String u, Properties p) throws SQLException {
		return this.innerDriver.connect(u, p);
	}
	public int getMajorVersion() {
		return this.innerDriver.getMajorVersion();
	}
	public int getMinorVersion() {
		return this.innerDriver.getMinorVersion();
	}
	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.innerDriver.getPropertyInfo(u, p);
	}
	public boolean jdbcCompliant() {
		return this.innerDriver.jdbcCompliant();
	}
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.innerDriver.getParentLogger();
	}
}

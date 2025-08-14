package org.curso.java.avanzado;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Scanner;

public class EjercicioJDBCFile {
	
	public static void main(String[] args) {
		final String PROPERTIES_FILE = "EjercicioJDBCFile.properties";
	
		// Archivo CSV a importar por defecto
		 String fileImport = "clientes.dat";
		 File file = new File(fileImport);
		 if (!file.exists()) {
			 System.err.println("El archivo de importación no existe: " + fileImport);
			 return;
		 } 
		 System.out.println("Archivo de importación: " + fileImport);
		
		 try (Scanner scanner = new Scanner(file)) {
			 // Cargar y registrar el driver JDBC desde un fichero de configuración
			 ProxyDBDriverLoader loaderJDBC = new ProxyDBDriverLoader(PROPERTIES_FILE);
			 DriverManager.registerDriver(loaderJDBC.getDriver()); 

			 // Conectar a la base de datos
			 try (Connection conn = DriverManager.getConnection(loaderJDBC.getUrlDB())){
				 System.out.println("Conexión establecida con éxito a la base de datos: " + loaderJDBC.getUrlDB());
				 if (scanner.hasNextLine()) {
					 String header = scanner.nextLine(); // Leer la primera línea (cabecera)
					 System.out.println("Cabecera del archivo: " + header);
				 }
				 
				 while(scanner.hasNextLine()) {
					 String line = scanner.nextLine();
					 String[] parts = line.split(";");
					 if (parts.length == 5) {
						 String id = parts[0].trim();
						 String nombre = parts[1].trim();
						 String apellidos = parts[2].trim();
						 String nacimiento = formatoFecha(parts[3].trim());
						 String pedidos = parts[4].trim();
						 
						 // Aquí podrías insertar los datos en la base de datos
						 String sqlInsert = "INSERT INTO Clientes (ID, NOMBRE, APELLIDOS, FECHA_NACIMIENTO, NUM_PEDIDOS) VALUES (?, ?, ?,?,?)";
						 try (PreparedStatement stmInsert = conn.prepareStatement(sqlInsert)){
							 // Preparar la sentencia SQL para insertar los datos
							 stmInsert.setInt(1, Integer.parseInt(id));
							 stmInsert.setString(2, nombre);
							 stmInsert.setString(3, apellidos);
							 stmInsert.setString(4, nacimiento);
							 stmInsert.setInt(5, Integer.parseInt(pedidos));
							 
							 int stResult = stmInsert.executeUpdate();
							 if (stResult > 0) {
								 System.out.println("Datos insertados correctamente: " + id + ", " + nombre + ", " + apellidos);
							 } else {
								 System.err.println("Error al insertar los datos: " + id + ", " + nombre + ", " + apellidos);
							 }
						 } catch (Exception e) {
							 System.err.println("Error al insertar los datos: " + e.getMessage());
						 }
						 
					 } else {
						 System.err.println("Línea inválida en el archivo CSV: " + line);
					 }
				 }
				 System.out.println("Datos importados desde el archivo: " + fileImport);
				 System.out.println();
				 
				 System.out.println("Consulta para obtener clientes con más de 10 pedidos y fecha de nacimiento anterior a 1978");
				 String sqlSelect = "SELECT * FROM Clientes WHERE NUM_PEDIDOS > 10 AND FECHA_NACIMIENTO < '1978-01-01' ";
				 try (Statement stmSelect = conn.createStatement()) {
					 // Ejecutar la consulta SQL
					 var rs = stmSelect.executeQuery(sqlSelect);
					 while (rs.next()) {
						 System.out.println("Cliente con más de 10 pedidos: " + rs.getString("NOMBRE") + " " + rs.getString("APELLIDOS")+ " -> " + rs.getString("NUM_PEDIDOS"));
					 }
				 } catch (Exception e) {
					 System.err.println("Error al consultar los datos: " + e.getMessage());
				 }
				 
			 } catch (Exception e) {
				 System.err.println("Error al conectar a la base de datos: " + e.getMessage());
			 } 
		 } catch (Exception e) {
			 System.err.println("Error al cargar el driver JDBC: " + e.getMessage());
		 }
		System.out.println("Importación de datos finalizada.");
	}

	private static String formatoFecha(String fecha) {
		String[] parts = fecha.split("/");
		return parts[2] + "-" + parts[1] + "-" + parts[0]; // Cambiar formato de dd-mm-yyyy a yyyy-mm-dd
	}
}

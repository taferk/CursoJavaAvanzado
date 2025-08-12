package org.curso.java.avanzado.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;


public class WebScraping {

	public static void main(String[] args)  {
		System.out.println("Iniciando web scraping...");	
		String url = args.length > 0 ? args[0] : "https://java.com";
		System.out.println("Scraping de la URL: " + url);
		
		try {
			URI uriConnection = new URI(url);
			HttpURLConnection con = (HttpURLConnection) uriConnection.toURL().openConnection();
			int responseCode = con.getResponseCode();

			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.err.println("Error: El servidor respondió con código " + responseCode);
				return;
			}

			System.out.println("Response Code: " + responseCode);
			System.out.println("Response Headers:");
			con.getHeaderFields().forEach((key, value) -> {
				System.out.println(key + ": " + value);
			});

			try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				System.out.println("Response:");
				System.out.println(response.toString());
			}

		} catch (URISyntaxException e) {
			System.err.println("Error: La URL proporcionada no tiene un formato válido.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error: No se pudo establecer conexión o leer datos del servidor.");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Error inesperado: " + e.getMessage());
			e.printStackTrace();
		} 
		
		System.out.println("Web scraping finalizado.");
	}

}

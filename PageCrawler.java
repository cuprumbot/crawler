import java.io.IOException;
import java.util.LinkedList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageCrawler {
	private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	private LinkedList<String> links = new LinkedList<String>();
	private Document htmlDocument;

	public boolean crawl (String url) {

		try {
			// Realizar conexion y leer pagina
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			htmlDocument = connection.get();

			//if (connection.response().statusCode() == 200) {
			//	System.out.println("\nVisiting " + url);
			//}

			/*
				PORTABILIDAD: Activar si se trabajaran con links en general
				Los links internos de Wikipedia no requieren esta comprobacion
			*/	
			/*
			if (!connection.response().contentType().contains("text/html")) {
				return false;
			}
			*/

			// Obtener todos los links
			Elements linksOnPage = htmlDocument.select("a[href]");

			for (Element link : linksOnPage) {
				
				// Comprobar si es link interno
				String internal = link.toString();
				if (internal.contains("<a href=\"/wiki/")) {

					// Obtener direccion absoluta
					String fullUrl = link.absUrl("href");
					fullUrl = fullUrl.replace("%27","'");
					fullUrl = fullUrl.replace("%E2%80%93","\u2013");
					fullUrl = fullUrl.replace("%22","\"");

					/*
					fullUrl = fullUrl.replace("%C2%B2","\u00B2");
					fullUrl = fullUrl.replace("%C3%A9","\u00E9");
					fullUrl = fullUrl.replace("%C3%B8","\u00F8");
					fullUrl = fullUrl.replace("%C3%B2","\u00F2");
					fullUrl = fullUrl.replace("%C3%A1","\u00E1");
					fullUrl = fullUrl.replace("%C3%A8","\u00E8");
					fullUrl = fullUrl.replace("%C3%AE","\u00EE");
					fullUrl = fullUrl.replace("%C3%AD","\u00ED");
					fullUrl = fullUrl.replace("%C3%BC","\u00FC");
					fullUrl = fullUrl.replace("%C3%B6","\u00F6");
					fullUrl = fullUrl.replace("%C5%8D","\u014D");
					fullUrl = fullUrl.replace("%C5%8C","\u014C");
					fullUrl = fullUrl.replace("%C5%82","\u0142");
					*/

					if (fullUrl.contains("%")) {
						//System.out.println("Skiping " + fullUrl);
						continue;
					}

					// Verificar que no sea la misma pagina
					// ...ni la pagina principal
					// ...ni una pagina especial
					// ...ni un anchor a una seccion de una pagina
					// Cuando se cumple se agrega a lista
					if (!fullUrl.equals(url) &&
						!fullUrl.contains("/wiki/Main_Page") &&
						!fullUrl.contains("/wiki/Book:") &&
						!fullUrl.contains("/wiki/Category:") &&
						!fullUrl.contains("/wiki/Draft:") &&
						!fullUrl.contains("/wiki/File:") &&
						!fullUrl.contains("/wiki/Gadget:") &&
						!fullUrl.contains("/wiki/Help:") &&
						!fullUrl.contains("/wiki/Media:") &&
						!fullUrl.contains("/wiki/MediaWiki:") &&
						!fullUrl.contains("/wiki/Module:") &&
						!fullUrl.contains("/wiki/Portal:") &&
						!fullUrl.contains("/wiki/Special:") &&
						!fullUrl.contains("/wiki/Talk:") &&
						!fullUrl.contains("/wiki/Template:") &&
						!fullUrl.contains("/wiki/TimedText:") &&
						!fullUrl.contains("/wiki/User:") &&
						!fullUrl.contains("/wiki/Wikipedia:") &&
						!fullUrl.contains("_talk:") &&
						!fullUrl.contains("#") 
					) {
						this.links.add(fullUrl);
					}
				}
				
			}

			return true;
			
		} catch (IOException e) {
			return false;
		}
	}

	public LinkedList<String> getLinks () {
		return this.links;
	}
}
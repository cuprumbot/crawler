import java.util.HashSet;
import java.util.LinkedList;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class Crawler {
	private static int TOTAL_PAGES_TO_VISIT = 127000;
	private static int EACH_FILE_SIZE = 100;
	private HashSet<String> pagesVisited = new HashSet<String>();
	private HashSet<String> pagesToVisit = new HashSet<String>();
	private LinkedList<String> errores = new LinkedList<String>();
	private PrintWriter writer;

	private long starttime;
	private int num;
	private String filename;

	public Crawler (String filename) {
		starttime = System.currentTimeMillis();

		this.filename = filename;
		this.num = 0;

		try {
			this.writer = new PrintWriter(filename + this.num + ".csv");
		} catch (Exception e) {}		
	}

	public Crawler (String filename, String visited, String pending, int num) {
		starttime = System.currentTimeMillis();

		this.filename = filename;
		this.num = num + 1;

		try {
			this.writer = new PrintWriter(filename + this.num + ".csv");
		} catch (Exception e) {}

		try {
			FileReader fr;
			BufferedReader br;
			String line;

			fr = new FileReader(visited);
			br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {

				pagesVisited.add(line.substring(1,line.length()-1));
			}

			fr = new FileReader(pending);
			br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				pagesToVisit.add(line.substring(1,line.length()-1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("\nContinuando trabajo...");
		System.out.println("\nPaginas visitadas:  " + pagesVisited.size());
		System.out.println("Paginas pendientes: " + pagesToVisit.size());
		System.out.println((System.currentTimeMillis() - starttime)/1000 + " segundos transcurridos");
		//System.out.println("Trabajando a " + ((pagesVisited.size()*1000) / (System.currentTimeMillis()-starttime)) + " paginas por segundo\n");
	}

	public void search (String url) {

		this.pagesToVisit.add(url);

		while (this.pagesVisited.size() < TOTAL_PAGES_TO_VISIT) {
	//	while (this.pagesToVisit.size() > 0) {	

			// Obtener siguiente pagina
			String currentUrl = this.nextUrl();
			PageCrawler pc = new PageCrawler();

			// Recorrer pagina
			if (currentUrl != null && pc.crawl(currentUrl)) {
				this.onSuccessfulVisit(currentUrl, pc);
			} else {
				this.errores.add(currentUrl);
			}

			if (pagesVisited.size() % EACH_FILE_SIZE == 0) {	// profiler

				System.out.println("\nPaginas visitadas:  " + pagesVisited.size());
				System.out.println("Paginas pendientes: " + pagesToVisit.size());
				System.out.println((System.currentTimeMillis() - starttime)/1000 + " segundos transcurridos\n");

				try {
					PrintWriter visited = new PrintWriter("lists\\visited" + num + ".csv");
					for (String str : pagesVisited) {
						visited.println("\"" + str + "\"");
					}
					visited.close();

					PrintWriter pending = new PrintWriter("lists\\pending" + num + ".csv");
					for (String str : pagesToVisit) {
						pending.println("\"" + str + "\"");
					}
					pending.close();
				} catch (Exception e) {}

				try {
					num++;
					writer.close();
					writer = new PrintWriter(filename + num + ".csv");
				} catch (Exception e) {}
			}
		}

		System.out.println("ERRORES:\n");
		for (String str : errores) System.out.println(str);

		// fin
		try {
			PrintWriter visited = new PrintWriter("lists\\visitedFinal.csv");
			for (String str : pagesVisited) {
				visited.println("\"" + str + "\"");
			}
			visited.close();
		} catch (Exception e) {}

		System.out.println("\nPaginas visitadas:  " + pagesVisited.size());
		System.out.println("Paginas pendientes: " + pagesToVisit.size());
		System.out.println((System.currentTimeMillis() - starttime)/1000 + " segundos transcurridos\n");
		// fin

		writer.close();
	}

	public String nextUrl() {

		// Toma la primera pagina
		// Si ya esta en la lista de visitadas, toma otra
		String nextUrl = null;
		do {
			//nextUrl = this.pagesToVisit.removeFirst();

			for (String str : pagesToVisit) {
				nextUrl = str;
				break;
			}
			pagesToVisit.remove(nextUrl);

		} while (this.pagesToVisit.size() > 0 && this.pagesVisited.contains(nextUrl));
		return nextUrl;
	}

	public boolean onSuccessfulVisit(String url, PageCrawler pc) {

		// Agrega a lista de visitadas
		// Agrega links pendientes de visitar
		this.pagesVisited.add(url);
		this.pagesToVisit.addAll(pc.getLinks());

		// DEBUG: Links agregados
		for (String str : pc.getLinks()) {
			try {
				writer.print("\"" + url.substring(34) + "\"");
				writer.print(",");
				writer.println("\"" + str.substring(34) + "\"");
			} catch (Exception e) {}
		}

		return true;
	}
}
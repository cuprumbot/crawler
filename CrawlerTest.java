public class CrawlerTest {
    public static void main(String[] args) {
        Crawler c = new Crawler("result\\crawler");
        c.search("https://simple.wikipedia.org/wiki/Philosophy");
    }
}
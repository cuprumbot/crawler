public class ContinueTest {
    public static void main(String[] args) {
    	int num = 125;

        Crawler c = new Crawler("result\\crawler", "lists\\visited" + num + ".csv", "lists\\pending" + num + ".csv", num);
        c.search("https://simple.wikipedia.org/wiki/Philosophy");
    }
}
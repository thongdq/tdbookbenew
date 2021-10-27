package td.book.tdbook.util;

public class HttpUtil {

    public static String createURLWithPort(int port, String uri) {
        return "http://localhost:" + port + uri;
    }

}

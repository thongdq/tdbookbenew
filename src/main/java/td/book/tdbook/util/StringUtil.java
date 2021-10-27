package td.book.tdbook.util;

public class StringUtil {

    public static String keepOnlyLetterAndNumber(String str) {
        str = str.replaceAll("[^a-zA-Z0-9]", "");
        return str;
    }

}

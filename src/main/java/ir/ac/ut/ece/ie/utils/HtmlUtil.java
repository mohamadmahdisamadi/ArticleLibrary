package ir.ac.ut.ece.ie.utils;

public class HtmlUtil {
    public static String escapeHtml(String text) {
        if (text == null)
            return "";

        StringBuilder escaped = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '<': escaped.append("&lt;"); break;
                case '>': escaped.append("&gt;"); break;
                case '&': escaped.append("&amp;"); break;
                case '"': escaped.append("&quot;"); break;
                case '\'': escaped.append("&#x27;"); break;
                default: escaped.append(c);
            }
        }
        return escaped.toString();
    }
}

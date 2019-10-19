package ni.org.jug.exchangerate.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author aalaniz
 */
public class HtmlGenerator {

    private static final String TAB_AS_SPACES = "    ";
    private static final char NEW_LINE = '\n';
    private static Map<Integer, String> INDENTATION = new HashMap<>();
    static {
        INDENTATION.put(0, "");
        INDENTATION.put(1, "    ");
        INDENTATION.put(2, "        ");
        INDENTATION.put(3, "            ");
        INDENTATION.put(4, "                ");
        INDENTATION.put(5, "                    ");
        INDENTATION.put(6, "                        ");
        INDENTATION.put(7, "                            ");
        INDENTATION.put(8, "                                ");
        INDENTATION.put(9, "                                    ");
        INDENTATION.put(10, "                                        ");
    }


    private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";

    private final StringBuilder html;
    private int indentationLevel;

    public HtmlGenerator() {
        this.html = new StringBuilder(2000);
    }

    public HtmlGenerator(int initialIndentationLevel) {
        this();
        this.indentationLevel = initialIndentationLevel;
    }

    public int getIndentationLevel() {
        return indentationLevel;
    }

    public HtmlGenerator html(String doctype) {
        html.append(doctype).append(NEW_LINE).append("<html>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator html5() {
        return html(HTML5_DOCTYPE);
    }

    public HtmlGenerator closeHtml() {
        html.append("</html>");
        return this;
    }

    public HtmlGenerator head() {
        indent();
        html.append(getIndentation()).append("<head>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeHead() {
        html.append(getIndentation()).append("</head>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator style() {
        indent();
        html.append(getIndentation()).append("<style>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeStyle() {
        html.append(getIndentation()).append("</style>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator body() {
        indent();
        html.append(getIndentation()).append("<body>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeBody() {
        html.append(getIndentation()).append("</body>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator block(String value) {
        indent();
        html.append(getIndentation()).append(escape(value)).append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator ln() {
        html.append(NEW_LINE);
        return this;
    }

    public HtmlGenerator p() {
        indent();
        html.append(getIndentation()).append("<p>");
        return this;
    }

    public HtmlGenerator closeP() {
        html.append("</p>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator inline(String content) {
        html.append(escape(content));
        return this;
    }

    public HtmlGenerator strong(String content) {
        strong(content, true);
        return this;
    }

    public HtmlGenerator strong(String content, boolean condition) {
        String escaped = escape(content);
        if (condition) {
            html.append("<strong>").append(escaped).append("</strong>");
        } else {
            html.append(escaped);
        }
        return this;
    }

    public HtmlGenerator strong(BigDecimal amount, boolean condition) {
        return strong(amount.toPlainString(), condition);
    }

    public HtmlGenerator nbsp() {
        html.append("&nbsp;");
        return this;
    }

    public HtmlGenerator a(String link, String text) {
        html.append("<a href=\"").append(escape(link)).append("\">").append(escape(text)).append("</a>");
        return this;
    }

    public HtmlGenerator div(String... cssClasses) {
        indent();
        html.append(getIndentation()).append("<div");
        if (cssClasses != null && cssClasses.length > 0) {
            String cssClassesToHtml = Stream.of(cssClasses).collect(Collectors.joining(" ", " class=\"", "\""));
            html.append(cssClassesToHtml);
        }
        html.append(">").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeDiv() {
        html.append(getIndentation()).append("</div>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator addPlaceholder(String placeholder) {
        html.append(placeholder).append(NEW_LINE);
        return this;
    }

    public HtmlGenerator table() {
        indent();
        html.append(getIndentation()).append("<table>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTable() {
        html.append(getIndentation()).append("</table>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator tr() {
        indent();
        html.append(getIndentation()).append("<tr>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTr() {
        html.append(getIndentation()).append("</tr>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator th(String text) {
        return th(text, -1);
    }

    public HtmlGenerator th(String text, int colspan) {
        indent();
        html.append(getIndentation()).append("<th");
        if (colspan > 0) {
            html.append(" colspan=\"").append(colspan).append("\"");
        }
        html.append(">").append(escape(text)).append("</th>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator td(String text) {
        return td(text, -1);
    }

    public HtmlGenerator td(BigDecimal amount) {
        return td(amount.toPlainString(), -1);
    }

    public HtmlGenerator td(String text, int colspan) {
        indent();
        html.append(getIndentation()).append("<td");
        if (colspan > 0) {
            html.append(" colspan=\"").append(colspan).append("\"");
        }
        html.append(">").append(escape(text)).append("</td>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator td() {
        return td(-1);
    }

    public HtmlGenerator td(int colspan) {
        indent();
        html.append(getIndentation()).append("<td");
        if (colspan > 0) {
            html.append(" colspan=\"").append(colspan).append("\"");
        }
        html.append(">").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTd() {
        html.append(getIndentation()).append("</td>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator tfoot() {
        indent();
        html.append(getIndentation()).append("<tfoot>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTfoot() {
        html.append(getIndentation()).append("</tfoot>").append(NEW_LINE);
        unindent();
        return this;
    }

    private void indent() {
        ++indentationLevel;
    }

    private void unindent() {
        --indentationLevel;
        if (indentationLevel < 0) {
            indentationLevel = 0;
        }
    }

    private String getIndentation() {
        String indentation = INDENTATION.get(indentationLevel);
        if (indentation != null) {
            return indentation;
        } else {
            StringBuilder newIndentation = new StringBuilder(indentationLevel*TAB_AS_SPACES.length());
            for (int i = 0; i < indentationLevel; i++) {
                newIndentation.append(TAB_AS_SPACES);
            }
            return newIndentation.toString();
        }
    }

    public String asHtml() {
        return html.toString();
    }

    public String asHtml(Object... args) {
        return String.format(html.toString(), args);
    }

    public static String escape(String text) {
        if (text.contains("%")) {
            // Se crean muchos objetos String, reescribir metodo
            int pos = text.indexOf('%');
            while (pos != -1) {
                if (!(pos <= text.length() - 2 && text.charAt(pos + 1) == 's') || pos == text.length() - 1) {
                    text = text.substring(0, pos + 1) + '%' + text.substring(pos + 1);
                    pos += 2;
                } else {
                    ++pos;
                }
                pos = text.indexOf('%', pos);
            }
            return text;
        } else {
            return text;
        }
    }

}
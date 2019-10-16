package ni.org.jug.exchangerate.util;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author aalaniz
 */
public class HtmlGenerator {

    private static final String TAB_AS_SPACES = "    ";
    private static final char NEW_LINE = '\n';

    private static final String HTML5_DOCTYPE = "<!DOCTYPE html>";

    private final StringBuilder html;
    private String indentation;

    public HtmlGenerator() {
        this.html = new StringBuilder(2000);
    }

    public HtmlGenerator(String initialIndentation) {
        this();
        this.indentation = initialIndentation;
    }

    public String getIndentation() {
        return indentation;
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
        html.append(indentation).append("<head>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeHead() {
        html.append(indentation).append("</head>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator style() {
        indent();
        html.append(indentation).append("<style>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeStyle() {
        html.append(indentation).append("</style>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator body() {
        indent();
        html.append(indentation).append("<body>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeBody() {
        html.append(indentation).append("</body>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator block(String value) {
        indent();
        html.append(indentation).append(escape(value)).append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator ln() {
        html.append(NEW_LINE);
        return this;
    }

    public HtmlGenerator p() {
        indent();
        html.append(indentation).append("<p>");
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

    public HtmlGenerator href(String link, String text) {
        html.append("<a href=\"").append(escape(link)).append("\">").append(escape(text)).append("</a>");
        return this;
    }

    public HtmlGenerator div(String... cssClasses) {
        indent();
        html.append(indentation).append("<div");
        if (cssClasses != null && cssClasses.length > 0) {
            String cssClassesToHtml = Stream.of(cssClasses).collect(Collectors.joining(" ", " class=\"", "\""));
            html.append(cssClassesToHtml);
        }
        html.append(">").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeDiv() {
        html.append(indentation).append("</div>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator addPlaceholder(String placeholder) {
        html.append(placeholder).append(NEW_LINE);
        return this;
    }

    public HtmlGenerator table() {
        indent();
        html.append(indentation).append("<table>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTable() {
        html.append(indentation).append("</table>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator tr() {
        indent();
        html.append(indentation).append("<tr>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTr() {
        html.append(indentation).append("</tr>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator th(String text) {
        return th(text, -1);
    }

    public HtmlGenerator th(String text, int colspan) {
        indent();
        html.append(indentation).append("<th");
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
        html.append(indentation).append("<td");
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
        html.append(indentation).append("<td");
        if (colspan > 0) {
            html.append(" colspan=\"").append(colspan).append("\"");
        }
        html.append(">").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTd() {
        html.append(indentation).append("</td>").append(NEW_LINE);
        unindent();
        return this;
    }

    public HtmlGenerator tfoot() {
        indent();
        html.append(indentation).append("<tfoot>").append(NEW_LINE);
        return this;
    }

    public HtmlGenerator closeTfoot() {
        html.append(indentation).append("</tfoot>").append(NEW_LINE);
        unindent();
        return this;
    }

    private void indent() {
        if (indentation == null || indentation.isEmpty()) {
            indentation = TAB_AS_SPACES;
        } else {
            indentation += TAB_AS_SPACES;
        }
    }

    private void unindent() {
        if (indentation != null && !indentation.isEmpty()) {
            indentation = indentation.substring(0, indentation.length() - TAB_AS_SPACES.length());
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
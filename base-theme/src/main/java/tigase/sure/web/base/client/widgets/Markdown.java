/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tigase.sure.web.base.client.widgets;

/**
 *
 * @author andrzej
 */
public class Markdown {

        private int header;
        private int orderedList = 0;

        private boolean blockquote = false;
        private boolean paragraph = false;
        private boolean unorderedList = false;
        
        public static String parse(String data) {
                Markdown parser = new Markdown();
                String result = parser.parseInt(data);
//                System.out.println(result);
                // em and strong
                result = result.replaceAll("\\*\\*([^\n*]+)\\*\\*", "<strong>$1</strong>");
                result = result.replaceAll("\\*([^\n*]+)\\*", "<em>$1</em>");
                // code blocks
                result = result.replaceAll("``([^`]+)``","<code>$1</code>");
                // centering text
                result = result.replaceAll("%%([^%]+)%%", "<p style=\"text-align: center;\">$1</p>");
                // handling video tags
                result = result.replaceAll("\\[video:youtube\\]\\(([^\\)]+)\\)\\[([0-9]+)x([0-9]+)\\]", "<iframe width=\"$2\" height=\"$3\" object src=\"$1\"></iframe>");
                result = result.replaceAll("\\[video:youtube\\]\\(([^\\)]+)\\)", "<iframe width=\"460\" height=\"300\" object src=\"$1\"></iframe>");
                result = result.replaceAll("\\[video\\]\\(([^\\)]+)\\)", "<video controls><source src=\"$1\"/></video>");
//                System.out.println(result);
                // handling image tags                
                result = result.replaceAll("\\!\\[([^\\]]+)\\]\\(([^\\)]+)\\)\\[([0-9]+)x([0-9]+)\\]", "<img src=\"$2\" alt=\"$1\" style=\"width: $3px; height: $4px;\" />");
                result = result.replaceAll("\\!\\[\\]\\(([^\\)]+)\\)\\[([0-9]+)x([0-9]+)\\]", "<img src=\"$1\" style=\"width: $2px; height: $3px;\" />");
                result = result.replaceAll("\\!\\[([^\\]]+)\\]\\(([^\\)]+)\\)", "<img src=\"$2\" alt=\"$1\"/>");
                result = result.replaceAll("\\!\\[\\]\\(([^\\)]+)\\)", "<img src=\"$1\"/>");
//                System.out.println(result);
                // handling links
                result = result.replaceAll("\\[([^\\]]+)\\]\\(([^\\)]+)\\)", "<a href=\"$2\" target=\"_blank\" >$1</a>");
//                System.out.println(result);
                result = result.replaceAll("\\[\\]\\(([^\\)]+)\\)", "<a href=\"$1\" target=\"_blank\" >$1</a>");
//                System.out.println(result);
                return result;
        }

        private String parseInt(String dataStr) {
                StringBuilder buf = new StringBuilder();                
                
                char[] data = dataStr.toCharArray();
                int linePosition = 0;
                for (int i = 0; i < data.length; i++) {
                        char c = data[i];

                        if (linePosition == 0) {
                                while (c == '#') {
                                        header++;
                                        i++;
                                        c = data[i];
                                }
                                
                                if (header != 0) {
                                        buf.append("<h");
                                        buf.append(header);
                                        buf.append(">");
                                        linePosition++;
                                        continue;                                        
                                }

                                if (c == '>' || (blockquote && c == ' ')) {
                                        if (!blockquote) {
                                                if (paragraph) {
                                                        buf.append("</p>\n");
                                                        paragraph = false; 
                                                }
                                                buf.append("<blockquote>");
                                        }
                                        blockquote = true;
                                        i++;
                                        linePosition++;
                                        continue;                                        
                                }
                                else if (blockquote) {
                                        buf.append("</blockquote>\n");
                                        blockquote = false;
                                }

                                if (c == ' ' && (unorderedList || orderedList != 0)) {
                                        buf.append("<br/>");
                                }
                                else {
                                        if ((c == '*' || c == '+' || c == '-') && (i+1 < data.length && data[i+1] == ' ')) {
                                                if (!unorderedList) {
                                                        unorderedList = true;
                                                        buf.append("<ul>\n");
                                                } else {
                                                        buf.append("</li>\n");
                                                }
                                                buf.append("<li>");
                                                i++;
                                                linePosition++;
                                                continue;
                                        } else if (unorderedList) {
                                                buf.append("</li>\n");
                                                buf.append("</ul>\n");
                                                unorderedList = false;
                                        }

                                        if (isDigit(c)) {
                                                if (i + 1 < data.length && data[i + 1] == '.' || (i + 2 < data.length && data[i + 2] == '.' && isDigit(data[i + 1]))) {
                                                        if (orderedList == 0) {
                                                                buf.append("<ol>\n");
                                                        } else {
                                                                buf.append("</li>\n");
                                                        }
                                                        buf.append("<li>");
                                                        orderedList++;
                                                linePosition++;
                                                i++;
                                                continue;
                                                }
                                        } else if (orderedList != 0) {
                                                buf.append("</li>\n");
                                                buf.append("</ol>\n");
                                                orderedList = 0;
                                        }
                                } 
                                
                                if (header == 0 && !paragraph) {
                                        buf.append("<p>");
                                        paragraph = true;
                                }
                                
                                linePosition++;
//                                if (c == '*')
                        }
                                                
                        if (c == '\n') {
                                if (header != 0) {
                                        buf.append("</h");
                                        buf.append(header);
                                        buf.append(">");

                                        header = 0;
                                }                                
                                buf.append(c);                                                
                                
                                if (i+1 < data.length && data[i+1] == '\n') {
                                        buf.append("</p>");
                                        paragraph = false;
                                }
                                else { 
                                        // removed for sure.im to be compatible with old JS implementation
//                                        if (/*paragraph &&*/ !unorderedList && orderedList == 0)
//                                                buf.append("<br/>");
                                }
                                
                                linePosition = 0;
                        }
                        else {
                                buf.append(c);                                                
                        
                                linePosition++;                                
                        }
                }

                if (blockquote) {
                        buf.append("</blockqoute>");
                        blockquote = false;
                }

                if (unorderedList) {
                        buf.append("</li>\n</ul>");
                        unorderedList = false;
                }                
                if (orderedList != 0) {
                        buf.append("</li>\n</ol>");
                        orderedList = 0;
                }

                if (paragraph) {
                        buf.append("</p>");
                        paragraph = false;
                }                
                
                return buf.toString();
        }
                
        public static boolean isDigit(char c) {
                return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
        }

        public static final String MARKDOWN_EXAMPLE = "## Header 2\n"
                + "### Header 3\n\n"
                + "**Strong Item**\n\n"
                + "*EM item*\n\n"
                + "Simple unordered list\n\n"
                + "* Type Markdown text in the left window\n"
                + "* See the HTML in the right\n"
                + "Simple ordered list\n"
                + "1. Item 1\n"
                + "2. Item 2\n"
                + "4. Item 3\n\n"
                + "Simple text with link to [Tigase.IM](http://tigase.im) site and with simple image ![image](http://www.tigase.org/files/image/tiger-oiled-292x170.jpg)[200x150] where 200x150 are dimensions passed to img tag.\n\n"                        
                + "> The overriding design goal for Markdown's\n"
                + "> formatting syntax is to make it as readable\n"
                + "> as possible. The idea is that a\n"
                + "> Markdown-formatted document should be\n"
                + "> publishable as-is, as plain text, without\n"
                + "> looking like it's been marked up with tags\n"
                + "> or formatting instructions.\n\n"
                + "This document is written in Markdown; you can see the plain-text version on the left.  To get a feel for Markdown's syntax, type some text into the left window and watch the results in the right.  You can see a Markdown syntax guide by switching the right-hand window from *Preview* to *Syntax Guide*.\n\n"
                + "[video:youtube](http://www.youtube.com/embed/RBOdNa4Ps7o)[300x200]\n\n"
                + "Above line inserts movie with id RBOdNa4Ps7o from YouTube resized to width = 300 and height = 200\n\n"
                + "Showdown is a Javascript port of Markdown.  You can get the full [source code] by clicking on the version number at the bottom of the page.";
        
}
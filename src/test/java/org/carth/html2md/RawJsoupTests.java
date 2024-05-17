package org.carth.html2md;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class RawJsoupTests {

    @Test
    public void parseHtml() {
        String html = """
<ac:structured-macro ac:name="code" ac:schema-version="1" ac:macro-id="7f4b57a0-bea4-42ec-a4c7-48e8ff49dfd7"><ac:parameter ac:name="language">java</ac:parameter><ac:parameter ac:name="title">Title: Hello1</ac:parameter><ac:plain-text-body><![CDATA[// Your First Program

class HelloWorld {
   public static void main(String[] args) {
       System.out.println("Hello, World!");
   }
}]]></ac:plain-text-body></ac:structured-macro><ac:structured-macro ac:name="code" ac:schema-version="1" ac:macro-id="6c1d2ae5-2a81-4d2d-ad50-19298724b43b"><ac:parameter ac:name="language">java</ac:parameter><ac:parameter ac:name="title">Title: Hello2</ac:parameter><ac:parameter ac:name="collapse">true</ac:parameter><ac:plain-text-body><![CDATA[// Your First Program

class HelloWorld {
   public static void main(String[] args) {
       System.out.println("Hello, World!");
   }
}]]></ac:plain-text-body></ac:structured-macro>""";

        // Parse the HTML string
        Document doc = Jsoup.parse(html);

        Elements elements = doc.select("ac|structured-macro");

        // Filter elements by attribute
        for (Element element : elements) {

            Optional<Element> title = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("title")).findFirst();
            Optional<Element> language = element.select("ac|parameter").stream().filter(el -> el.hasAttr("ac:name") && el.attr("ac:name").equals("language")).findFirst();
            var body = element.select("ac|plain-text-body");
            Element code = element.select("ac|plain-text-body").first();
            String titlePart = title.map(Element::text).orElse("");
            String languagePart = language.map(Element::wholeText).orElse("");
            String codePart = code.wholeText();

            System.out.println(codePart);
        }
    }
}

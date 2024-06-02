package org.carth.html2md.gliffy;

import com.mxgraph.gliffy.importer.GliffyDiagramConverter;
import org.carth.html2md.utils.FileUtils;

import java.io.IOException;

public class GliffyParser {

    public static void main(String[] args) {
        try {
            String baseDir = "/Users/carth/Development/github.c4rth/html2md/files/test-migration/docs/attachments/";
            String[] files = {"test-gliffy-basic", "test-gliffy-cloud", "gliffy-diagram-application-flow"};
             for (String file: files) {
                 String gliffy = FileUtils.readFile(baseDir + file);
                 GliffyDiagramConverter converter = new GliffyDiagramConverter(gliffy);
                 System.out.println(converter.getReport());
                 String drawIo = converter.getGraphXml();
                 FileUtils.writeFile(baseDir + file + ".drawio", drawIo);
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

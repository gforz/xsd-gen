package org.wiztools.xsdgen;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.wiztools.commons.Charsets;

/**
 *
 * @author subWiz
 */
public class XsdGenMain {

    private static class ArgParse {
        @Argument(value="p", alias="prefix", description="The namespace prefix for the xsd (default is `xsd').")
        private String xsdPrefix;

        @Argument(value="o", alias="output", description="Write xsd to outfile instead of STDOUT.")
        private String xsdFile;

        @Argument(value="f", alias="force", description="Force write output XSD even if it exists.")
        private boolean isForceWrite;

        @Argument(value="e", alias="encoding", description="Output encoding (default is `UTF-8').")
        private String encoding;

        @Argument(value="h", alias="help", description="Print usage help.")
        private boolean isHelp;
    }

    public static void main(String[] arg) throws ParseException, IOException {
        ArgParse cliParser = new ArgParse();
        List<String> extras = Args.parse(cliParser, arg);
        if(extras.size() != 1
                || cliParser.isHelp) {
            Args.usage(cliParser);
            System.exit(1);
        }

        final File xmlFile = new File(extras.get(0));

        // XSD prefix
        final XsdGen xsdGen;
        if(cliParser.xsdPrefix != null) {
            xsdGen = new XsdGen(cliParser.xsdPrefix);
        }
        else {
            xsdGen = new XsdGen();
        }

        // Charset
        final Charset charset;
        if(cliParser.encoding != null) {
            charset = Charset.forName(cliParser.encoding);
        }
        else {
            charset = Charsets.UTF_8;
        }

        // Output file
        final OutputStream os;
        if(cliParser.xsdFile != null) {
            File outFile = new File(cliParser.xsdFile);
            if(outFile.exists() && !cliParser.isForceWrite) {
                System.err.println("The outfile already exists!");
                System.exit(2);
            }
            os = new FileOutputStream(outFile);
        }
        else {
            os = System.out;
        }

        // Parse & Write!
        xsdGen.parse(xmlFile).write(os, charset);
    }
}

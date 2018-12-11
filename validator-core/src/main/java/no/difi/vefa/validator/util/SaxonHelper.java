package no.difi.vefa.validator.util;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XsltCompiler;

/**
 * @author erlend
 */
public class SaxonHelper {

    public static final Processor PROCESSOR = new Processor(false);

    public static XsltCompiler newXsltCompiler() {
        return PROCESSOR.newXsltCompiler();
    }
}

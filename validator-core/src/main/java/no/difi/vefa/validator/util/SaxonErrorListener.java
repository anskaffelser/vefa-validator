package no.difi.vefa.validator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class SaxonErrorListener implements ErrorListener {

    public static SaxonErrorListener INSTANCE = new SaxonErrorListener();

    private Logger log;

    public SaxonErrorListener() {
        this(LoggerFactory.getLogger(SaxonErrorListener.class));
    }

    public SaxonErrorListener(Logger log) {
        this.log = log;
    }

    @Override
    public void warning(TransformerException exception) throws TransformerException {
        if (exception.getMessage().contains("The expression can succeed only if the supplied value is an empty sequence."))
            log.info(exception.getMessage(), exception);
        else if (exception.getMessage().contains("will never select anything"))
            log.info(exception.getMessage(), exception);
        else
            log.warn(exception.getMessage(), exception);
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        if (exception.getMessage().contains("Ambiguous rule match for"))
            log.info(exception.getMessage(), exception);
        else
            log.error(exception.getMessage(), exception);
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        log.error(exception.getMessage(), exception);
    }
}

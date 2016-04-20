package no.difi.vefa.validator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class SaxonErrorListener implements ErrorListener {

    private static Logger logger = LoggerFactory.getLogger(SaxonErrorListener.class);

    @Override
    public void warning(TransformerException exception) throws TransformerException {
        if (exception.getMessage().contains("The expression can succeed only if the supplied value is an empty sequence."))
            logger.info(exception.getMessage(), exception);
        else if (exception.getMessage().contains("will never select anything"))
            logger.info(exception.getMessage(), exception);
        else
            logger.warn(exception.getMessage(), exception);
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        if (exception.getMessage().contains("Ambiguous rule match for"))
            logger.info(exception.getMessage(), exception);
        else
            logger.error(exception.getMessage(), exception);
    }

    @Override
    public void fatalError(TransformerException exception) throws TransformerException {
        logger.error(exception.getMessage(), exception);
    }
}

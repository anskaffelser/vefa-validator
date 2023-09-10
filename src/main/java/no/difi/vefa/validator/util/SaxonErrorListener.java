package no.difi.vefa.validator.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import java.util.Objects;

@Slf4j
class SaxonErrorListener implements ErrorListener {

    @Override
    public void warning(TransformerException exception) {
        if (exception.getMessage().contains("The expression can succeed only if the supplied value is an empty sequence."))
            log.info(exception.getMessage());
        else if (exception.getMessage().contains("will never select anything"))
            log.info(exception.getMessage());
        else
            log.warn(exception.getMessage());
    }

    @Override
    public void error(TransformerException exception) throws TransformerException {
        if (exception.getMessage().contains("Ambiguous rule match for"))
            log.info(exception.getMessage(), exception);
        else
            log.error(exception.getMessage(), exception);
    }

    @Override
    public void fatalError(TransformerException exception) {
        if (Objects.nonNull(exception.getMessage()) && exception.getMessage().startsWith("Exception thrown by URIResolver"))
            log.error(exception.getCause().getMessage());
        else
            log.error(exception.getMessage(), exception);
    }
}

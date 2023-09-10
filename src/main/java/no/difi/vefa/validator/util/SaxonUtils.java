package no.difi.vefa.validator.util;

import net.sf.saxon.s9api.Message;
import org.slf4j.LoggerFactory;

import javax.xml.transform.ErrorListener;
import java.util.function.Consumer;

public interface SaxonUtils {

    ErrorListener ERROR_LISTENER = new SaxonErrorListener();

    Consumer<Message> MESSAGE_HANDLER = FunctionUtil.load(() -> {
        final var log = LoggerFactory.getLogger(SaxonUtils.class);

        return message -> {
            if (message.isTerminate()) {
                log.warn("{} - {}", message.getErrorCode(), message.getContent().getStringValue());
            } else {
                log.debug("{} - {}", message.getErrorCode(), message.getContent().getStringValue());
            }
        };
    });
}

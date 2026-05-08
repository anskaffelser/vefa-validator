package no.difi.vefa.validator.util;

import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.MessageListener2;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import javax.xml.transform.SourceLocator;

@Slf4j
public class SaxonMessageListener implements MessageListener2 {

    public static final MessageListener2 INSTANCE = new SaxonMessageListener();

    @Override
    public void message(XdmNode content, QName errorCode, boolean terminate, SourceLocator locator) {
        if (terminate)
            log.warn("{} - {}", errorCode, content.getStringValue());
        else
            log.debug("{} - {}", errorCode, content.getStringValue());
    }
}

package no.difi.vefa.validator.api;

import no.difi.xsd.vefa.validator._1.AssertionType;

public interface FlagFilterer {

    void filterFlag(AssertionType assertionType);

}

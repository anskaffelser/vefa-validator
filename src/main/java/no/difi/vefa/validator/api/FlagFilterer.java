package no.difi.vefa.validator.api;

import no.difi.xsd.vefa.validator._1.AssertionType;

@FunctionalInterface
public interface FlagFilterer {

    FlagFilterer DEFAULT = at -> {
        // No action.
    };

    void filterFlag(AssertionType assertionType);

}

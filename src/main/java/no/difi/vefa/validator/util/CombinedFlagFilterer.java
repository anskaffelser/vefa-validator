package no.difi.vefa.validator.util;

import no.difi.vefa.validator.api.FlagFilterer;
import no.difi.xsd.vefa.validator._1.AssertionType;

public class CombinedFlagFilterer implements FlagFilterer {

    private final FlagFilterer[] flagFilterers;

    public CombinedFlagFilterer(FlagFilterer... flagFilterers) {
        this.flagFilterers = flagFilterers;
    }

    @Override
    public void filterFlag(AssertionType assertionType) {
        for (FlagFilterer flagFilterer : flagFilterers)
            if (flagFilterer != null)
                flagFilterer.filterFlag(assertionType);
    }
}

package no.difi.vefa.validator;

import no.difi.vefa.validator.api.FlagFilterer;
import no.difi.xsd.vefa.validator._1.AssertionType;

class CombinedFlagFilterer implements FlagFilterer {

    private FlagFilterer[] flagFilterers;

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

package no.difi.vefa.validator.util;

import no.difi.vefa.validator.api.FlagFilterer;
import no.difi.xsd.vefa.validator._1.AssertionType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CombinedFlagFilterer implements FlagFilterer {

    private final List<FlagFilterer> flagFilterers;

    public CombinedFlagFilterer(FlagFilterer... flagFilterers) {
        this.flagFilterers = Arrays.stream(flagFilterers)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void filterFlag(AssertionType assertionType) {
        for (FlagFilterer flagFilterer : flagFilterers)
            flagFilterer.filterFlag(assertionType);
    }
}

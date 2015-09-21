package no.difi.vefa.validator.api;

import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;

import javax.xml.bind.annotation.XmlTransient;

public class Section extends SectionType {

    @XmlTransient
    private FlagFilterer flagFilterer;

    /**
     * Initiate section.
     *
     * @param flagFilterer
     */
    public Section(FlagFilterer flagFilterer) {
        this.flagFilterer = flagFilterer;

        this.setFlag(FlagType.OK);
    }

    /**
     * Add assertion to section using identifier, description and flag.
     *
     * @param identifier Identifier used for matching.
     * @param text Description of identifier.
     * @param flagType Flag associated with identifier.
     */
    public void add(String identifier, String text, FlagType flagType) {
        AssertionType assertionType = new AssertionType();
        assertionType.setIdentifier(identifier);
        assertionType.setText(text);
        assertionType.setFlag(flagType);

        add(assertionType);
    }

    public void add(AssertionType assertionType) {
        flagFilterer.filterFlag(assertionType);

        if (assertionType.getFlag() != null) {

            if (assertionType.getFlag().compareTo(getFlag()) > 0)
                setFlag(assertionType.getFlag());

            this.getAssertion().add(assertionType);
        }
    }
}

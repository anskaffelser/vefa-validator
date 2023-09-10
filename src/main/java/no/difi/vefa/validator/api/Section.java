package no.difi.vefa.validator.api;

import jakarta.xml.bind.annotation.XmlTransient;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;

import java.util.List;

public class Section extends SectionType {

    @XmlTransient
    private final FlagFilterer flagFilterer;

    /**
     * Initiate section.
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
    public void add(String identifier, String text, String textFriendly, FlagType flagType) {
        AssertionType assertionType = new AssertionType();
        assertionType.setIdentifier(identifier);
        assertionType.setText(text);
        assertionType.setTextFriendly(textFriendly);
        assertionType.setFlag(flagType);

        add(assertionType);
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

    public void add(List<AssertionType> assertions) {
        for (AssertionType assertion : assertions)
            add(assertion);
    }

    public void add(AssertionType assertion) {
        flagFilterer.filterFlag(assertion);

        if (assertion.getTextFriendly() == null)
            assertion.setTextFriendly(assertion.getText());
        if (assertion.getLocationFriendly() == null)
            assertion.setLocationFriendly(assertion.getLocation());

        if (assertion.getFlag() != null) {
            if (assertion.getFlag().compareTo(getFlag()) > 0)
                setFlag(assertion.getFlag());

            this.getAssertion().add(assertion);
        }
    }
}

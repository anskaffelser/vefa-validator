package no.dfo.anskaffelser.vefa.validator.api;

public interface Expectation extends FlagFilterer {

    String getDescription();

    void verify(Section section);

}

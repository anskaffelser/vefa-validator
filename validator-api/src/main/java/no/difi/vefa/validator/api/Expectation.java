package no.difi.vefa.validator.api;

public interface Expectation extends FlagFilterer {

    String getDescription();

    void verify(Section section);

}

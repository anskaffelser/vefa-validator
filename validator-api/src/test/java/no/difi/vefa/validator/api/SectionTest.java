package no.difi.vefa.validator.api;

import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SectionTest {

    @Test
    public void simpleNullFlag() {
        FlagFilterer flagFilterer = Mockito.mock(FlagFilterer.class);
        Section section = new Section(flagFilterer);

        section.add("TEST", "Simple test", null);

        Assert.assertEquals(section.getAssertion().size(), 0);
        Assert.assertEquals(section.getFlag(), FlagType.OK);

        Mockito.verify(flagFilterer).filterFlag(Mockito.any(AssertionType.class));
        Mockito.verifyNoMoreInteractions(flagFilterer);
    }

    @Test
    public void simpleOkFlag() {
        FlagFilterer flagFilterer = Mockito.mock(FlagFilterer.class);
        Section section = new Section(flagFilterer);

        section.add("TEST", "Simple test", FlagType.OK);

        Assert.assertEquals(section.getAssertion().size(), 1);
        Assert.assertEquals(section.getFlag(), FlagType.OK);

        Mockito.verify(flagFilterer).filterFlag(Mockito.any(AssertionType.class));
        Mockito.verifyNoMoreInteractions(flagFilterer);
    }

    @Test
    public void simpleWarningFlag() {
        FlagFilterer flagFilterer = Mockito.mock(FlagFilterer.class);
        Section section = new Section(flagFilterer);

        section.add("TEST", "Simple test", FlagType.WARNING);

        Assert.assertEquals(section.getAssertion().size(), 1);
        Assert.assertEquals(section.getFlag(), FlagType.WARNING);

        Mockito.verify(flagFilterer).filterFlag(Mockito.any(AssertionType.class));
        Mockito.verifyNoMoreInteractions(flagFilterer);
    }
}

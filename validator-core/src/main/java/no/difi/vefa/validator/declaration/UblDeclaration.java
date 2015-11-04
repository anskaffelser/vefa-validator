package no.difi.vefa.validator.declaration;

import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.api.Expectation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.expectation.XmlExpectation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
public class UblDeclaration implements Declaration {

    private static final Pattern regexCustomizationId = Pattern.compile("<\\w*:{0,1}CustomizationID.*>(.*)</\\w*:{0,1}CustomizationID\\s*>", Pattern.MULTILINE);
    private static final Pattern regexProfileId = Pattern.compile("<\\w*:{0,1}ProfileID.*>(.*)</\\w*:{0,1}ProfileID\\s*>", Pattern.MULTILINE);

    public boolean verify(String content) throws ValidatorException {
        return content.contains("urn:oasis:names:specification:ubl:schema:xsd:");
    }

    public String detect(String content) throws ValidatorException {
        String customizationId = null;
        String profileId = null;

        Matcher matcher = regexCustomizationId.matcher(content);
        if (matcher.find())
            customizationId = matcher.group(1).trim();

        matcher = regexProfileId.matcher(content);
        if (matcher.find())
            profileId = matcher.group(1).trim();

        if (customizationId == null)
            throw new ValidatorException("Unable to detect customizationId.");
        if (profileId == null)
            throw new ValidatorException("Unable to detect profileId.");

        return String.format("%s#%s", profileId, customizationId);
    }

    @Override
    public Expectation expectations(String content) throws ValidatorException {
        return new XmlExpectation(content);
    }
}

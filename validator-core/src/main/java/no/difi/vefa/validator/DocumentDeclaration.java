package no.difi.vefa.validator;

import no.difi.vefa.validator.api.Declaration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Document declaration containing profileId and customizationId.
 */
class DocumentDeclaration implements Declaration {

    private static final Pattern regexCustomizationId = Pattern.compile("<\\w*:{0,1}CustomizationID\\s*>(.*)</\\w*:{0,1}CustomizationID\\s*>", Pattern.MULTILINE);
    private static final Pattern regexProfileId = Pattern.compile("<\\w*:{0,1}ProfileID\\s*>(.*)</\\w*:{0,1}ProfileID\\s*>", Pattern.MULTILINE);

    private String customizationId;
    private String profileId;

    DocumentDeclaration(String content) {
        Matcher matcher = regexCustomizationId.matcher(content);
        if (matcher.find())
            customizationId = matcher.group(1).trim();

        matcher = regexProfileId.matcher(content);
        if (matcher.find())
            profileId = matcher.group(1).trim();
    }

    DocumentDeclaration(String customizationId, String profileId) {
        this.customizationId = customizationId;
        this.profileId = profileId;
    }

    /**
     * CustomizationId declared in document for validation.
     *
     * @return CustomizationId
     */
    public String getCustomizationId() {
        return customizationId;
    }

    /**
     * ProfileId declared in document for validation.
     *
     * @return ProfileId
     */
    public String getProfileId() {
        return profileId;
    }

    @Override
    public String toString() {
        return String.format("%s#%s", profileId, customizationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentDeclaration that = (DocumentDeclaration) o;

        if (!customizationId.equals(that.customizationId)) return false;
        return profileId.equals(that.profileId);

    }

    @Override
    public int hashCode() {
        int result = customizationId.hashCode();
        result = 31 * result + profileId.hashCode();
        return result;
    }
}

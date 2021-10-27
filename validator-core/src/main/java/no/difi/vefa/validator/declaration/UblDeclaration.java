package no.difi.vefa.validator.declaration;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.Declaration;
import no.difi.vefa.validator.lang.ValidatorException;
import no.difi.vefa.validator.util.StreamUtils;
import org.kohsuke.MetaInfServices;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Document declaration for OASIS Universal Business Language (UBL).
 */
@Type("xml.ubl")
@MetaInfServices(Declaration.class)
public class UblDeclaration extends AbstractXmlDeclaration {

    private static final Pattern PATTERN = Pattern.compile("urn:oasis:names:specification:ubl:schema:xsd:(.+)-2::(.+)");

    private static final Gson gson = new Gson();

    private XsltExecutable xsltExecutable;

    @Inject
    private void init(Processor processor) throws ValidatorException {
        try (InputStream inputStream = getClass().getResourceAsStream("/vefa-validator/xslt/ubl-detect.xslt")) {
            xsltExecutable = processor.newXsltCompiler().compile(new StreamSource(inputStream));
        } catch (SaxonApiException | IOException e) {
            throw new ValidatorException("Unable to load detector for UBL.", e);
        }
    }

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return PATTERN.matcher(parent.get(0)).matches();
    }

    @Override
    public List<String> detect(InputStream streamContent, List<String> parent) throws ValidatorException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (InputStream is = new ByteArrayInputStream(StreamUtils.readAllAndReset(streamContent))) {
            XsltTransformer xsltTransformer = xsltExecutable.load();
            xsltTransformer.setSource(new StreamSource(is));
            xsltTransformer.setDestination(xsltExecutable.getProcessor().newSerializer(baos));
            xsltTransformer.transform();
        } catch (SaxonApiException | IOException e) {
            throw new ValidatorException("Unable to detect UBL information.", e);
        }

        return gson.fromJson(baos.toString(), List.class);
    }
}

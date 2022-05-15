package no.difi.vefa.validator.declaration;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import no.difi.vefa.validator.annotation.Type;
import no.difi.vefa.validator.api.CachedFile;
import no.difi.vefa.validator.api.DeclarationWithChildren;
import no.difi.vefa.validator.lang.ValidatorException;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Type("xml.sbdh")
public class SbdhDeclaration extends AbstractXmlDeclaration implements DeclarationWithChildren {

    private static final String NAMESPACE =
            "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader";

    @Inject
    @Named("sbdh-extractor")
    private Provider<XsltExecutable> extractor;

    @Inject
    private Processor processor;

    @Override
    public boolean verify(byte[] content, List<String> parent) {
        return parent.get(0).startsWith(NAMESPACE);
    }

    @Override
    public List<String> detect(InputStream contentStream, List<String> parent) {
        return Arrays.asList(parent.get(0), "SBDH:1.0");
    }

    @Override
    public Iterable<CachedFile> children(InputStream inputStream) throws ValidatorException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            XsltTransformer xsltTransformer = extractor.get().load();
            xsltTransformer.setSource(new StreamSource(inputStream));
            xsltTransformer.setDestination(processor.newSerializer(baos));
            xsltTransformer.transform();
            xsltTransformer.close();

            if (baos.size() <= 38)
                return Collections.emptyList();

            return Collections.singletonList(CachedFile.of(baos.toByteArray()));
        } catch (SaxonApiException e) {
            throw new ValidatorException("Unable to extract SBDH content.", e);
        }
    }
}

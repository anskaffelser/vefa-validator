package no.difi.vefa.validator.util;

import com.sun.net.httpserver.HttpServer;
import net.sf.saxon.s9api.SaxonApiException;
import no.difi.vefa.validator.api.ArtifactHolder;
import no.difi.vefa.validator.api.Document;
import no.difi.vefa.validator.api.FlagFilterer;
import no.difi.vefa.validator.api.Section;
import no.difi.vefa.validator.checker.XsdChecker;
import no.difi.vefa.validator.checker.XsdCheckerFactory;
import no.difi.vefa.validator.module.SaxonModule;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.TestSet;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

public class XmlSecurityTest {

    @DataProvider
    public Object[][] doctypes() {
        return new Object[][]{
                {"<!DOCTYPE root [<!ENTITY xxe SYSTEM '%s'>]><root>&xxe;</root>"},
                {"<!DOCTYPE root [<!ENTITY %% xxe SYSTEM '%s'>%%xxe;]><root/>"},
                {"<!DOCTYPE root SYSTEM '%s'><root/>"},
                {"<!DOCTYPE root [<!ENTITY internal 'internal'>]><root>&internal;</root>"}
        };
    }

    @Test(dataProvider = "doctypes")
    public void rejectsDoctypeWithoutExternalReads(String template) throws Exception {
        try (var probe = new ExternalResourceProbe()) {
            String xml = String.format(template, probe.uri());
            var builder = new SaxonModule().getProcessor().newDocumentBuilder();
            Assert.expectThrows(SaxonApiException.class,
                    () -> builder.build(new StreamSource(new StringReader(xml))));

            var schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                    new StreamSource(new StringReader("<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                            + "<xs:element name='root' type='xs:string'/></xs:schema>")));
            var section = new Section(Mockito.mock(FlagFilterer.class));
            new XsdChecker(schema).check(new Document(bytes(xml)), section);
            Assert.assertEquals(section.getFlag(), FlagType.FATAL);
            section.getAssertion().forEach(assertion ->
                    Assert.assertFalse(assertion.getText().contains(ExternalResourceProbe.CANARY)));

            String testSet = xml.replace("<root", "<testSet xmlns='http://difi.no/xsd/vefa/validator/1.0'")
                    .replace("</root>", "</testSet>");
            var context = JAXBHelper.context(TestSet.class);
            Assert.expectThrows(JAXBException.class,
                    () -> JAXBHelper.unmarshal(context, bytes(testSet), TestSet.class));
            Assert.assertEquals(probe.requests.get(), 0);
        }
    }

    @Test
    public void doesNotResolveStaxEntities() throws Exception {
        try (var probe = new ExternalResourceProbe()) {
            var reader = SecureXml.inputFactory().createXMLStreamReader(bytes(
                    "<!DOCTYPE root [<!ENTITY xxe SYSTEM '" + probe.uri() + "'>]><root>&xxe;</root>"));
            try {
                Assert.expectThrows(javax.xml.stream.XMLStreamException.class, () -> {
                    while (reader.hasNext())
                        reader.next();
                });
            } finally {
                reader.close();
            }
            Assert.assertEquals(probe.requests.get(), 0);
        }
    }

    @DataProvider
    public Object[][] resourceFunctions() {
        return new Object[][]{{"document('%s')"}, {"unparsed-text('%s')"}, {"collection('%s')"}};
    }

    @Test(dataProvider = "resourceFunctions")
    public void blocksXsltNetworkReads(String expression) throws Exception {
        try (var probe = new ExternalResourceProbe()) {
            var processor = new SaxonModule().getProcessor();
            String xslt = "<xsl:stylesheet version='2.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>"
                    + "<xsl:template match='/'><xsl:value-of select=\"" + String.format(expression, probe.uri())
                    + "\"/></xsl:template></xsl:stylesheet>";
            var transformer = processor.newXsltCompiler()
                    .compile(new StreamSource(new StringReader(xslt))).load();
            transformer.setSource(new StreamSource(new StringReader("<root/>")));
            transformer.setDestination(processor.newSerializer(new java.io.ByteArrayOutputStream()));
            Assert.expectThrows(SaxonApiException.class, transformer::transform);
            Assert.assertEquals(probe.requests.get(), 0);
        }
    }

    @Test
    public void rejectsFileEntities() throws Exception {
        var file = Files.createTempFile("validator-xxe-", ".txt");
        try {
            Files.writeString(file, ExternalResourceProbe.CANARY);
            String xml = "<!DOCTYPE root [<!ENTITY xxe SYSTEM '" + file.toUri() + "'>]><root>&xxe;</root>";
            Assert.expectThrows(SaxonApiException.class, () -> new SaxonModule().getProcessor()
                    .newDocumentBuilder().build(new StreamSource(new StringReader(xml))));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void doesNotProcessXInclude() throws Exception {
        try (var probe = new ExternalResourceProbe()) {
            String xml = "<root xmlns:xi='http://www.w3.org/2001/XInclude'>"
                    + "<xi:include href='" + probe.uri() + "' parse='text'/></root>";
            var node = new SaxonModule().getProcessor().newDocumentBuilder()
                    .build(new StreamSource(new StringReader(xml)));
            Assert.assertFalse(node.getStringValue().contains(ExternalResourceProbe.CANARY));
            Assert.assertEquals(probe.requests.get(), 0);
        }
    }

    @Test
    public void preservesHolderLocalSchemaIncludes() throws Exception {
        var holder = Mockito.mock(ArtifactHolder.class);
        Mockito.when(holder.getInputStream("schemas/main.xsd")).thenAnswer(invocation -> bytes(
                "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                        + "<xs:include schemaLocation='types.xsd'/><xs:element name='root' type='LocalType'/>"
                        + "</xs:schema>"));
        Mockito.when(holder.get("schemas/types.xsd")).thenReturn((
                "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
                        + "<xs:simpleType name='LocalType'><xs:restriction base='xs:string'/></xs:simpleType>"
                        + "</xs:schema>").getBytes(StandardCharsets.UTF_8));
        var section = new Section(Mockito.mock(FlagFilterer.class));
        new XsdCheckerFactory().prepare(holder, "schemas/main.xsd")
                .check(new Document(bytes("<root>valid</root>")), section);
        Assert.assertEquals(section.getFlag(), FlagType.OK);
        Assert.assertTrue(section.getAssertion().isEmpty());
    }

    private static ByteArrayInputStream bytes(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    private static final class ExternalResourceProbe implements AutoCloseable {
        private static final String CANARY = "EXTERNAL_RESOURCE_CANARY";
        private final AtomicInteger requests = new AtomicInteger();
        private final HttpServer server;

        private ExternalResourceProbe() throws java.io.IOException {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", exchange -> {
                requests.incrementAndGet();
                byte[] body = CANARY.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(body);
                } finally {
                    exchange.close();
                }
            });
            server.start();
        }

        private String uri() {
            return "http://127.0.0.1:" + server.getAddress().getPort() + "/external";
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }
}

package no.difi.vefa.validator.build.util;

import no.difi.asic.*;
import no.difi.vefa.validator.build.Cli;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author erlend
 */
public class AsicArchiver extends SimpleFileVisitor<Path> {

    private static final AsicWriterFactory ASIC_WRITER_FACTORY = AsicWriterFactory.newFactory(SignatureMethod.CAdES);

    private static final SignatureHelper SIGNATURE_HELPER =
            new SignatureHelper(Cli.class.getResourceAsStream("/keystore-self-signed.jks"), "changeit", null, "changeit");

    private AsicWriter asicWriter;

    private Path directory;

    public static void archive(Path target, Path directory) throws IOException {
        AsicWriter asicWriter = ASIC_WRITER_FACTORY.newContainer(target);
        Files.walkFileTree(directory.toAbsolutePath(), new AsicArchiver(asicWriter, directory.toAbsolutePath()));
        asicWriter.sign(SIGNATURE_HELPER);
    }

    public AsicArchiver(AsicWriter asicWriter, Path directory) {
        this.asicWriter = asicWriter;
        this.directory = directory;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String path = file.toString().substring(directory.toString().length() + 1).replace("\\", "/");
        asicWriter.add(file, path, MimeType.XML);
        return super.visitFile(file, attrs);
    }
}

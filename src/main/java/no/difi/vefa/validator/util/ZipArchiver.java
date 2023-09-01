package no.difi.vefa.validator.util;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author erlend
 */
public interface ZipArchiver {

    static void archive(Path target, Path directory) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(target);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            Files.walkFileTree(directory.toAbsolutePath(), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String path = file.toString().substring(directory.toString().length() + 1).replace("\\", "/");

                    zipOutputStream.putNextEntry(new ZipEntry(path));
                    try (InputStream inputStream = Files.newInputStream(file)) {
                        ByteStreams.copy(inputStream, zipOutputStream);
                    }
                    zipOutputStream.closeEntry();

                    return super.visitFile(file, attrs);
                }
            });
        }
    }
}

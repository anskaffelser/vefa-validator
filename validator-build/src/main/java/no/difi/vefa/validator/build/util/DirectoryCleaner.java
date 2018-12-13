package no.difi.vefa.validator.build.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author erlend
 */
public class DirectoryCleaner extends SimpleFileVisitor<Path> {

    private Path path;

    public static void clean(Path path) throws IOException {
        Files.walkFileTree(path, new DirectoryCleaner(path));
    }

    public DirectoryCleaner(Path path) {
        this.path = path;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (!dir.equals(path))
            Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
}

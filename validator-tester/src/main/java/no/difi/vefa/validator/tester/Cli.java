package no.difi.vefa.validator.tester;

import no.difi.vefa.validator.api.Validation;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Cli {

    public static void main(String... args) throws Exception {
        System.exit(perform(args));
    }

    public static int perform(String... args) throws Exception {
        Options options = new Options();
        options.addOption("a", "artifacts", true, "Artifacts");
        options.addOption("x", "exitcode", false, "Status in exit code");

        CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(options, args);

        List<Path> testFolders = new ArrayList<>();
        for (String arg : cmd.getArgs())
            testFolders.add(Paths.get(arg));

        String artifacts = cmd.getOptionValue("a", "https://anskaffelser.dev/repo/validator/current/");
        List<Validation> validations;
        if (artifacts.startsWith("http"))
            validations = Tester.perform(URI.create(artifacts), testFolders);
        else
            validations = Tester.perform(Paths.get(artifacts), testFolders);

        int result = 0;
        if (cmd.hasOption("x"))
            for (Validation validation : validations)
                if (validation.getReport().getFlag().compareTo(FlagType.EXPECTED) > 0)
                    result = Math.max(result, validation.getReport().getFlag().compareTo(FlagType.EXPECTED));

        return result;
    }
}
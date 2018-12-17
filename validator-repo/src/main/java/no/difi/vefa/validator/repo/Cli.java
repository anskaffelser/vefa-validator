package no.difi.vefa.validator.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.nio.file.Paths;

@Slf4j
public class Cli {

    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            log.info("Usage:");
            log.info("  Generate artifacts.xml in repo-folder:");
            log.info("    validator-repo --target repo-folder");
            return;
        }

        Options options = new Options();
        options.addOption("t", "target", true, "Target");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        Repo.generateArtifacts(Paths.get(cmd.getOptionValue("target")), true);
    }
}

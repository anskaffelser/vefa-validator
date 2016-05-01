package no.difi.vefa.validator;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class Cli {

    private static Logger logger = LoggerFactory.getLogger(Cli.class);

    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            logger.info("Usage:");
            logger.info("  Generate artifacts.xml in repo-folder:");
            logger.info("    validator-repo --target repo-folder");
            return;
        }

        Options options = new Options();
        options.addOption("t", "target", true, "Target");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        Repo.generateArtifacts(Paths.get(cmd.getOptionValue("target")), true);
    }
}

package no.difi.vefa.validator.build;

import no.difi.asic.SignatureHelper;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

public class Cli {

    private static Logger logger = LoggerFactory.getLogger(Cli.class);

    public static void main(String... args) throws Exception {
        Options options = new Options();
        options.addOption("t", "test", false, "Run tests");
        options.addOption("s", "site", false, "Create site");
        options.addOption("b", "build", true, "Build identifier");
        options.addOption("n", "name", true, "Name");
        options.addOption("w", "weight", true, "Weight");
        options.addOption(Option.builder("ksf").desc("Keystore file").hasArg(true).build());
        options.addOption(Option.builder("ksp").desc("Keystore password").hasArg(true).build());
        options.addOption(Option.builder("pkp").desc("Private key password").hasArg(true).build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        for (String arg : cmd.getArgs()) {
            Builder builder = new Builder(Paths.get(arg));

            SignatureHelper signatureHelper = null;
            if (cmd.hasOption("ksf")) {
                logger.info("Signing information detected.");
                signatureHelper = new SignatureHelper(
                        new File(cmd.getOptionValue("ksf")),
                        cmd.getOptionValue("ksp"),
                        cmd.getOptionValue("pkp"));
            }

            builder.build(
                    cmd.getOptionValue("name", "rules"),
                    cmd.getOptionValue("build", UUID.randomUUID().toString()),
                    Long.parseLong(cmd.getOptionValue("weight", "0")),
                    signatureHelper
            );

            if (cmd.hasOption("test"))
                builder.test();

            if (cmd.hasOption("site"))
                builder.site();
        }
    }
}

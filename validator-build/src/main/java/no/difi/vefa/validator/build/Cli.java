package no.difi.vefa.validator.build;

import no.difi.asic.SignatureHelper;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.build.task.SiteTask;
import no.difi.vefa.validator.build.task.TestTask;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

public class Cli {

    private static Logger logger = LoggerFactory.getLogger(Cli.class);

    public static void main(String... args) throws Exception {
        System.exit(perform(args));
    }

    public static int perform(String... args) throws Exception {
        Options options = new Options();
        options.addOption("t", "test", false, "Run tests");
        options.addOption("s", "site", false, "Create site");
        options.addOption("b", "build", true, "Build identifier");
        options.addOption("n", "name", true, "Name");
        options.addOption("w", "weight", true, "Weight");
        options.addOption("x", "exitcode", false, "Status in exit code");
        options.addOption(Option.builder("ksf").desc("Keystore file").hasArg(true).build());
        options.addOption(Option.builder("ksp").desc("Keystore password").hasArg(true).build());
        options.addOption(Option.builder("pkp").desc("Private key password").hasArg(true).build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        int result = 0;

        for (String arg : cmd.getArgs()) {
            SignatureHelper signatureHelper = null;
            if (cmd.hasOption("ksf")) {
                logger.info("Signing information detected.");
                signatureHelper = new SignatureHelper(
                        new File(cmd.getOptionValue("ksf")),
                        cmd.getOptionValue("ksp"),
                        cmd.getOptionValue("pkp"));
            }

            Build build = new Build(Paths.get(arg));
            build.setSetting("name", cmd.getOptionValue("name", "rules"));
            build.setSetting("build", cmd.getOptionValue("build", UUID.randomUUID().toString()));
            build.setSetting("weight", cmd.getOptionValue("weight", "0"));

            new Builder().build(build, signatureHelper);

            if (cmd.hasOption("test")) {
                TestTask testTask = new TestTask(build);
                testTask.perform();
                testTask.close();
            }

            if (cmd.hasOption("site"))
                new SiteTask().build(build);

            if (cmd.hasOption("x"))
                for (Validation validation : build.getTestValidations())
                    if (validation.getReport().getFlag().compareTo(FlagType.EXPECTED) > 0)
                        result = Math.max(result, validation.getReport().getFlag().compareTo(FlagType.EXPECTED));
        }

        return result;
    }
}

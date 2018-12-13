package no.difi.vefa.validator.build;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.build.Build;
import no.difi.vefa.validator.build.module.BuildModule;
import no.difi.vefa.validator.build.module.SchematronModule;
import no.difi.vefa.validator.module.SaxonModule;
import no.difi.vefa.validator.tester.Tester;
import no.difi.xsd.vefa.validator._1.FlagType;
import org.apache.commons.cli.*;

import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class Cli {

    @Inject
    private Provider<Builder> builder;

    public static void main(String... args) throws Exception {
        System.exit(getInjector()
                .getInstance(Cli.class)
                .perform(args));
    }

    protected static Injector getInjector() {
        return Guice.createInjector(new SaxonModule(), new BuildModule(), new SchematronModule());
    }

    public int perform(String... args) throws Exception {
        Options options = new Options();
        options.addOption("c", "config", true, "Config file");
        options.addOption("t", "test", false, "Run tests");
        options.addOption("b", "build", true, "Build identifier");
        options.addOption("n", "name", true, "Name");
        options.addOption("w", "weight", true, "Weight");
        options.addOption("x", "exitcode", false, "Status in exit code");
        options.addOption("p", "profile", true, "Buildconfig profile");
        options.addOption("a", "source", true, "Source folder");
        options.addOption("s", "site", true, "Create site - DEPRECATED");
        options.addOption(Option.builder("target").desc("Target folder").hasArg(true).build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        int result = 0;

        for (String arg : cmd.getArgs()) {
            Build build = new Build(Paths.get(arg),
                    cmd.getOptionValue("source", ""),
                    cmd.getOptionValue("target", cmd.hasOption("profile") ? String.format("target-%s", cmd.getOptionValue("profile")) : "target"));
            build.setSetting("config", cmd.getOptionValue("config", cmd.hasOption("profile") ? String.format("buildconfig-%s.xml", cmd.getOptionValue("profile")) : "buildconfig.xml"));
            build.setSetting("name", cmd.getOptionValue("name", "rules"));
            build.setSetting("build", cmd.getOptionValue("build", UUID.randomUUID().toString()));
            build.setSetting("weight", cmd.getOptionValue("weight", "0"));

            builder.get().build(build);

            if (cmd.hasOption("test"))
                Tester.perform(build);

            if (cmd.hasOption("x"))
                for (Validation validation : build.getTestValidations())
                    if (validation.getReport().getFlag().compareTo(FlagType.EXPECTED) > 0)
                        result = Math.max(result, validation.getReport().getFlag().compareTo(FlagType.EXPECTED));
        }

        return result;
    }
}
